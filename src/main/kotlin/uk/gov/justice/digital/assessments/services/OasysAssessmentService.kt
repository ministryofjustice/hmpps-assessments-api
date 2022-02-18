package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.EpisodeOasysAnswerDto
import uk.gov.justice.digital.assessments.api.EpisodeOasysAnswersDto
import uk.gov.justice.digital.assessments.api.OasysAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import uk.gov.justice.digital.assessments.services.dto.OasysAnswers
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException

@Service
class OasysAssessmentService(
  private val subjectRepository: SubjectRepository,
  private val questionService: QuestionService
) {

  fun getLatestEpisodeOfTypeForSubjectWithCrn(
    assessmentSchemaCode: AssessmentSchemaCode,
    crn: String
  ): OasysAssessmentEpisodeDto {
    val subjectEntity = (
      subjectRepository.findByCrn(crn)
        ?: throw EntityNotFoundException("Subject for crn $crn not found")
      )
    val latestInProgressOrCompleteEpisode = subjectEntity.getCurrentAssessment()?.getLatestInProgessOrCompleteEpisodeOfType(assessmentSchemaCode)
      ?: throw EntityNotFoundException("Closed Episode for Subject for crn $crn not found for type $assessmentSchemaCode ")

    var oasysAnswers = OasysAnswers.from(
      latestInProgressOrCompleteEpisode,
      object : OasysAnswers.Companion.MappingProvider {
        override fun getAllQuestions(): QuestionSchemaEntities =
          questionService.getAllSectionQuestionsForQuestions(latestInProgressOrCompleteEpisode.answers?.keys?.toList() ?: emptyList())

        override fun getTableQuestions(tableCode: String): QuestionSchemaEntities =
          questionService.getAllGroupQuestionsByGroupCode(tableCode)
      }
    )
    return OasysAssessmentEpisodeDto.from(latestInProgressOrCompleteEpisode, oasysAnswers.toEpisodeOasysAnswers())
  }

  private fun OasysAnswers.toEpisodeOasysAnswers(): EpisodeOasysAnswersDto {
    val answers = this.map { EpisodeOasysAnswerDto(it.questionCode, it.answer) }
    return EpisodeOasysAnswersDto(answers)
  }
}
