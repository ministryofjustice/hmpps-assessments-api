package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AnswerSchemaDto
import uk.gov.justice.digital.assessments.api.AssessmentAnswersDto
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.AssessmentSubjectDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDateTime
import java.util.UUID
import javax.transaction.Transactional

@Service
class AssessmentService(
  private val assessmentRepository: AssessmentRepository,
  private val subjectRepository: SubjectRepository,
  private val questionService: QuestionService,
  private val episodeService: EpisodeService,
  private val courtCaseClient: CourtCaseRestClient,
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient,
  private val offenderService: OffenderService
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    const val courtSource = "COURT"
    const val deliusSource = "DELIUS"
  }

  fun createNewAssessment(newAssessment: CreateAssessmentDto): AssessmentDto {
    if (newAssessment.isDelius()) {
      return createFromDelius(newAssessment.deliusEventId, newAssessment.crn, newAssessment.oasysAssessmentType)
    }
    if (newAssessment.isCourtCase()) {
      return createFromCourtCase(newAssessment.courtCode!!, newAssessment.caseNumber!!, newAssessment.oasysAssessmentType)
    }
    throw IllegalStateException("Empty create assessment request")
  }

  @Transactional
  fun createNewEpisode(assessmentUuid: UUID, reason: String, oasysAssessmentType: OasysAssessmentType): AssessmentEpisodeDto {
    val assessment = getAssessmentByUuid(assessmentUuid)
    val episode = createPrepopulatedEpisode(assessment, reason, oasysAssessmentType = oasysAssessmentType)
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
          ?: throw IllegalStateException("Question Code not found for UUID ${episodeAnswer.key}")
        val answerSchema = matchAnswers(episodeAnswer, question)
        if (answerSchema.isNotEmpty()) {
          answers[questionCode] = AnswerSchemaDto.from(answerSchema)
        }
      }
    }
    return answers
  }

  private fun createFromDelius(eventId: Long?, crn: String?, oasysAssessmentType: OasysAssessmentType?): AssessmentDto {
    if (eventId == null || crn.isNullOrEmpty() || oasysAssessmentType == null) {
      throw IllegalStateException("Unable to create OASys Assessment with assessment type: $oasysAssessmentType, eventId: $eventId, crn: $crn")
    }
    val existingSubject = subjectRepository.findBySourceAndSourceIdAndCrn(deliusSource, eventId.toString(), crn)
    if (existingSubject != null) {
      log.info("Existing assessment ${existingSubject.assessment?.assessmentUuid} found for delius event id: $eventId, crn: $crn")
      return AssessmentDto.from(existingSubject.assessment)
    }
    val offender = offenderService.getOffender(crn)
    val oasysOffenderPk = assessmentUpdateRestClient.createOasysOffender(crn = crn, deliusEvent = eventId)
    val oasysSetPK = oasysOffenderPk?.let { assessmentUpdateRestClient.createAssessment(it, oasysAssessmentType) }
    return createDeliusAssessmentWithPrepopulatedEpisode(
      crn,
      offender,
      oasysOffenderPk,
      oasysSetPK,
      eventId,
      oasysAssessmentType
    )
  }

  private fun createFromCourtCase(
    courtCode: String,
    caseNumber: String,
    oasysAssessmentType: OasysAssessmentType
  ): AssessmentDto {

    val sourceId = courtSourceId(courtCode, caseNumber)
    val existingSubject = subjectRepository.findBySourceAndSourceId(courtSource, sourceId)
    if (existingSubject != null) {
      log.info("Existing assessment ${existingSubject.assessment?.assessmentUuid} found for court $courtCode, case $caseNumber")
      return AssessmentDto.from(existingSubject.assessment)
    }
    val courtCase = courtCaseClient.getCourtCase(courtCode, caseNumber)
      ?: throw EntityNotFoundException("No court case found for $courtCode, $caseNumber")

    val oasysOffenderPk = courtCase.crn?.let { assessmentUpdateRestClient.createOasysOffender(crn = it) }
    val oasysSetPK = oasysOffenderPk?.let { assessmentUpdateRestClient.createAssessment(it, oasysAssessmentType) }
    return createCourtAssessmentWithPrepopulatedEpisode(
      sourceId,
      courtCase,
      oasysOffenderPk,
      oasysSetPK,
      courtCode,
      caseNumber,
      oasysAssessmentType
    )
  }

  private fun matchAnswers(
    episodeAnswer: Map.Entry<UUID, AnswerEntity>,
    question: QuestionSchemaEntity
  ): Set<AnswerSchemaEntity> {
    val answerSchemas = question.answerSchemaEntities
    return episodeAnswer.value.answers.map { answer ->
      answer.items.map { item ->
        answerSchemas.firstOrNull { answerSchema ->
          item.equals(answerSchema.value)
        }
          ?: throw IllegalStateException("Answer Code not found for question ${question.questionSchemaUuid} answer value $item")
      }
    }.flatten().toSet()
  }

  fun getEpisode(episodeUuid: UUID, assessmentUuid: UUID): AssessmentEpisodeEntity {
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
    sourceId: String,
    courtCase: CourtCase,
    assessment: AssessmentEntity,
    oasysOffenderPk: Long?
  ): SubjectEntity {
    return SubjectEntity(
      source = courtSource,
      sourceId = sourceId,
      name = courtCase.defendantName,
      oasysOffenderPk = oasysOffenderPk,
      pnc = courtCase.pnc,
      crn = courtCase.crn,
      dateOfBirth = courtCase.defendantDob,
      createdDate = assessment.createdDate,
      assessment = assessment
    )
  }

  private fun createCourtAssessmentWithPrepopulatedEpisode(
    sourceId: String,
    courtCase: CourtCase,
    oasysOffenderPk: Long?,
    oasysSetPK: Long?,
    courtCode: String,
    caseNumber: String,
    oasysAssessmentType: OasysAssessmentType
  ): AssessmentDto {
    val assessment = AssessmentEntity(createdDate = LocalDateTime.now())
    val subject = subjectFromCourtCase(sourceId, courtCase, assessment, oasysOffenderPk)
    assessment.addSubject(subject)
    createPrepopulatedEpisode(assessment, "Court Request", oasysSetPK, oasysAssessmentType)
    val newAssessment = AssessmentDto.from(assessmentRepository.save(assessment))
    log.info("New assessment ${assessment.assessmentUuid} created for court $courtCode, case $caseNumber")
    return newAssessment
  }

  private fun createDeliusAssessmentWithPrepopulatedEpisode(
    crn: String,
    offender: OffenderDto,
    oasysOffenderPk: Long?,
    oasysSetPK: Long?,
    eventId: Long?,
    oasysAssessmentType: OasysAssessmentType
  ): AssessmentDto {
    val assessment = AssessmentEntity(createdDate = LocalDateTime.now())
    val subject = SubjectEntity(
      source = deliusSource,
      sourceId = eventId.toString(),
      name = "${offender.firstName} ${offender.surname}",
      oasysOffenderPk = oasysOffenderPk,
      pnc = offender.pncNumber,
      crn = crn,
      dateOfBirth = offender.dateOfBirth,
      createdDate = assessment.createdDate,
      assessment = assessment
    )
    assessment.addSubject(subject)
    createPrepopulatedEpisode(assessment, "", oasysSetPK, oasysAssessmentType)
    val newAssessment = AssessmentDto.from(assessmentRepository.save(assessment))
    log.info("New assessment ${assessment.assessmentUuid} created for Delius event ID: $eventId, CRN: $crn")
    return newAssessment
  }

  private fun createPrepopulatedEpisode(
    assessment: AssessmentEntity,
    reason: String,
    oasysSetPK: Long? = null,
    oasysAssessmentType: OasysAssessmentType
  ): AssessmentEpisodeEntity {
    val episode = assessment.newEpisode(reason, oasysSetPk = oasysSetPK, oasysAssessmentType = oasysAssessmentType)
    episodeService.prepopulate(episode)
    log.info("New episode created for assessment ${assessment.assessmentUuid}")
    return episode
  }

  private fun courtSourceId(courtCode: String?, caseNumber: String?): String {
    return "$courtCode|$caseNumber"
  }
}
