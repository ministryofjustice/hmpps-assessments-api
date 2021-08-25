package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Tests")
class AssessmentServiceTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val subjectRepository: SubjectRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val courtCaseRestClient: CourtCaseRestClient = mockk()
  private val episodeService: EpisodeService = mockk()
  private val offenderService: OffenderService = mockk()
  private val oasysAssessmentUpdateService: OasysAssessmentUpdateService = mockk()

  private val assessmentsService = AssessmentService(
    assessmentRepository,
    subjectRepository,
    questionService,
    episodeService,
    courtCaseRestClient,
    oasysAssessmentUpdateService,
    offenderService
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val assessmentSchemaCode = AssessmentSchemaCode.ROSH

  private val episodeId1 = 1L
  private val episodeId2 = 2L
  private val episodeId3 = 3L

  private val questionSchemaUuid1 = UUID.randomUUID()
  private val questionSchemaUuid2 = UUID.randomUUID()
  private val questionSchemaUuid3 = UUID.randomUUID()
  private val questionCode1 = "Q1"
  private val questionCode2 = "Q2"
  private val questionCode3 = "question_code_3"
  private val answer1Uuid = UUID.randomUUID()
  private val answer2Uuid = UUID.randomUUID()
  private val answer3Uuid = UUID.randomUUID()

  @Nested
  @DisplayName("episodes")
  inner class CreatingEpisode {
    @Test
    fun `create new episode`() {
      val assessment: AssessmentEntity = mockk()
      every { assessment.assessmentUuid } returns assessmentUuid
      every { assessment.assessmentId } returns 0
      every {
        assessment.newEpisode(
          "Change of Circs",
          assessmentSchemaCode = assessmentSchemaCode
        )
      } returns AssessmentEpisodeEntity(
        episodeId = episodeId1,
        assessment = assessment,
        createdDate = LocalDateTime.now(),
        assessmentSchemaCode = AssessmentSchemaCode.ROSH

      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      every { episodeService.prepopulate(any()) } returnsArgument 0

      val episodeDto = assessmentsService.createNewEpisode(assessmentUuid, "Change of Circs", assessmentSchemaCode)

      assertThat(episodeDto.assessmentUuid).isEqualTo(assessmentUuid)
      assertThat(episodeDto.episodeId).isEqualTo(episodeId1)
    }

    @Test
    fun `fetch all episodes for an assessment`() {
      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            changeReason = "Change of Circs 1",
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH

          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            changeReason = "Change of Circs 2",
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH

          )
        )
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      val episodeDtos = assessmentsService.getAssessmentEpisodes(assessmentUuid)
      assertThat(episodeDtos).hasSize(2)
    }

    @Test
    fun `throw exception if assessment does not exist`() {

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

      assertThatThrownBy { assessmentsService.getAssessmentEpisodes(assessmentUuid) }
        .isInstanceOf(EntityNotFoundException::class.java)
        .hasMessage("Assessment $assessmentUuid not found")
    }

    @Test
    fun `get latest assessment episode`() {
      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            changeReason = "Change of Circs 1",
            createdDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().minusDays(1),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            changeReason = "Change of Circs 2",
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          )
        )
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      val episodeDto = assessmentsService.getCurrentAssessmentEpisode(assessmentUuid)
      assertThat(episodeDto.episodeId).isEqualTo(episodeId2)
    }

    @Test
    fun `get current episode throws exception if assessment does not exist`() {

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

      assertThatThrownBy { assessmentsService.getCurrentAssessmentEpisode(assessmentUuid) }
        .isInstanceOf(EntityNotFoundException::class.java)
        .hasMessage("Assessment $assessmentUuid not found")
    }

    @Test
    fun `get current episode throws exception if no current episode exists`() {

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

      assertThatThrownBy { assessmentsService.getCurrentAssessmentEpisode(assessmentUuid) }
        .isInstanceOf(EntityNotFoundException::class.java)
        .hasMessage("Assessment $assessmentUuid not found")
    }
  }

  @Nested
  @DisplayName("coded answers")
  inner class CodedAnswers {
    @Test
    fun `fetch answers for all episodes`() {

      setupQuestionCodes()

      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            answers = mutableMapOf(questionCode1 to AnswerEntity.from("YES")),
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            answers = mutableMapOf(questionCode2 to AnswerEntity.from("NO")),
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          )
        )
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      val result = assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)

      assertThat(result.assessmentUuid).isEqualTo(assessmentUuid)
      assertThat(result.answers["Q1"]?.first()?.answerSchemaUuid).isEqualTo(answer1Uuid)
      assertThat(result.answers["Q2"]?.first()?.answerSchemaUuid).isEqualTo(answer3Uuid)
    }

    @Test
    fun `overwrite older episode answers with newer episode answers`() {
      setupQuestionCodes()

      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            endDate = LocalDateTime.of(2020, 10, 1, 9, 0, 0),
            answers = mutableMapOf(
              questionCode1 to AnswerEntity.from("YES")
            ),
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId3,
            endDate = LocalDateTime.of(2020, 10, 2, 10, 0, 0),
            answers = mutableMapOf(
              questionCode2 to AnswerEntity.from("MAYBE")
            ),
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            endDate = LocalDateTime.of(2020, 10, 2, 9, 0, 0),
            answers = mutableMapOf(
              questionCode2 to AnswerEntity.from("NO")
            ),
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          ),
        )
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      val result = assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)
      assertThat(result.answers["Q1"]?.first()?.answerSchemaUuid).isEqualTo(answer1Uuid)
      assertThat(result.answers["Q2"]?.first()?.answerSchemaUuid).isEqualTo(answer2Uuid)
    }

    @Test
    fun `overwrite older episode answers latest episode answers`() {
      setupQuestionCodes()
      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            endDate = LocalDateTime.of(2020, 10, 1, 9, 0, 0),
            answers = mutableMapOf(
              questionCode1 to AnswerEntity.from("YES")
            ),
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId3,
            endDate = null,
            answers = mutableMapOf(
              questionCode2 to AnswerEntity.from("NO")
            ),
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            endDate = LocalDateTime.of(2020, 10, 2, 9, 0, 0),
            answers = mutableMapOf(
              questionCode2 to AnswerEntity.from("MAYBE")
            ),
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          ),
        )
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      val result = assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)
      assertThat(result.answers["Q1"]?.first()?.answerSchemaUuid).isEqualTo(answer1Uuid)
      assertThat(result.answers["Q2"]?.first()?.answerSchemaUuid).isEqualTo(answer3Uuid)
    }

    @Test
    fun `only fetch coded answers`() {
      setupQuestionCodes()

      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            answers = mutableMapOf(
              questionCode1 to AnswerEntity.from("YES"),
              questionCode3 to AnswerEntity.from("free text")
            ),
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          )
        )
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      val result = assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)

      assertThat(result.assessmentUuid).isEqualTo(assessmentUuid)
      assertThat(result.answers["Q1"]?.first()?.answerSchemaUuid).isEqualTo(answer1Uuid)
      assertThat(result.answers).doesNotContainKey("Q2")
    }

    @Test
    fun `throw exception when answer code lookup fails`() {
      setupQuestionCodes()

      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            answers = mutableMapOf(
              questionCode1 to AnswerEntity.from("NO")
            ),
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          )
        )
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      assertThatThrownBy { assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid) }
        .isInstanceOf(IllegalStateException::class.java)
        .hasMessage("Answer Code not found for question $questionSchemaUuid1 answer value NO")
    }
  }

  private fun setupQuestionCodes() {
    val dummy = AnswerSchemaGroupEntity(answerSchemaId = 99)

    val yes =
      AnswerSchemaEntity(answerSchemaId = 1, answerSchemaUuid = answer1Uuid, value = "YES", answerSchemaGroup = dummy)
    val maybe =
      AnswerSchemaEntity(answerSchemaId = 2, answerSchemaUuid = answer2Uuid, value = "MAYBE", answerSchemaGroup = dummy)
    val no =
      AnswerSchemaEntity(answerSchemaId = 3, answerSchemaUuid = answer3Uuid, value = "NO", answerSchemaGroup = dummy)

    val group1 = AnswerSchemaGroupEntity(answerSchemaId = 1, answerSchemaEntities = listOf(yes))
    val group2 = AnswerSchemaGroupEntity(answerSchemaId = 2, answerSchemaEntities = listOf(maybe, no))

    every { questionService.getAllQuestions() } returns QuestionSchemaEntities(
      listOf(
        QuestionSchemaEntity(
          questionSchemaId = 1,
          questionSchemaUuid = questionSchemaUuid1,
          questionCode = questionCode1,
          answerSchemaGroup = group1
        ),
        QuestionSchemaEntity(
          questionSchemaId = 2,
          questionSchemaUuid = questionSchemaUuid2,
          questionCode = questionCode2,
          answerSchemaGroup = group2
        ),
        QuestionSchemaEntity(
          questionSchemaId = 3,
          questionSchemaUuid = questionSchemaUuid3,
          questionCode = questionCode3
        )
      )
    )
  }
}
