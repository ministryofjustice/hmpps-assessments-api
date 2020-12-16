package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.*
import uk.gov.justice.digital.assessments.jpa.entities.*
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import java.time.LocalDateTime
import java.util.*
import javax.transaction.Transactional

@Service
class AssessmentService(
        private val assessmentRepository: AssessmentRepository,
        private val subjectRepository: SubjectRepository,
        private val questionService: QuestionService,
        private val courtCaseClient: CourtCaseRestClient
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
        val courtSource = "COURT"
    }

    fun createNewAssessment(newAssessment: CreateAssessmentDto): AssessmentDto {
        if (newAssessment.isSupervision()) {
            return createFromSupervision(newAssessment.supervisionId!!)
        }
        if (newAssessment.isCourtCase()) {
            return createFromCourtCase(newAssessment.courtCode!!, newAssessment.caseNumber!!)
        }

        throw IllegalStateException("Empty create assessment request")
    }

    private fun createFromSupervision(supervisionId: String): AssessmentDto {
        val existingAssessment = assessmentRepository.findBySupervisionId(supervisionId)

        if (existingAssessment != null) {
            log.info("Existing assessment found for supervision $supervisionId")
            return AssessmentDto.from(existingAssessment)
        }

        val newAssessment = assessmentRepository.save(AssessmentEntity(supervisionId = supervisionId, createdDate = LocalDateTime.now()))
        log.info("New assessment created for supervision $supervisionId")
        return AssessmentDto.from(newAssessment)
    }

    private fun createFromCourtCase(courtCode: String, caseNumber: String): AssessmentDto {
        // do we have a subject associated with this case?
        val sourceId = courtSourceId(courtCode, caseNumber)
        val existingSubject = subjectRepository.findBySourceAndSourceId(courtSource, sourceId)

        // yes, so return the assessment
        if (existingSubject != null) {
            log.info("Existing assessment found for court $courtCode, case $caseNumber")
            return AssessmentDto.from(existingSubject.assessment!!)
        }

        // no, so fetch subject details from court case service
        val courtCase = courtCaseClient.getCourtCase(courtCode, caseNumber)
                ?: throw EntityNotFoundException("No court case found for $courtCode, $caseNumber")

        // create assessment
        val assessment = AssessmentEntity(createdDate = LocalDateTime.now())
        val subject = subjectFromCourtCase(sourceId, courtCase, assessment)
        assessment.addSubject(subject)
        assessment.newEpisode("Court Request")
        val newAssessment = AssessmentDto.from(assessmentRepository.save(assessment))
        log.info("New assessment created for court $courtCode, case $caseNumber")
        return newAssessment
    }

    @Transactional
    fun createNewEpisode(assessmentUuid: UUID, reason: String): AssessmentEpisodeDto? {
        val assessment = getAssessmentByUuid(assessmentUuid)
        val episode = assessment.newEpisode(reason)
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

    fun getCurrentAssessmentCodedAnswers(assessmentUuid: UUID) : AssessmentAnswersDto {
        val questionCodes: Map<UUID, String?> =  questionService.getAllQuestions().map { it.questionSchemaUuid to it.questionCode }.toMap()
        val answerSchemas =  questionService.getAllAnswers()
        val assessment = getAssessmentByUuid(assessmentUuid)
        val answers: MutableMap<String, Set<AnswerSchemaDto>> = mapAssessmentQuestionAndAnswerCodes(assessment, questionCodes, answerSchemas)
        return AssessmentAnswersDto(assessmentUuid, answers)
    }

    private fun mapAssessmentQuestionAndAnswerCodes(assessment: AssessmentEntity, questionCodes: Map<UUID, String?>, answerSchemas: List<AnswerSchemaEntity>): MutableMap<String, Set<AnswerSchemaDto>>{
        val answers: MutableMap<String, Set<AnswerSchemaDto>> = mutableMapOf()

        assessment.episodes.sortedWith(compareBy(nullsLast()) { it.endDate }).forEach { episode ->
            if (episode.answers != null) {
                episode.answers!!.forEach { episodeAnswer ->
                    val questionCode = questionCodes[episodeAnswer.key]
                            ?: throw IllegalStateException("Question Code not found for UUID ${episodeAnswer.key}")
                    val answerSchema = matchAnswers(episodeAnswer, answerSchemas)
                    if (answerSchema.isNotEmpty()) {
                        answers[questionCode] = AnswerSchemaDto.from(answerSchema)
                    }
                }
            }
        }
        return answers
    }

    private fun matchAnswers(episodeAnswer: Map.Entry<UUID, AnswerEntity>, answerSchemas: List<AnswerSchemaEntity>): Set<AnswerSchemaEntity> {
        return episodeAnswer.value.answers.map {
            answerSchemas.firstOrNull { answerSchema ->
                answerSchema.answerSchemaUuid == episodeAnswer.value.answers.keys.first()
            } ?: throw IllegalStateException("Answer Code not found for UUID ${it.key}")
        }.toSet()
    }

    @Transactional
    fun updateEpisode(assessmentUuid: UUID, episodeUuid: UUID, updatedEpisodeAnswers: UpdateAssessmentEpisodeDto): AssessmentEpisodeDto {
        val episode = getEpisode(episodeUuid, assessmentUuid)
        return updateEpisode(episode, updatedEpisodeAnswers)
    }

    @Transactional
    fun updateCurrentEpisode(assessmentUuid: UUID, updatedEpisodeAnswers: UpdateAssessmentEpisodeDto): AssessmentEpisodeDto {
        val episode = getCurrentEpsiode(assessmentUuid)
        return updateEpisode(episode, updatedEpisodeAnswers)
    }

    private fun updateEpisode(episode: AssessmentEpisodeEntity, updatedEpisodeAnswers: UpdateAssessmentEpisodeDto): AssessmentEpisodeDto {
        if (episode.isClosed()) throw UpdateClosedEpisodeException("Cannot update closed Episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid}")

        for (updatedAnswer in updatedEpisodeAnswers.answers) {
            val currentQuestionAnswer = episode.answers?.get(updatedAnswer.key)

            if (currentQuestionAnswer == null) {
                episode.answers?.put(updatedAnswer.key, AnswerEntity(updatedAnswer.value.freeTextAnswer, updatedAnswer.value.answers))
            } else {
                currentQuestionAnswer.freeTextAnswer = updatedAnswer.value.freeTextAnswer
                currentQuestionAnswer.answers = updatedAnswer.value.answers
            }
        }
        log.info("Updated episode ${episode.episodeUuid} with ${updatedEpisodeAnswers.answers.size} answer(s) for assessment ${episode.assessment?.assessmentUuid}")
        return AssessmentEpisodeDto.from(episode)
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

    private fun subjectFromCourtCase(sourceId: String, courtCase: CourtCase, assessment: AssessmentEntity): SubjectEntity {
        return SubjectEntity(
                source = courtSource,
                sourceId = sourceId,
                name = courtCase.defendantName,
                pnc = courtCase.pnc,
                crn = courtCase.crn,
                dateOfBirth = courtCase.defendantDob,
                createdDate = assessment.createdDate,
                assessment = assessment
        )
    }

    private fun courtSourceId(courtCode: String, caseNumber: String): String {
        return "$courtCode|$caseNumber"
    }

}