package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.api.AnswerSchemaDto
import uk.gov.justice.digital.assessments.api.AssessmentAnswersDto
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.AssessmentSubjectDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.OffenceEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDateTime
import java.util.UUID

@Service
class AssessmentService(
  private val assessmentRepository: AssessmentRepository,
  private val subjectRepository: SubjectRepository,
  private val questionService: QuestionService,
  private val episodeService: EpisodeService,
  private val courtCaseClient: CourtCaseRestClient,
  private val oasysAssessmentUpdateService: OasysAssessmentUpdateService,
  private val offenderService: OffenderService
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    const val courtSource = "COURT"
    const val deliusSource = "DELIUS"
  }

  @Transactional("assessmentsTransactionManager")
  fun createNewAssessment(newAssessment: CreateAssessmentDto): AssessmentDto {
    if (newAssessment.isDelius()) {
      return createFromDelius(
        newAssessment.deliusEventId,
        newAssessment.crn,
        newAssessment.assessmentSchemaCode
      )
    }
    if (newAssessment.isCourtCase()) {
      return createFromCourtCase(
        newAssessment.courtCode!!,
        newAssessment.caseNumber!!,
        newAssessment.assessmentSchemaCode
      )
    }
    throw IllegalStateException("Empty create assessment request")
  }

  @Transactional("assessmentsTransactionManager")
  fun createNewEpisode(
    assessmentUuid: UUID,
    eventId: Long,
    reason: String,
    assessmentSchemaCode: AssessmentSchemaCode
  ): AssessmentEpisodeDto {
    val assessment = getAssessmentByUuid(assessmentUuid)
    val crn = assessment.subject?.crn
      ?: throw EntityNotFoundException("No CRN found for subject for assessment $assessmentUuid")
    val episode = createPrepopulatedEpisode(
      assessment,
      reason,
      assessmentSchemaCode = assessmentSchemaCode,
      crn = crn,
      source = deliusSource,
      eventId = eventId.toString()
    )
    log.info("New episode created for assessment $assessmentUuid")
    return AssessmentEpisodeDto.from(episode)
  }

  fun getAssessmentSubject(assessmentUuid: UUID): AssessmentSubjectDto {
    val assessment = getAssessmentByUuid(assessmentUuid)
    return AssessmentSubjectDto.from(assessment.subject)
      ?: throw EntityNotFoundException("No subject found for $assessmentUuid")
  }

  fun getAssessmentEpisodes(assessmentUuid: UUID): Collection<AssessmentEpisodeDto>? {
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
    val answers: MutableMap<String, Collection<AnswerSchemaDto>> =
      mapAssessmentQuestionAndAnswerCodes(assessment, questions)
    return AssessmentAnswersDto(assessmentUuid, answers)
  }

  private fun mapAssessmentQuestionAndAnswerCodes(
    assessment: AssessmentEntity,
    questions: QuestionSchemaEntities
  ): MutableMap<String, Collection<AnswerSchemaDto>> {
    val answers: MutableMap<String, Collection<AnswerSchemaDto>> = mutableMapOf()

    assessment.episodes.sortedWith(compareBy(nullsLast()) { it.endDate }).forEach {
      val episodeAnswers = mapAssessmentQuestionAndAnswerCodes(it, questions)
      answers += episodeAnswers
    }

    return answers
  }

  private fun mapAssessmentQuestionAndAnswerCodes(
    episode: AssessmentEpisodeEntity,
    questions: QuestionSchemaEntities
  ): MutableMap<String, Collection<AnswerSchemaDto>> {
    val answers: MutableMap<String, Collection<AnswerSchemaDto>> = mutableMapOf()

    episode.answers?.forEach { episodeAnswer ->
      val question = questions[episodeAnswer.key]
        ?: throw IllegalStateException("Question not found for UUID ${episodeAnswer.key}")

      if (question.answerSchemaGroup != null) {
        val questionCode = question.questionCode
        val answerSchema = matchAnswers(episodeAnswer, question)
        if (answerSchema.isNotEmpty()) {
          answers[questionCode] = AnswerSchemaDto.from(answerSchema)
        }
      }
    }
    return answers
  }

  private fun createFromDelius(
    eventId: Long?,
    crn: String?,
    assessmentSchemaCode: AssessmentSchemaCode?
  ): AssessmentDto {
    if (eventId == null || crn.isNullOrEmpty() || assessmentSchemaCode == null) {
      throw IllegalStateException("Unable to create OASys Assessment with assessment type: $assessmentSchemaCode, eventId: $eventId, crn: $crn")
    }
    val arnAssessment = getOrCreateAssessment(crn, eventId)
    val (oasysOffenderPk, oasysSetPK) = oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
      crn = crn,
      deliusEventId = eventId,
      assessmentSchemaCode = assessmentSchemaCode
    )
    subjectRepository.save(arnAssessment.subject?.copy(oasysOffenderPk = oasysOffenderPk))
    createPrepopulatedEpisode(
      arnAssessment,
      "",
      oasysSetPK,
      assessmentSchemaCode,
      crn,
      deliusSource,
      eventId.toString()
    )
    return AssessmentDto.from(arnAssessment)
  }

  private fun getOrCreateAssessment(crn: String, eventId: Long): AssessmentEntity {
    val existingSubject = subjectRepository.findByCrn(crn)
    return if (existingSubject != null) {
      log.info("Existing assessment ${existingSubject.getCurrentAssessment()?.assessmentUuid} found for delius event id: $eventId, crn: $crn")
      existingSubject.getCurrentAssessment()
        ?: throw EntityNotFoundException("Subject $existingSubject doesn't belong to any assessment")
    } else {
      val offender = offenderService.getOffender(crn)
      createDeliusAssessment(
        crn,
        offender,
        eventId
      )
    }
  }

  private fun createFromCourtCase(
    courtCode: String,
    caseNumber: String,
    assessmentSchemaCode: AssessmentSchemaCode,
  ): AssessmentDto {
    val courtCase = courtCaseClient.getCourtCase(courtCode, caseNumber)
      ?: throw EntityNotFoundException("No court case found for $courtCode, $caseNumber")
    val crn = courtCase.crn ?: throw EntityNotFoundException("No CRN found for $courtCode, $caseNumber")
    val existingSubject = subjectRepository.findByCrn(crn)
    if (existingSubject != null) {
      log.info("Existing assessment ${existingSubject.getCurrentAssessment()?.assessmentUuid} found for court $courtCode, case $caseNumber")
      return AssessmentDto.from(existingSubject.getCurrentAssessment())
    }

    val (oasysOffenderPk, oasysSetPK) = oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
      crn = courtCase.crn,
      assessmentSchemaCode = assessmentSchemaCode
    )

    return createCourtAssessmentWithPrepopulatedEpisode(
      courtCase,
      oasysOffenderPk,
      oasysSetPK,
      courtCode,
      caseNumber,
      assessmentSchemaCode,
      crn
    )
  }

  private fun matchAnswers(
    episodeAnswer: Map.Entry<String, List<String>>,
    question: QuestionSchemaEntity
  ): Set<AnswerSchemaEntity> {
    val answerSchemas = question.answerSchemaEntities
    return episodeAnswer.value.map { answer ->
      answerSchemas.firstOrNull { answerSchema ->
        answer == answerSchema.value
      }
        ?: throw IllegalStateException("Answer Code not found for question ${question.questionSchemaUuid} answer value $answer")
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
    return assessmentRepository.findByAssessmentUuid(assessmentUuid)
      ?: throw EntityNotFoundException("Assessment $assessmentUuid not found")
  }

  private fun subjectFromCourtCase(
    courtCase: CourtCase,
    oasysOffenderPk: Long?
  ): SubjectEntity {
    return SubjectEntity(
      name = courtCase.defendantName,
      oasysOffenderPk = oasysOffenderPk,
      pnc = courtCase.pnc,
      crn = courtCase.crn ?: "",
      dateOfBirth = courtCase.defendantDob,
      createdDate = LocalDateTime.now(),
    )
  }

  private fun createCourtAssessmentWithPrepopulatedEpisode(
    courtCase: CourtCase,
    oasysOffenderPk: Long?,
    oasysSetPK: Long?,
    courtCode: String,
    caseNumber: String,
    assessmentSchemaCode: AssessmentSchemaCode,
    crn: String,
  ): AssessmentDto {
    val sourceId = courtSourceId(courtCode, caseNumber)
    val subject = subjectFromCourtCase(courtCase, oasysOffenderPk)
    subjectRepository.save(subject)
    val assessment = AssessmentEntity(
      createdDate = LocalDateTime.now(),
      subject = subject
    )
    createPrepopulatedEpisode(assessment, "Court Request", oasysSetPK, assessmentSchemaCode, crn, courtSource, sourceId)
    val newAssessment = AssessmentDto.from(assessmentRepository.save(assessment))
    log.info("New assessment ${assessment.assessmentUuid} created for court $courtCode, case $caseNumber")
    return newAssessment
  }

  private fun createDeliusAssessment(
    crn: String,
    offender: OffenderDto,
    eventId: Long,
  ): AssessmentEntity {
    val subject = SubjectEntity(
      name = "${offender.firstName} ${offender.surname}",
      pnc = offender.pncNumber,
      crn = crn,
      dateOfBirth = offender.dateOfBirth,
      createdDate = LocalDateTime.now(),
    )
    val subjectEntity = subjectRepository.save(subject)
    val assessment = AssessmentEntity(subject = subjectEntity)
    log.info("About to save assessment: ${assessment.assessmentUuid}")
    val newAssessment = assessmentRepository.save(assessment)
    log.info("New assessment ${assessment.assessmentUuid} created for Delius event ID: $eventId, CRN: $crn")
    return newAssessment
  }

  private fun createPrepopulatedEpisode(
    assessment: AssessmentEntity,
    reason: String,
    oasysSetPK: Long? = null,
    assessmentSchemaCode: AssessmentSchemaCode,
    crn: String,
    source: String,
    eventId: String
  ): AssessmentEpisodeEntity {
    var offence: OffenceDto? = null
    if (source == deliusSource) {
      offence = getEpisodeOffence(crn, eventId.toLong())
    }
    val episode = assessment.newEpisode(
      reason,
      oasysSetPk = oasysSetPK,
      assessmentSchemaCode = assessmentSchemaCode,
      offence = OffenceEntity(
        source = source,
        sourceId = eventId,
        offenceCode = offence?.offenceCode,
        codeDescription = offence?.codeDescription,
        offenceSubCode = offence?.offenceSubCode,
        subCodeDescription = offence?.subCodeDescription,
        sentenceDate = offence?.sentenceDate
      )
    )
    episodeService.prepopulate(episode)
    log.info("New episode episode with id:${episode.episodeId} and uuid:${episode.episodeUuid} created for assessment ${assessment.assessmentUuid}")
    return episode
  }

  private fun getEpisodeOffence(crn: String, eventId: Long): OffenceDto {
    return offenderService.getOffence(crn, eventId)
  }

  private fun courtSourceId(courtCode: String?, caseNumber: String?): String {
    return "$courtCode|$caseNumber"
  }
}
