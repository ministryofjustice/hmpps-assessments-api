package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.api.AssessmentSubjectDto
import uk.gov.justice.digital.assessments.api.DeliusEventType
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.api.answers.AnswerDto
import uk.gov.justice.digital.assessments.api.answers.AssessmentAnswersDto
import uk.gov.justice.digital.assessments.api.assessments.AssessmentDto
import uk.gov.justice.digital.assessments.api.assessments.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.assessments.CreateAssessmentDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.OffenceEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.audit.AuditType
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.services.dto.ExternalSource
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.utils.AssessmentUtils
import uk.gov.justice.digital.assessments.utils.RequestData
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID

@Service
class AssessmentService(
  private val assessmentRepository: AssessmentRepository,
  private val subjectRepository: SubjectRepository,
  private val authorService: AuthorService,
  private val questionService: QuestionService,
  private val episodeService: EpisodeService,
  private val offenderService: OffenderService,
  private val auditService: AuditService,
  private val telemetryService: TelemetryService,
  private val clock: Clock
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Transactional("assessmentsTransactionManager")
  fun createNewAssessment(newAssessment: CreateAssessmentDto): AssessmentDto {
    if (newAssessment.isDelius()) {
      return createFromDelius(
        newAssessment.deliusEventId,
        newAssessment.crn,
        newAssessment.assessmentSchemaCode,
        newAssessment.deliusEventType
      )
    }
    throw IllegalStateException("Empty create assessment request")
  }

  @Transactional("assessmentsTransactionManager")
  fun createNewEpisode(
    assessmentUuid: UUID,
    eventId: Long,
    reason: String,
    assessmentType: AssessmentType,
    eventType: DeliusEventType
  ): AssessmentEpisodeDto {
    log.info("Entered createNewEpisode with uuid: $assessmentUuid and type: $assessmentType")
    val assessment = getAssessmentByUuid(assessmentUuid)
    val subject = assessment.subject
      ?: throw EntityNotFoundException("No CRN found for subject for assessment $assessmentUuid")

    offenderService.validateUserAccess(subject.crn)
    val offence = offenderService.getOffence(eventType, subject.crn, eventId)
    val offender = offenderService.getCommunityOffender(subject.crn)
    val episode = createPrePopulatedEpisode(
      assessment,
      reason,
      assessmentType = assessmentType,
      source = ExternalSource.DELIUS.name,
      eventId = eventId.toString(),
      offence = offence,
      subject = subject,
      offender
    )
    log.info("New episode created for assessment $assessmentUuid")
    return AssessmentEpisodeDto.from(episode)
  }

  fun getAssessmentSubject(assessmentUuid: UUID): AssessmentSubjectDto {
    val assessment = getAssessmentByUuid(assessmentUuid)
    return AssessmentSubjectDto.from(assessment.subject, clock)
      ?: throw EntityNotFoundException("No subject found for $assessmentUuid")
  }

  fun getAssessmentEpisodes(assessmentUuid: UUID): Collection<AssessmentEpisodeDto> {
    val assessment = getAssessmentByUuid(assessmentUuid)
    log.info("Found ${assessment.episodes.size} for assessment $assessmentUuid")
    return AssessmentEpisodeDto.from(assessment.episodes)
  }

  fun getCurrentAssessmentEpisode(assessmentUuid: UUID): AssessmentEpisodeDto {
    return AssessmentEpisodeDto.from(getCurrentEpisode(assessmentUuid))
  }

  fun getCurrentAssessmentCodedAnswers(assessmentUuid: UUID): AssessmentAnswersDto {
    val questions = questionService.getAllQuestions()
    val assessment = getAssessmentByUuid(assessmentUuid)
    val answers: MutableMap<String, Collection<AnswerDto>> =
      mapAssessmentQuestionAndAnswerCodes(assessment, questions)
    return AssessmentAnswersDto(assessmentUuid, answers)
  }

  private fun mapAssessmentQuestionAndAnswerCodes(
    assessment: AssessmentEntity,
    questions: QuestionSchemaEntities
  ): MutableMap<String, Collection<AnswerDto>> {
    val answers: MutableMap<String, Collection<AnswerDto>> = mutableMapOf()

    assessment.episodes.sortedWith(compareBy(nullsLast()) { it.endDate }).forEach {
      val episodeAnswers = mapAssessmentQuestionAndAnswerCodes(it, questions)
      answers += episodeAnswers
    }

    return answers
  }

  private fun mapAssessmentQuestionAndAnswerCodes(
    episode: AssessmentEpisodeEntity,
    questions: QuestionSchemaEntities
  ): MutableMap<String, Collection<AnswerDto>> {
    val answers: MutableMap<String, Collection<AnswerDto>> = mutableMapOf()

    episode.answers.forEach { episodeAnswer ->
      val question = questions[episodeAnswer.key]
        ?: throw IllegalStateException("Question not found for UUID ${episodeAnswer.key}")

      if (question.answerGroup != null) {
        val questionCode = question.questionCode
        val answerSchema = matchAnswers(episodeAnswer, question)
        if (answerSchema.isNotEmpty()) {
          answers[questionCode] = AnswerDto.from(answerSchema)
        }
      }
    }
    return answers
  }

  private fun createFromDelius(
    eventId: Long?,
    crn: String?,
    assessmentType: AssessmentType?,
    eventType: DeliusEventType
  ): AssessmentDto {
    if (eventId == null || crn.isNullOrEmpty() || assessmentType == null) {
      throw IllegalStateException("Unable to create Assessment with assessment type: $assessmentType, eventId: $eventId, crn: $crn")
    }
    offenderService.validateUserAccess(crn)
    val offender = offenderService.getCommunityOffender(crn)
    val arnAssessment = getOrCreateAssessment(crn, eventId, OffenderDto.from(offender))
    val offence = offenderService.getOffence(eventType, crn, eventId)

    val subject = subjectRepository.save(arnAssessment.subject?.copy())
    createPrePopulatedEpisode(
      arnAssessment,
      "",
      assessmentType,
      ExternalSource.DELIUS.name,
      offence.convictionId.toString(),
      offence,
      subject,
      offender
    )
    return AssessmentDto.from(arnAssessment)
  }

  fun getCurrentEpisode(crn: String): AssessmentEpisodeDto {
    log.info("Entered getCurrentEpisode{}", crn)

    val existingSubject = subjectRepository.findByCrn(crn)
    val currentEpisode = existingSubject?.getCurrentAssessment()?.getCurrentEpisode() ?: throw EntityNotFoundException("No current episode found for $crn")
    return AssessmentEpisodeDto.from(currentEpisode)
  }

  private fun getOrCreateAssessment(crn: String, eventId: Long, offender: OffenderDto): AssessmentEntity {

    val existingSubject = subjectRepository.findByCrn(crn)
    return if (existingSubject != null) {
      log.info("Existing assessment ${existingSubject.getCurrentAssessment()?.assessmentUuid} found for delius event id: $eventId, crn: $crn")
      existingSubject.getCurrentAssessment() ?: throw EntityNotFoundException("Subject $existingSubject doesn't belong to any assessment")
    } else {
      createDeliusAssessment(
        crn,
        offender,
        eventId
      )
    }
  }

  private fun matchAnswers(
    episodeAnswer: Map.Entry<String, List<Any>>,
    question: QuestionEntity
  ): Set<AnswerEntity> {
    val answerSchemas = question.answerEntities
    return episodeAnswer.value.map { answer ->
      answerSchemas.firstOrNull { answerSchema ->
        answer == answerSchema.value
      }
        ?: throw IllegalStateException("Answer Code not found for question ${question.questionUuid} answer value $answer")
    }.toSet()
  }

  fun getEpisode(assessmentUuid: UUID, episodeUuid: UUID): AssessmentEpisodeEntity {
    return getAssessmentByUuid(assessmentUuid).episodes.firstOrNull { it.episodeUuid == episodeUuid }
      ?: throw EntityNotFoundException("No Episode $episodeUuid for $assessmentUuid")
  }

  fun getCurrentEpisode(assessmentUuid: UUID): AssessmentEpisodeEntity {
    val assessment = getAssessmentByUuid(assessmentUuid)
    return assessment.getCurrentEpisode()
      ?: throw EntityNotFoundException("No current Episode for $assessmentUuid")
  }

  fun getAssessmentByUuid(assessmentUuid: UUID): AssessmentEntity {
    log.debug("Entered getAssessmentByUuid($assessmentUuid)")
    val assessment = assessmentRepository.findByAssessmentUuid(assessmentUuid)
      ?: throw EntityNotFoundException("Assessment $assessmentUuid not found")
    if (!RequestData.isClientGrantType()) {
      assessment.subject?.crn?.let { offenderService.validateUserAccess(it) }
    }
    return assessment
  }

  private fun createDeliusAssessment(
    crn: String,
    offender: OffenderDto,
    eventId: Long,
  ): AssessmentEntity {
    val subjectEntity = subjectRepository.save(
      SubjectEntity(
        name = "${offender.firstName} ${offender.surname}",
        pnc = offender.pncNumber,
        crn = crn,
        dateOfBirth = offender.dateOfBirth,
        createdDate = LocalDateTime.now(),
        gender = offender.gender?.uppercase(),
      )
    )
    val assessment = AssessmentEntity(subject = subjectEntity)
    log.info("About to save delius assessment: ${assessment.assessmentUuid}")
    val newAssessment = assessmentRepository.save(assessment)
    log.info("New assessment ${assessment.assessmentUuid} created for Delius event ID: $eventId, CRN: $crn")
    return newAssessment
  }

  private fun createPrePopulatedEpisode(
    assessment: AssessmentEntity,
    reason: String,
    assessmentType: AssessmentType,
    source: String,
    eventId: String,
    offence: OffenceDto? = null,
    subject: SubjectEntity?,
    offender: CommunityOffenderDto

  ): AssessmentEpisodeEntity {
    log.info("Entered createPrePopulatedEpisode")
    val author = authorService.getOrCreateAuthor()
    val isNewEpisode = !assessment.hasCurrentEpisode()
    log.info("isNewEpisode is $isNewEpisode")
    val episode = assessment.newEpisode(
      reason,
      assessmentType = assessmentType,
      offence = OffenceEntity(
        source = source,
        sourceId = eventId,
        offenceCode = offence?.offenceCode,
        codeDescription = offence?.codeDescription,
        offenceSubCode = offence?.offenceSubCode,
        subCodeDescription = offence?.subCodeDescription,
        sentenceDate = offence?.sentenceDate
      ),
      author
    )
    if (isNewEpisode) {
      episodeService.prePopulateEpisodeFromDelius(episode, offender)
      episodeService.prePopulateFromPreviousEpisodes(episode, assessment.episodes)
      AssessmentUtils.removeOrphanedAnswers(episode)
      auditAndLogCreateEpisode(assessment.assessmentUuid, episode, subject?.crn)
    }
    log.info("New episode episode with id:${episode.episodeId} and uuid:${episode.episodeUuid} created for assessment ${assessment.assessmentUuid}")
    return episode
  }

  private fun auditAndLogCreateEpisode(
    assessmentUuid: UUID,
    episode: AssessmentEpisodeEntity,
    crn: String?
  ) {
    auditService.createAuditEvent(
      AuditType.ARN_ASSESSMENT_CREATED,
      assessmentUuid,
      episode.episodeUuid,
      crn,
      episode.author
    )
    telemetryService.trackAssessmentEvent(
      TelemetryEventType.ASSESSMENT_CREATED,
      crn,
      episode.author,
      assessmentUuid,
      episode.episodeUuid,
      episode.assessmentType
    )
  }
}
