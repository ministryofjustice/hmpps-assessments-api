package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.OASysMappingEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.EpisodeRepository
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.ValidationErrorDto
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase
import uk.gov.justice.digital.assessments.services.dto.OasysAnswers
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Tests")
class AssessmentServiceTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val episodeRepository: EpisodeRepository = mockk()
  private val subjectRepository: SubjectRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val courtCaseRestClient: CourtCaseRestClient = mockk()
  private val episodeService: EpisodeService = mockk()
  private val offenderService: OffenderService = mockk()
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient = mockk()

  private val assessmentsService = AssessmentService(
    assessmentRepository,
    episodeRepository,
    subjectRepository,
    questionService,
    episodeService,
    courtCaseRestClient,
    assessmentUpdateRestClient,
    offenderService
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val assessmentType = AssessmentType.SHORT_FORM_PSR

  private val episodeId1 = 1L
  private val episodeId2 = 2L
  private val episodeId3 = 3L

  private val episodeUuid = UUID.randomUUID()

  private val existingQuestionUuid = UUID.randomUUID()

  private val question1Uuid = UUID.randomUUID()
  private val question2Uuid = UUID.randomUUID()
  private val question3Uuid = UUID.randomUUID()
  private val childQuestion1 = UUID.randomUUID()
  private val childQuestion2 = UUID.randomUUID()
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
      every { assessment.newEpisode("Change of Circs", assessmentType = assessmentType) } returns AssessmentEpisodeEntity(episodeId = episodeId1, assessment = assessment)
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      every { episodeService.prepopulate(any()) } returnsArgument 0

      val episodeDto = assessmentsService.createNewEpisode(assessmentUuid, "Change of Circs", assessmentType)

      assertThat(episodeDto.assessmentUuid).isEqualTo(assessmentUuid)
      assertThat(episodeDto.episodeId).isEqualTo(episodeId1)
    }

    @Test
    fun `fetch all episodes for an assessment`() {
      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(episodeId = episodeId1, changeReason = "Change of Circs 1"),
          AssessmentEpisodeEntity(episodeId = episodeId2, changeReason = "Change of Circs 2")
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
          AssessmentEpisodeEntity(episodeId = episodeId1, changeReason = "Change of Circs 1", endDate = LocalDateTime.now().minusDays(1)),
          AssessmentEpisodeEntity(episodeId = episodeId2, changeReason = "Change of Circs 2")
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
  @DisplayName("update episode")
  inner class UpdateAnswers {
    @Test
    fun `update episode throws exception if episode does not exist`() {

      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(episodeUuid = UUID.randomUUID(), episodeId = episodeId2, changeReason = "Change of Circs 2")
        )
      )

      val updatedAnswers = UpdateAssessmentEpisodeDto(mapOf())

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      assertThatThrownBy { assessmentsService.updateEpisode(assessmentUuid, episodeUuid, updatedAnswers) }
        .isInstanceOf(EntityNotFoundException::class.java)
        .hasMessage("No Episode $episodeUuid for $assessmentUuid")
    }

    @Test
    fun `add new answers to existing question for an episode`() {
      val answers = mutableMapOf(
        existingQuestionUuid to AnswerEntity("free text")
      )
      val assessment = assessmentEntity(answers)

      val newQuestionUuid = UUID.randomUUID()
      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mapOf(newQuestionUuid to listOf("trousers"))
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      every { assessmentRepository.save(any()) } returns null

      val episodeDto = assessmentsService.updateEpisode(assessmentUuid, episodeUuid, updatedAnswers)

      assertThat(episodeDto.answers).hasSize(2)
      with(episodeDto.answers[existingQuestionUuid]!!) {
        assertThat(size).isEqualTo(1)
        assertThat(first()).isEqualTo("free text")
      }

      with(episodeDto.answers[newQuestionUuid]!!) {
        assertThat(size).isEqualTo(1)
        assertThat(first()).isEqualTo("trousers")
      }
    }

    @Test
    fun `change an existing answer for an episode`() {
      val answers = mutableMapOf(
        existingQuestionUuid to AnswerEntity("free text")
      )
      val assessment = assessmentEntity(answers)

      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mapOf(existingQuestionUuid to listOf("new free text"))
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      every { assessmentRepository.save(any()) } returns null

      val episodeDto = assessmentsService.updateEpisode(assessmentUuid, episodeUuid, updatedAnswers)

      assertThat(episodeDto.answers).hasSize(1)
      with(episodeDto.answers[existingQuestionUuid]!!) {
        assertThat(size).isEqualTo(1)
        assertThat(first()).isEqualTo("new free text")
      }
    }

    @Test
    fun `remove answers for an existing question for an episode`() {
      val answers = mutableMapOf(
        existingQuestionUuid to AnswerEntity(listOf("free text", "fruit loops", "biscuits"))
      )
      val assessment = assessmentEntity(answers)

      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mapOf(existingQuestionUuid to listOf("fruit loops", "custard"))
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      every { assessmentRepository.save(any()) } returns null

      val episodeDto = assessmentsService.updateEpisode(assessmentUuid, episodeUuid, updatedAnswers)

      assertThat(episodeDto.answers).hasSize(1)
      with(episodeDto.answers[existingQuestionUuid]!!) {
        assertThat(size).isEqualTo(2)
        assertThat(this).containsAll(listOf("fruit loops", "custard"))
      }
    }

    @Test
    fun `do not update a closed episode`() {
      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeUuid = episodeUuid,
            episodeId = episodeId2,
            endDate = LocalDateTime.now().minusDays(1),
            changeReason = "Change of Circs 2",
            answers = mutableMapOf(
              existingQuestionUuid to AnswerEntity("free text")
            ),
            assessment = AssessmentEntity(assessmentUuid = assessmentUuid)
          )
        )
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      assertThatThrownBy { assessmentsService.updateEpisode(assessmentUuid, episodeUuid, UpdateAssessmentEpisodeDto(answers = emptyMap())) }
        .isInstanceOf(UpdateClosedEpisodeException::class.java)
        .hasMessage("Cannot update closed Episode $episodeUuid for assessment $assessmentUuid")
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
            answers = mutableMapOf(question1Uuid to AnswerEntity("YES"))
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            answers = mutableMapOf(question2Uuid to AnswerEntity("NO"))
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
              question1Uuid to AnswerEntity("YES")
            )
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId3,
            endDate = LocalDateTime.of(2020, 10, 2, 10, 0, 0),
            answers = mutableMapOf(
              question2Uuid to AnswerEntity("MAYBE")
            )
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            endDate = LocalDateTime.of(2020, 10, 2, 9, 0, 0),
            answers = mutableMapOf(
              question2Uuid to AnswerEntity("NO")
            )
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
              question1Uuid to AnswerEntity("YES")
            )
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId3,
            endDate = null,
            answers = mutableMapOf(
              question2Uuid to AnswerEntity("NO")
            )
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            endDate = LocalDateTime.of(2020, 10, 2, 9, 0, 0),
            answers = mutableMapOf(
              question2Uuid to AnswerEntity("MAYBE")
            )
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
              question1Uuid to AnswerEntity("YES"),
              question3Uuid to AnswerEntity("free text")
            )
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
    fun `throw exception when question code lookup fails`() {
      every { questionService.getAllQuestions() } returns QuestionSchemaEntities(
        listOf(
          QuestionSchemaEntity(questionSchemaId = 2, questionSchemaUuid = question2Uuid, answerSchemaGroup = AnswerSchemaGroupEntity(1))
        )
      )

      every { questionService.getAllAnswers() } returns listOf()

      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            answers = mutableMapOf(
              question2Uuid to AnswerEntity("YES")
            )
          )
        )
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      assertThatThrownBy {
        assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)
      }
        .isInstanceOf(IllegalStateException::class.java)
        .hasMessage("Question Code not found for UUID $question2Uuid")
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
              question1Uuid to AnswerEntity("NO")
            )
          )
        )
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      assertThatThrownBy { assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid) }
        .isInstanceOf(IllegalStateException::class.java)
        .hasMessage("Answer Code not found for question $question1Uuid answer value NO")
    }
  }

  @Nested
  @DisplayName("update episode with table answers")
  inner class TableAnswers {
    val childNameQuestion = makeQuestion(10, childQuestion1, "Name")
    val childAddressQuestion = makeQuestion(11, childQuestion2, "Address")
    val childTableQuestions = QuestionSchemaEntities(listOf(
      childNameQuestion,
      childAddressQuestion
    ))

    @BeforeEach
    fun setup() {
      every { assessmentRepository.save(any()) } returns null
      every { questionService.getAllGroupQuestions("children_at_risk") } returns childTableQuestions
    }

    @Test
    fun `add first row to table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity("some free text"),
        question2Uuid to AnswerEntity("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity("not mapped to oasys"),
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(answers)

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestion1 to listOf("child name"),
          childQuestion2 to listOf("child address"))
      )

      val episodeDto = assessmentsService.addEpisodeTableRow(assessmentUuid, episodeUuid, "children_at_risk", tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      with(episodeDto.answers[childQuestion1]!!) {
        assertThat(size).isEqualTo(1)
        assertThat(first()).isEqualTo("child name")
      }

      with(episodeDto.answers[childQuestion2]!!) {
        assertThat(size).isEqualTo(1)
        assertThat(first()).isEqualTo("child address")
      }
    }

    @Test
    fun `add second row to table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity("some free text"),
        question2Uuid to AnswerEntity("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity("not mapped to oasys"),
        childQuestion1 to AnswerEntity("child name 1"),
        childQuestion2 to AnswerEntity("child address 1")
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(answers)

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestion1 to listOf("child name 2"),
          childQuestion2 to listOf("child address 2"))
      )

      val episodeDto = assessmentsService.addEpisodeTableRow(assessmentUuid, episodeUuid, "children_at_risk", tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      with(episodeDto.answers[childQuestion1]!!) {
        assertThat(size).isEqualTo(2)
        assertThat(first()).isEqualTo("child name 1")
        assertThat(last()).isEqualTo("child name 2")
      }

      with(episodeDto.answers[childQuestion2]!!) {
        assertThat(size).isEqualTo(2)
        assertThat(first()).isEqualTo("child address 1")
        assertThat(last()).isEqualTo("child address 2")
      }
    }

    @Test
    fun `add second row with partial data to table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity("some free text"),
        question2Uuid to AnswerEntity("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity("not mapped to oasys"),
        childQuestion1 to AnswerEntity("child name 1"),
        childQuestion2 to AnswerEntity("child address 1")
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(answers)

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestion1 to listOf("child name 2"))
      )

      val episodeDto = assessmentsService.addEpisodeTableRow(assessmentUuid, episodeUuid, "children_at_risk", tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      with(episodeDto.answers[childQuestion1]!!) {
        assertThat(size).isEqualTo(2)
        assertThat(first()).isEqualTo("child name 1")
        assertThat(last()).isEqualTo("child name 2")
      }

      with(episodeDto.answers[childQuestion2]!!) {
        assertThat(size).isEqualTo(2)
        assertThat(first()).isEqualTo("child address 1")
        assertThat(last()).isEqualTo("")
      }
    }
  }

  private fun setupQuestionCodes() {
    val dummy = AnswerSchemaGroupEntity(answerSchemaId = 99)

    val yes = AnswerSchemaEntity(answerSchemaId = 1, answerSchemaUuid = answer1Uuid, value = "YES", answerSchemaGroup = dummy)
    val maybe = AnswerSchemaEntity(answerSchemaId = 2, answerSchemaUuid = answer2Uuid, value = "MAYBE", answerSchemaGroup = dummy)
    val no = AnswerSchemaEntity(answerSchemaId = 3, answerSchemaUuid = answer3Uuid, value = "NO", answerSchemaGroup = dummy)

    val group1 = AnswerSchemaGroupEntity(answerSchemaId = 1, answerSchemaEntities = listOf(yes))
    val group2 = AnswerSchemaGroupEntity(answerSchemaId = 2, answerSchemaEntities = listOf(maybe, no))

    every { questionService.getAllQuestions() } returns QuestionSchemaEntities(
      listOf(
        QuestionSchemaEntity(questionSchemaId = 1, questionSchemaUuid = question1Uuid, questionCode = "Q1", answerSchemaGroup = group1),
        QuestionSchemaEntity(questionSchemaId = 2, questionSchemaUuid = question2Uuid, questionCode = "Q2", answerSchemaGroup = group2),
        QuestionSchemaEntity(questionSchemaId = 3, questionSchemaUuid = question3Uuid)
      )
    )
  }

  private fun assessmentEntity(answers: MutableMap<UUID, AnswerEntity>): AssessmentEntity {
    return AssessmentEntity(
      assessmentId = assessmentId,
      episodes = mutableListOf(
        AssessmentEpisodeEntity(
          episodeUuid = episodeUuid,
          episodeId = episodeId2,
          changeReason = "Change of Circs 2",
          answers = answers
        )
      )
    )
  }

  private fun makeQuestion(
    questionSchemaId: Long,
    questionSchemaUuid: UUID,
    questionCode: String
  ): QuestionSchemaEntity {
    val question = QuestionSchemaEntity(
      questionSchemaId = questionSchemaId,
      questionSchemaUuid = questionSchemaUuid,
      questionCode = questionCode,
      answerType = "free text"
    )
    return question
  }

}
