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
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.OASysMappingEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.transaction.Transactional

@Service
class AssessmentService(
  private val assessmentRepository: AssessmentRepository,
  private val subjectRepository: SubjectRepository,
  private val questionService: QuestionService,
  private val episodeService: EpisodeService,
  private val courtCaseClient: CourtCaseRestClient,
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    const val courtSource = "COURT"
    val oasysDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  }

  fun createNewAssessment(newAssessment: CreateAssessmentDto): AssessmentDto {
    if (newAssessment.isSupervision()) {
      return createFromSupervision(newAssessment.supervisionId)
    }
    if (newAssessment.isCourtCase()) {
      return createFromCourtCase(newAssessment.courtCode!!, newAssessment.caseNumber!!, newAssessment.assessmentType)
    }
    throw IllegalStateException("Empty create assessment request")
  }

  @Transactional
  fun createNewEpisode(assessmentUuid: UUID, reason: String, assessmentType: AssessmentType): AssessmentEpisodeDto {
    val assessment = getAssessmentByUuid(assessmentUuid)
    val episode = createPrepopulatedEpisode(assessment, reason, assessmentType = assessmentType)
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
    return AssessmentEpisodeDto.from(getCurrentEpsiode(assessmentUuid))
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
    questions: List<QuestionSchemaEntity>
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
    questions: List<QuestionSchemaEntity>
  ): MutableMap<String, Collection<AnswerSchemaDto>> {
    val answers: MutableMap<String, Collection<AnswerSchemaDto>> = mutableMapOf()

    episode.answers?.forEach { episodeAnswer ->
      val question = questions.firstOrNull { it.questionSchemaUuid == episodeAnswer.key }
        ?: throw IllegalStateException("Question not found for UUID ${episodeAnswer.key}")

      if (question.answerSchemaGroup != null) {
        val questionCode = question?.questionCode
          ?: throw IllegalStateException("Question Code not found for UUID ${episodeAnswer.key}")
        val answerSchema = matchAnswers(episodeAnswer, question)
        if (answerSchema.isNotEmpty()) {
          answers[questionCode] = AnswerSchemaDto.from(answerSchema)
        }
      }
    }

    return answers
  }

  private fun createFromSupervision(supervisionId: String?): AssessmentDto {
    val existingAssessment = assessmentRepository.findBySupervisionId(supervisionId)

    if (existingAssessment != null) {
      log.info("Existing assessment found for supervision $supervisionId")
      return AssessmentDto.from(existingAssessment)
    }

    val newAssessment =
      assessmentRepository.save(AssessmentEntity(supervisionId = supervisionId, createdDate = LocalDateTime.now()))
    log.info("New assessment created for supervision $supervisionId")
    return AssessmentDto.from(newAssessment)
  }

  private fun createFromCourtCase(
    courtCode: String,
    caseNumber: String,
    assessmentType: AssessmentType
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
    val oasysSetPK = oasysOffenderPk?.let { assessmentUpdateRestClient.createAssessment(it, assessmentType) }
    return createCourtAssessmentWithPrepopulatedEpisode(
      sourceId,
      courtCase,
      oasysOffenderPk,
      oasysSetPK,
      courtCode,
      caseNumber,
      assessmentType
    )
  }

  private fun matchAnswers(
    episodeAnswer: Map.Entry<UUID, AnswerEntity>,
    question: QuestionSchemaEntity
  ): Set<AnswerSchemaEntity> {
    val answerSchemas = question.answerSchemaEntities
    return episodeAnswer.value.answers.map { answer ->
      answerSchemas.firstOrNull { answerSchema ->
        answerSchema.value == answer
      } ?: throw IllegalStateException("Answer Code not found for question ${question.questionSchemaUuid} answer value ${answer}")
    }.toSet()
  }

  @Transactional
  fun updateEpisode(
    assessmentUuid: UUID,
    episodeUuid: UUID,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    val episode = getEpisode(episodeUuid, assessmentUuid)
    return updateEpisode(episode, updatedEpisodeAnswers)
  }

  @Transactional
  fun updateCurrentEpisode(
    assessmentUuid: UUID,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    val episode = getCurrentEpsiode(assessmentUuid)
    return updateEpisode(episode, updatedEpisodeAnswers)
  }

  private fun updateEpisode(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    if (episode.isClosed()) throw UpdateClosedEpisodeException("Cannot update closed Episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid}")

    updateEpisodeAnswers(episode, updatedEpisodeAnswers)
    log.info("Updated episode ${episode.episodeUuid} with ${updatedEpisodeAnswers.answers.size} answer(s) for assessment ${episode.assessment?.assessmentUuid}")

    updateOASysAssessment(episode.assessment?.subject?.oasysOffenderPk, episode)
    log.info("Updated OASys ${episode.assessment?.subject?.oasysOffenderPk} with ${updatedEpisodeAnswers.answers.size} answer(s) for assessment ${episode.assessment?.assessmentUuid}")

    assessmentRepository.save(episode.assessment)
    log.info("Saved episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid}")

    return AssessmentEpisodeDto.from(episode)
  }

  private fun updateEpisodeAnswers(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ) {
    for (updatedAnswer in updatedEpisodeAnswers.answers) {
      val currentQuestionAnswer = episode.answers?.get(updatedAnswer.key)

      if (currentQuestionAnswer == null) {
        episode.answers?.put(
          updatedAnswer.key,
          AnswerEntity(updatedAnswer.value)
        )
      } else {
        currentQuestionAnswer.answers = updatedAnswer.value
      }
    }
  }

  fun updateOASysAssessment(
    offenderPk: Long?,
    episode: AssessmentEpisodeEntity
  ) {
    if (episode.assessmentType == null || episode.oasysSetPk == null || offenderPk == null) {
      log.info("Unable to update OASys Assessment with keys type: ${episode.assessmentType} oasysSet: ${episode.oasysSetPk} offenderPk: $offenderPk")
      return
    }

    val questions: Map<UUID, QuestionSchemaEntity?> =
      questionService.getAllQuestions().map { it.questionSchemaUuid to it }.toMap()

    val oasysAnswers: MutableList<OasysAnswer> = mutableListOf()

    // TODO: If we want to handle multiple mappings per question we will need to add assessment type to the mapping
    episode.answers?.forEach { episodeAnswer ->
      val question = questions[episodeAnswer.key]
      val oasysMapping = question?.oasysMappings?.toList()?.getOrNull(0)
      oasysAnswers.addAll(
        mapOasysAnswer(
          oasysMapping,
          episodeAnswer.value.answers,
          question?.answerType)
        )
    }

    assessmentUpdateRestClient.updateAssessment(offenderPk, episode.oasysSetPk!!, episode.assessmentType!!, oasysAnswers.toSet())
    log.info("Updated OASys assessment oasysSet: ${episode.oasysSetPk}")
  }

  fun mapOasysAnswer(
    oasysMapping: OASysMappingEntity?,
    answers: Collection<String>,
    answerType: String?
  ): List<OasysAnswer> {
    if (oasysMapping == null) return emptyList()

    return answers.map { it ->
      val answer = when (answerType) {
      "date" -> LocalDate.parse(it, DateTimeFormatter.ISO_DATE_TIME).format(oasysDateFormatter)
        else -> it
      }

      OasysAnswer(
        oasysMapping.sectionCode,
        oasysMapping.logicalPage,
        oasysMapping.questionCode,
        answer,
        oasysMapping.isFixed
      )
    }.toList()
  }

  private fun getEpisode(episodeUuid: UUID, assessmentUuid: UUID): AssessmentEpisodeEntity {
    return getAssessmentByUuid(assessmentUuid).episodes.firstOrNull { it.episodeUuid == episodeUuid }
      ?: throw EntityNotFoundException("No Episode $episodeUuid for $assessmentUuid")
  }

  private fun getCurrentEpsiode(assessmentUuid: UUID): AssessmentEpisodeEntity {
    val assessment = getAssessmentByUuid(assessmentUuid)
    return assessment.getCurrentEpisode()
      ?: throw EntityNotFoundException("No current Episode for $assessmentUuid")
  }

  private fun getAssessmentByUuid(assessmentUuid: UUID): AssessmentEntity {
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
    assessmentType: AssessmentType
  ): AssessmentDto {
    val assessment = AssessmentEntity(createdDate = LocalDateTime.now())
    val subject = subjectFromCourtCase(sourceId, courtCase, assessment, oasysOffenderPk)
    assessment.addSubject(subject)
    createPrepopulatedEpisode(assessment, "Court Request", oasysSetPK, assessmentType)
    val newAssessment = AssessmentDto.from(assessmentRepository.save(assessment))
    log.info("New assessment ${assessment.assessmentUuid} created for court $courtCode, case $caseNumber")
    return newAssessment
  }

  private fun createPrepopulatedEpisode(
    assessment: AssessmentEntity,
    reason: String,
    oasysSetPK: Long? = null,
    assessmentType: AssessmentType
  ): AssessmentEpisodeEntity {
    val episode = assessment.newEpisode(reason, oasysSetPk = oasysSetPK, assessmentType = assessmentType)
    episodeService.prepopulate(episode)
    log.info("New episode created for assessment ${assessment.assessmentUuid}")
    return episode
  }

  private fun courtSourceId(courtCode: String?, caseNumber: String?): String {
    return "$courtCode|$caseNumber"
  }
}
