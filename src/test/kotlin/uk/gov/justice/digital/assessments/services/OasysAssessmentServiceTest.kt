package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.EpisodeOasysAnswersDto
import uk.gov.justice.digital.assessments.api.OasysAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import java.time.LocalDate
import java.time.LocalDateTime

class OasysAssessmentServiceTest {
  private val subjectRepository: SubjectRepository = mockk()
  private val questionService: QuestionService = mockk()

  private val oasysAssessmentService = OasysAssessmentService(subjectRepository, questionService)

  @Test
  fun `return latest episode for subject`() {
    val assessmentSchemaCode = AssessmentSchemaCode.ROSH
    val crn = "X1234"
    val episode2 = AssessmentEpisodeEntity(
      episodeId = 567,
      changeReason = "Change of Circs 2",
      createdDate = LocalDateTime.now(),
      endDate = LocalDateTime.now(),
      assessmentSchemaCode = AssessmentSchemaCode.ROSH
    )
    val episode1 = AssessmentEpisodeEntity(
      episodeId = 456,
      changeReason = "Change of Circs 1",
      createdDate = LocalDateTime.now().minusDays(1),
      endDate = LocalDateTime.now().minusDays(1),
      assessmentSchemaCode = AssessmentSchemaCode.ROSH
    )
    val assessment = AssessmentEntity(
      assessmentId = 1232,
      episodes = mutableListOf(
        episode1,
        episode2
      )
    )
    val subject = SubjectEntity(
      oasysOffenderPk = 1L,
      dateOfBirth = LocalDate.of(1989, 1, 1),
      crn = crn,
      source = "DELIUS",
      sourceId = "128647",
      assessment = assessment
    )
    every { subjectRepository.findByCrn(crn) } returns subject

    every { questionService.getAllSectionQuestionsForQuestions(emptyList()) } returns QuestionSchemaEntities(emptyList())
    every { questionService.getAllGroupQuestionsByGroupCode(any()) } returns QuestionSchemaEntities(emptyList())

    val latestEpisode =
      oasysAssessmentService.getLatestEpisodeOfTypeForSubjectWithCrn(assessmentSchemaCode, crn)

    assertThat(latestEpisode).isEqualTo(OasysAssessmentEpisodeDto.from(episode2, EpisodeOasysAnswersDto()))
  }
}
