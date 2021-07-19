package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.EpisodeRepository
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import uk.gov.justice.digital.assessments.testutils.Verify
import java.time.LocalDateTime
import java.util.UUID

class AssessmentUpdateServiceTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val episodeRepository: EpisodeRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient = mockk()
  private val subjectRepository: SubjectRepository = mockk()
  private val courtCaseRestClient: CourtCaseRestClient = mockk()
  private val episodeService: EpisodeService = mockk()
  private val offenderService: OffenderService = mockk()
  private val predictorService: PredictorService = mockk()
  private val assessmentSchemaService: AssessmentSchemaService = mockk()

  private val assessmentUpdateService = AssessmentUpdateService(
    assessmentRepository,
    episodeRepository,
    questionService,
    assessmentUpdateRestClient,
    predictorService,
    assessmentSchemaService
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val episodeId2 = 2L

  private val episodeUuid = UUID.randomUUID()

  private val existingQuestionUuid = UUID.randomUUID()

  private val question1Uuid = UUID.randomUUID()
  private val question2Uuid = UUID.randomUUID()
  private val question3Uuid = UUID.randomUUID()
  private val childQuestion1 = UUID.randomUUID()
  private val childQuestion2 = UUID.randomUUID()

  @BeforeEach
  fun setup() {
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.ROSH) } returns OasysAssessmentType.SHORT_FORM_PSR
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.RSR) } returns OasysAssessmentType.SOMETHING_IN_OASYS
  }

  @Nested
  @DisplayName("update episode")
  inner class UpdateAnswers {
    @Test
    fun `update episode throws exception if episode does not exist`() {

      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeUuid = UUID.randomUUID(),
            episodeId = episodeId2,
            changeReason = "Change of Circs 2"
          )
        )
      )

      val updatedAnswers = UpdateAssessmentEpisodeDto(mapOf())

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      assertThatThrownBy { assessmentUpdateService.updateEpisode(updatedAnswers) }
        .isInstanceOf(EntityNotFoundException::class.java)
        .hasMessage("No Episode $episodeUuid for $assessmentUuid")
    }

    @Test
    fun `add new answers to existing question for an episode`() {
      val answers = mutableMapOf(
        existingQuestionUuid to AnswerEntity.from("free text")
      )
      val assessment = assessmentEntity(answers)

      val newQuestionUuid = UUID.randomUUID()
      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mapOf(newQuestionUuid to listOf("trousers"))
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      every { assessmentRepository.save(any()) } returns null

      val episodeDto = assessmentUpdateService.updateEpisode(assessment.episodes.first(), updatedAnswers)

      assertThat(episodeDto.answers).hasSize(2)
      Verify.singleAnswer(
        episodeDto.answers[existingQuestionUuid]!!,
        "free text"
      )

      Verify.singleAnswer(
        episodeDto.answers[newQuestionUuid]!!,
        "trousers"
      )
    }

    @Test
    fun `change an existing answer for an episode`() {
      val answers = mutableMapOf(
        existingQuestionUuid to AnswerEntity.from("free text")
      )
      val assessment = assessmentEntity(answers)

      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mapOf(existingQuestionUuid to listOf("new free text"))
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      every { assessmentRepository.save(any()) } returns null

      val episodeDto = assessmentUpdateService.updateEpisode(assessment.episodes.first(), updatedAnswers)

      assertThat(episodeDto.answers).hasSize(1)
      Verify.singleAnswer(
        episodeDto.answers[existingQuestionUuid]!!,
        "new free text"
      )
    }

    @Test
    fun `remove answers for an existing question for an episode`() {
      val answers = mutableMapOf(
        existingQuestionUuid to AnswerEntity.from(listOf("free text", "fruit loops", "biscuits"))
      )
      val assessment = assessmentEntity(answers)

      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mapOf(existingQuestionUuid to listOf("fruit loops", "custard"))
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      every { assessmentRepository.save(any()) } returns null

      val episodeDto = assessmentUpdateService.updateEpisode(assessment.episodes.first(), updatedAnswers)

      assertThat(episodeDto.answers).hasSize(1)
      Verify.singleAnswer(
        episodeDto.answers[existingQuestionUuid]!!,
        "fruit loops",
        "custard"
      )
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
              existingQuestionUuid to AnswerEntity.from("free text")
            ),
            assessment = AssessmentEntity(assessmentUuid = assessmentUuid)
          )
        )
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      assertThatThrownBy {
        assessmentUpdateService.updateEpisode(assessment.episodes.first(),
          UpdateAssessmentEpisodeDto(answers = emptyMap()),
        )
      }
        .isInstanceOf(UpdateClosedEpisodeException::class.java)
        .hasMessage("Cannot update closed Episode $episodeUuid for assessment $assessmentUuid")
    }
  }

  @Nested
  @DisplayName("update episode with table answers")
  inner class TableAnswers {
    private val childNameQuestion = makeQuestion(10, childQuestion1, "Name")
    private val childAddressQuestion = makeQuestion(11, childQuestion2, "Address")
    private val childTableQuestions = QuestionSchemaEntities(
      listOf(
        childNameQuestion,
        childAddressQuestion
      )
    )

    @BeforeEach
    fun setup() {
      every { assessmentRepository.save(any()) } returns null
      every { questionService.getAllGroupQuestionsByGroupCode("children_at_risk") } returns childTableQuestions
      every { questionService.getAllGroupQuestionsByGroupCode("nonsense_table") } returns QuestionSchemaEntities(
        emptyList()
      )
    }

    @Test
    fun `add first row to table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
      )
      val assessmentEntity = assessmentEntity(answers)
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestion1 to listOf("child name"),
          childQuestion2 to listOf("child address")
        )
      )

      val episodeDto =
        assessmentUpdateService.addEpisodeTableRow(assessmentEntity.episodes.first(), "children_at_risk", tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.singleAnswer(
        episodeDto.answers[childQuestion1]!!,
        "child name"
      )

      Verify.singleAnswer(
        episodeDto.answers[childQuestion2]!!,
        "child address"
      )
    }

    @Test
    fun `add first row to table - current episode`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(answers)

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestion1 to listOf("child name"),
          childQuestion2 to listOf("child address")
        )
      )

      val episodeDto =
        assessmentUpdateService.addCurrentEpisodeTableRow(assessmentUuid, "children_at_risk", tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestion1]!!,
        "child name"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestion2]!!,
        "child address"
      )
    }

    @Test
    fun `add second row to table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from("child name 1"),
        childQuestion2 to AnswerEntity.from("child address 1")
      )
      val assessmentEntity = assessmentEntity(answers)
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestion1 to listOf("child name 2"),
          childQuestion2 to listOf("child address 2")
        )
      )

      val episodeDto =
        assessmentUpdateService.addEpisodeTableRow(assessmentEntity.episodes.first(), "children_at_risk", tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestion1]!!,
        "child name 1",
        "child name 2"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestion2]!!,
        "child address 1",
        "child address 2"
      )
    }

    @Test
    fun `add second row with partial data to table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from("child name 1"),
        childQuestion2 to AnswerEntity.from("child address 1")
      )

      val assessmentEntity = assessmentEntity(answers)
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestion1 to listOf("child name 2")
        )
      )

      val episodeDto =
        assessmentUpdateService.addEpisodeTableRow(assessmentEntity.episodes.first(), "children_at_risk", tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestion1]!!,
        "child name 1",
        "child name 2"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestion2]!!,
        "child address 1",
        ""
      )
    }

    @Test
    fun `update first row of table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from(listOf("name of child 1", "child name 2")),
        childQuestion2 to AnswerEntity.from(listOf("address of child 1", "child address 2"))
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(answers)

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestion1 to listOf("child name 1"),
          childQuestion2 to listOf("child address 1")
        )
      )

      val episodeDto =
        assessmentUpdateService.updateEpisodeTableRow(assessmentUuid, episodeUuid, "children_at_risk", 0, tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestion1]!!,
        "child name 1",
        "child name 2"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestion2]!!,
        "child address 1",
        "child address 2"
      )
    }

    @Test
    fun `update last row of table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from(listOf("child name 1", "name of child 2")),
        childQuestion2 to AnswerEntity.from(listOf("child address 1", ""))
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(answers)

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestion1 to listOf("child name 2"),
          childQuestion2 to listOf("child address 2")
        )
      )

      val episodeDto =
        assessmentUpdateService.updateEpisodeTableRow(assessmentUuid, episodeUuid, "children_at_risk", 1, tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestion1]!!,
        "child name 1",
        "child name 2"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestion2]!!,
        "child address 1",
        "child address 2"
      )
    }

    @Test
    fun `update middle row of table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from(listOf("child name 1", "name of child 2", "child name 3")),
        childQuestion2 to AnswerEntity.from(listOf("child address 1", "address of child 2", "child address 3"))
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(answers)

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestion1 to listOf("child name 2"),
          childQuestion2 to listOf("child address 2")
        )
      )

      val episodeDto =
        assessmentUpdateService.updateEpisodeTableRow(assessmentUuid, episodeUuid, "children_at_risk", 1, tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestion1]!!,
        "child name 1",
        "child name 2",
        "child name 3"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestion2]!!,
        "child address 1",
        "child address 2",
        "child address 3"
      )
    }

    @Test
    fun `delete first row of table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from(listOf("child name 1", "child name 2", "child name 3")),
        childQuestion2 to AnswerEntity.from(listOf("child address 1", "child address 2", "child address 3"))
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(answers)

      val episodeDto = assessmentUpdateService.deleteEpisodeTableRow(assessmentUuid, episodeUuid, "children_at_risk", 0)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestion1]!!,
        "child name 2",
        "child name 3"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestion2]!!,
        "child address 2",
        "child address 3"
      )
    }

    @Test
    fun `delete middle row of table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from(listOf("child name 1", "name of child 2", "child name 3")),
        childQuestion2 to AnswerEntity.from(listOf("child address 1", "address of child 2", "child address 3"))
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(answers)

      val episodeDto = assessmentUpdateService.deleteEpisodeTableRow(assessmentUuid, episodeUuid, "children_at_risk", 1)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestion1]!!,
        "child name 1",
        "child name 3"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestion2]!!,
        "child address 1",
        "child address 3"
      )
    }

    @Test
    fun `delete last row of table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from(listOf("child name 1", "child name 2", "child name 3")),
        childQuestion2 to AnswerEntity.from(listOf("child address 1", "child address 2", "child address 3"))
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(answers)

      val episodeDto = assessmentUpdateService.deleteEpisodeTableRow(assessmentUuid, episodeUuid, "children_at_risk", 2)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestion1]!!,
        "child name 1",
        "child name 2"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestion2]!!,
        "child address 1",
        "child address 2"
      )
    }

    @Test
    fun `delete only row of table`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from(listOf("child name 1")),
        childQuestion2 to AnswerEntity.from(listOf("child address 1"))
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(answers)

      val episodeDto = assessmentUpdateService.deleteEpisodeTableRow(assessmentUuid, episodeUuid, "children_at_risk", 0)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.emptyAnswer(episodeDto.answers[childQuestion1]!!)
      Verify.emptyAnswer(episodeDto.answers[childQuestion2]!!)
    }

    @Test
    fun `fail on bad table name`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from(listOf("child name 1", "name of child 2", "child name 3")),
        childQuestion2 to AnswerEntity.from(listOf("child address 1", "address of child 2", "child address 3"))
      )
      val assessmentEntity = assessmentEntity(answers)
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity

      assertThatThrownBy {
        assessmentUpdateService.addEpisodeTableRow(
          assessmentEntity.episodes.first(),
          "nonsense_table",
          UpdateAssessmentEpisodeDto(emptyMap())
        )
      }
        .isInstanceOf(IllegalStateException::class.java)
        .hasMessage("No questions found for table nonsense_table")
    }

    @Test
    fun `fail update on bad index`() {
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(mutableMapOf())

      for (index in listOf(-3, -1, 0, 5)) {
        assertThatThrownBy {
          assessmentUpdateService.updateEpisodeTableRow(
            assessmentUuid,
            episodeUuid,
            "children_at_risk",
            index,
            UpdateAssessmentEpisodeDto(emptyMap())
          )
        }
          .isInstanceOf(IllegalStateException::class.java)
          .hasMessage("Bad index $index for table children_at_risk")
      }
    }

    @Test
    fun `fail delete on bad index`() {
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity(mutableMapOf())

      for (index in listOf(-3, -1, 0, 5)) {
        assertThatThrownBy {
          assessmentUpdateService.deleteEpisodeTableRow(
            assessmentUuid,
            episodeUuid,
            "children_at_risk",
            index
          )
        }
          .isInstanceOf(IllegalStateException::class.java)
          .hasMessage("Bad index $index for table children_at_risk")
      }
    }
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
    return QuestionSchemaEntity(
      questionSchemaId = questionSchemaId,
      questionSchemaUuid = questionSchemaUuid,
      questionCode = questionCode,
      answerType = "free text"
    )
  }
}
