package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.assessments.api.EpisodeOasysAnswersDto
import uk.gov.justice.digital.assessments.api.OasysAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType.ROSH
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType.RSR
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import java.time.LocalDate
import java.time.LocalDateTime

class OasysAssessmentServiceTest {
  private val subjectRepository: SubjectRepository = mockk()
  private val questionService: QuestionService = mockk()

  private val oasysAssessmentService = OasysAssessmentService(subjectRepository, questionService)

  companion object {
    @JvmStatic
    fun stateDates(): List<Arguments> {
      return listOf(
        Arguments.of(
          StateDates(
            assessmentType = ROSH,
            createdDate = LocalDateTime.now().minusWeeks(2),
            endDate = LocalDateTime.now().minusWeeks(1),
            closedDate = null
          ),
          StateDates(
            assessmentType = ROSH,
            createdDate = LocalDateTime.now().minusWeeks(1),
            endDate = LocalDateTime.now(),
            closedDate = null
          ),
        ),
        Arguments.of(
          StateDates(
            assessmentType = ROSH,
            createdDate = LocalDateTime.now().minusWeeks(1),
            endDate = null,
            closedDate = LocalDateTime.now().minusWeeks(1)
          ),
          StateDates(
            assessmentType = ROSH,
            createdDate = LocalDateTime.now().minusWeeks(2),
            endDate = LocalDateTime.now(),
            closedDate = null
          ),
        ),
        Arguments.of(
          StateDates(
            assessmentType = RSR,
            createdDate = LocalDateTime.now().minusWeeks(1),
            endDate = null,
            closedDate = null
          ),
          StateDates(
            assessmentType = ROSH,
            createdDate = LocalDateTime.now().minusWeeks(2),
            endDate = null,
            closedDate = null
          ),
        ),
      )
    }
  }

  @ParameterizedTest
  @MethodSource("stateDates")
  fun `return latest episode for subject`(stateDates1: StateDates, stateDates2: StateDates) {
    val assessmentSchemaCode = ROSH
    val crn = "X1234"
    val episode2 = AssessmentEpisodeEntity(
      episodeId = 567,
      changeReason = "Change of Circs 2",
      createdDate = stateDates2.createdDate,
      endDate = stateDates2.endDate,
      closedDate = stateDates2.closedDate,
      assessmentType = stateDates2.assessmentType,
      author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      assessment = AssessmentEntity()
    )
    val episode1 = AssessmentEpisodeEntity(
      episodeId = 456,
      changeReason = "Change of Circs 1",
      createdDate = stateDates1.createdDate,
      endDate = stateDates1.endDate,
      closedDate = stateDates1.closedDate,
      assessmentType = stateDates1.assessmentType,
      author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      assessment = AssessmentEntity()
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
      assessments = listOf(assessment)
    )
    every { subjectRepository.findByCrn(crn) } returns subject

    every { questionService.getAllSectionQuestionsForQuestions(emptyList()) } returns QuestionSchemaEntities(emptyList())
    every { questionService.getAllGroupQuestionsByGroupCode(any()) } returns QuestionSchemaEntities(emptyList())

    val latestEpisode =
      oasysAssessmentService.getLatestEpisodeOfTypeForSubjectWithCrn(assessmentSchemaCode, crn)

    assertThat(latestEpisode).isEqualTo(OasysAssessmentEpisodeDto.from(episode2, EpisodeOasysAnswersDto()))
  }

  data class StateDates(
    val assessmentType: AssessmentType,
    val createdDate: LocalDateTime,
    val endDate: LocalDateTime?,
    val closedDate: LocalDateTime?,
  )
}
