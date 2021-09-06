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
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import uk.gov.justice.digital.assessments.services.dto.AssessmentEpisodeUpdateErrors
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import uk.gov.justice.digital.assessments.testutils.Verify
import java.time.LocalDateTime
import java.util.UUID

class AssessmentUpdateServiceTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val episodeRepository: EpisodeRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val riskPredictorsService: RiskPredictorsService = mockk()
  private val assessmentSchemaService: AssessmentSchemaService = mockk()
  private val oasysAssessmentUpdateService: OasysAssessmentUpdateService = mockk()

  private val assessmentUpdateService = AssessmentUpdateService(
    assessmentRepository,
    episodeRepository,
    questionService,
    riskPredictorsService,
    oasysAssessmentUpdateService
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val episodeId2 = 2L

  private val episodeUuid = UUID.randomUUID()

  private val existingQuestionCode = "existing_question_code"

  private val questionCode1 = "question_code_1"
  private val questionCode2 = "question_code_2"
  private val questionCode3 = "question_code_3"
  private val childQuestionCode1 = "child_question_code_1"
  private val childQuestionCode2 = "child_question_code_2"

  @BeforeEach
  fun setup() {
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.ROSH) } returns OasysAssessmentType.SHORT_FORM_PSR
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.RSR) } returns OasysAssessmentType.SOMETHING_IN_OASYS
  }

  @Nested
  @DisplayName("update episode")
  inner class UpdateAnswers {
    @Test
    fun `add new answers to existing question for an episode`() {
      val answers = mutableMapOf(
        existingQuestionCode to AnswerEntity.from("free text")
      )
      val assessment = assessmentEntity(answers)

      val newQuestionCode = "new_question_code"
      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mapOf(newQuestionCode to listOf("trousers"))
      )

      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessment.episodes.first(),
          updatedAnswers.asAnswersDtos()
        )
      } returns AssessmentEpisodeUpdateErrors()
      every { assessmentRepository.save(any()) } returns null

      val episodeDto = assessmentUpdateService.updateEpisode(assessment.episodes.first(), updatedAnswers)

      assertThat(episodeDto.answers).hasSize(2)
      Verify.singleAnswer(
        episodeDto.answers[existingQuestionCode]!!,
        "free text"
      )

      Verify.singleAnswer(
        episodeDto.answers[newQuestionCode]!!,
        "trousers"
      )
    }

    @Test
    fun `change an existing answer for an episode`() {
      val answers = mutableMapOf(
        existingQuestionCode to AnswerEntity.from("free text")
      )
      val assessment = assessmentEntity(answers)

      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mapOf(existingQuestionCode to listOf("new free text"))
      )

      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessment.episodes.first(),
          updatedAnswers.asAnswersDtos()
        )
      } returns AssessmentEpisodeUpdateErrors()
      every { assessmentRepository.save(any()) } returns null

      val episodeDto = assessmentUpdateService.updateEpisode(assessment.episodes.first(), updatedAnswers)

      assertThat(episodeDto.answers).hasSize(1)
      Verify.singleAnswer(
        episodeDto.answers[existingQuestionCode]!!,
        "new free text"
      )
    }

    @Test
    fun `remove answers for an existing question for an episode`() {
      val answers = mutableMapOf(
        existingQuestionCode to AnswerEntity.from(listOf("free text", "fruit loops", "biscuits"))
      )
      val assessment = assessmentEntity(answers)

      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mapOf(existingQuestionCode to listOf("fruit loops", "custard"))
      )

      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessment.episodes.first(),
          updatedAnswers.asAnswersDtos()
        )
      } returns AssessmentEpisodeUpdateErrors()
      every { assessmentRepository.save(any()) } returns null

      val episodeDto = assessmentUpdateService.updateEpisode(assessment.episodes.first(), updatedAnswers)

      assertThat(episodeDto.answers).hasSize(1)
      Verify.singleAnswer(
        episodeDto.answers[existingQuestionCode]!!,
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
              existingQuestionCode to AnswerEntity.from("free text")
            ),
            assessment = AssessmentEntity(assessmentUuid = assessmentUuid),
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          )
        )
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      assertThatThrownBy {
        assessmentUpdateService.updateEpisode(
          assessment.episodes.first(),
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
    private val childNameQuestion = makeQuestion(10, childQuestionCode1)
    private val childAddressQuestion = makeQuestion(11, childQuestionCode2)
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
        questionCode1 to AnswerEntity.from("some free text"),
        questionCode2 to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        questionCode3 to AnswerEntity.from("not mapped to oasys"),
      )
      val assessmentEntity = assessmentEntity(answers)
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessmentEntity.episodes.first(),
          any()
        )
      } returns AssessmentEpisodeUpdateErrors()

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestionCode1 to listOf("child name"),
          childQuestionCode2 to listOf("child address")
        )
      )

      val episodeDto =
        assessmentUpdateService.addEpisodeTableRow(assessmentEntity.episodes.first(), "children_at_risk", tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.singleAnswer(
        episodeDto.answers[childQuestionCode1]!!,
        "child name"
      )

      Verify.singleAnswer(
        episodeDto.answers[childQuestionCode2]!!,
        "child address"
      )
    }

    @Test
    fun `add first row to table - current episode`() {
      val answers = mutableMapOf(
        questionCode1 to AnswerEntity.from("some free text"),
        questionCode2 to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        questionCode3 to AnswerEntity.from("not mapped to oasys"),
      )
      val assessmentEntity = assessmentEntity(answers)
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessmentEntity.episodes.first(),
          any()
        )
      } returns AssessmentEpisodeUpdateErrors()

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestionCode1 to listOf("child name"),
          childQuestionCode2 to listOf("child address")
        )
      )

      val episodeDto =
        assessmentUpdateService.addCurrentEpisodeTableRow(
          assessmentEntity.episodes.first(),
          "children_at_risk",
          tableAnswers
        )

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode1]!!,
        "child name"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode2]!!,
        "child address"
      )
    }

    @Test
    fun `add second row to table`() {
      val answers = mutableMapOf(
        questionCode1 to AnswerEntity.from("some free text"),
        questionCode2 to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        questionCode3 to AnswerEntity.from("not mapped to oasys"),
        childQuestionCode1 to AnswerEntity.from("child name 1"),
        childQuestionCode2 to AnswerEntity.from("child address 1")
      )
      val assessmentEntity = assessmentEntity(answers)
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessmentEntity.episodes.first(),
          any()
        )
      } returns AssessmentEpisodeUpdateErrors()

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestionCode1 to listOf("child name 2"),
          childQuestionCode2 to listOf("child address 2")
        )
      )

      val episodeDto =
        assessmentUpdateService.addEpisodeTableRow(assessmentEntity.episodes.first(), "children_at_risk", tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode1]!!,
        "child name 1",
        "child name 2"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode2]!!,
        "child address 1",
        "child address 2"
      )
    }

    @Test
    fun `add second row with partial data to table`() {
      val answers = mutableMapOf(
        questionCode1 to AnswerEntity.from("some free text"),
        questionCode2 to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        questionCode3 to AnswerEntity.from("not mapped to oasys"),
        childQuestionCode1 to AnswerEntity.from("child name 1"),
        childQuestionCode2 to AnswerEntity.from("child address 1")
      )

      val assessmentEntity = assessmentEntity(answers)
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessmentEntity.episodes.first(),
          any()
        )
      } returns AssessmentEpisodeUpdateErrors()

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestionCode1 to listOf("child name 2")
        )
      )

      val episodeDto =
        assessmentUpdateService.addEpisodeTableRow(assessmentEntity.episodes.first(), "children_at_risk", tableAnswers)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode1]!!,
        "child name 1",
        "child name 2"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode2]!!,
        "child address 1",
        ""
      )
    }

    @Test
    fun `update first row of table`() {
      val answers = mutableMapOf(
        questionCode1 to AnswerEntity.from("some free text"),
        questionCode2 to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        questionCode3 to AnswerEntity.from("not mapped to oasys"),
        childQuestionCode1 to AnswerEntity.from(listOf("name of child 1", "child name 2")),
        childQuestionCode2 to AnswerEntity.from(listOf("address of child 1", "child address 2"))
      )
      val assessmentEntity = assessmentEntity(answers)
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessmentEntity.episodes.first(),
          any()
        )
      } returns AssessmentEpisodeUpdateErrors()

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestionCode1 to listOf("child name 1"),
          childQuestionCode2 to listOf("child address 1")
        )
      )

      val episodeDto =
        assessmentUpdateService.updateEpisodeTableRow(
          assessmentEntity.episodes.first(),
          "children_at_risk",
          0,
          tableAnswers
        )

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode1]!!,
        "child name 1",
        "child name 2"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode2]!!,
        "child address 1",
        "child address 2"
      )
    }

    @Test
    fun `update last row of table`() {
      val answers = mutableMapOf(
        questionCode1 to AnswerEntity.from("some free text"),
        questionCode2 to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        questionCode3 to AnswerEntity.from("not mapped to oasys"),
        childQuestionCode1 to AnswerEntity.from(listOf("child name 1", "name of child 2")),
        childQuestionCode2 to AnswerEntity.from(listOf("child address 1", ""))
      )
      val assessmentEntity = assessmentEntity(answers)
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessmentEntity.episodes.first(),
          any()
        )
      } returns AssessmentEpisodeUpdateErrors()

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestionCode1 to listOf("child name 2"),
          childQuestionCode2 to listOf("child address 2")
        )
      )

      val episodeDto =
        assessmentUpdateService.updateEpisodeTableRow(
          assessmentEntity.episodes.first(),
          "children_at_risk",
          1,
          tableAnswers
        )

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode1]!!,
        "child name 1",
        "child name 2"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode2]!!,
        "child address 1",
        "child address 2"
      )
    }

    @Test
    fun `update middle row of table`() {
      val answers = mutableMapOf(
        questionCode1 to AnswerEntity.from("some free text"),
        questionCode2 to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        questionCode3 to AnswerEntity.from("not mapped to oasys"),
        childQuestionCode1 to AnswerEntity.from(listOf("child name 1", "name of child 2", "child name 3")),
        childQuestionCode2 to AnswerEntity.from(listOf("child address 1", "address of child 2", "child address 3"))
      )
      val assessmentEntity = assessmentEntity(answers)
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessmentEntity.episodes.first(),
          any()
        )
      } returns AssessmentEpisodeUpdateErrors()

      val tableAnswers = UpdateAssessmentEpisodeDto(
        mapOf(
          childQuestionCode1 to listOf("child name 2"),
          childQuestionCode2 to listOf("child address 2")
        )
      )

      val episodeDto =
        assessmentUpdateService.updateEpisodeTableRow(
          assessmentEntity.episodes.first(),
          "children_at_risk",
          1,
          tableAnswers
        )

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode1]!!,
        "child name 1",
        "child name 2",
        "child name 3"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode2]!!,
        "child address 1",
        "child address 2",
        "child address 3"
      )
    }

    @Test
    fun `delete first row of table`() {
      val answers = mutableMapOf(
        questionCode1 to AnswerEntity.from("some free text"),
        questionCode2 to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        questionCode3 to AnswerEntity.from("not mapped to oasys"),
        childQuestionCode1 to AnswerEntity.from(listOf("child name 1", "child name 2", "child name 3")),
        childQuestionCode2 to AnswerEntity.from(listOf("child address 1", "child address 2", "child address 3"))
      )
      val assessmentEntity = assessmentEntity(answers)
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessmentEntity.episodes.first(),
          any()
        )
      } returns AssessmentEpisodeUpdateErrors()

      val episodeDto =
        assessmentUpdateService.deleteEpisodeTableRow(assessmentEntity.episodes.first(), "children_at_risk", 0)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode1]!!,
        "child name 2",
        "child name 3"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode2]!!,
        "child address 2",
        "child address 3"
      )
    }

    @Test
    fun `delete middle row of table`() {
      val answers = mutableMapOf(
        questionCode1 to AnswerEntity.from("some free text"),
        questionCode2 to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        questionCode3 to AnswerEntity.from("not mapped to oasys"),
        childQuestionCode1 to AnswerEntity.from(listOf("child name 1", "name of child 2", "child name 3")),
        childQuestionCode2 to AnswerEntity.from(listOf("child address 1", "address of child 2", "child address 3"))
      )
      val assessmentEntity = assessmentEntity(answers)
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessmentEntity.episodes.first(),
          any()
        )
      } returns AssessmentEpisodeUpdateErrors()

      val episodeDto =
        assessmentUpdateService.deleteEpisodeTableRow(assessmentEntity.episodes.first(), "children_at_risk", 1)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode1]!!,
        "child name 1",
        "child name 3"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode2]!!,
        "child address 1",
        "child address 3"
      )
    }

    @Test
    fun `delete last row of table`() {
      val answers = mutableMapOf(
        questionCode1 to AnswerEntity.from("some free text"),
        questionCode2 to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        questionCode3 to AnswerEntity.from("not mapped to oasys"),
        childQuestionCode1 to AnswerEntity.from(listOf("child name 1", "child name 2", "child name 3")),
        childQuestionCode2 to AnswerEntity.from(listOf("child address 1", "child address 2", "child address 3"))
      )

      val assessmentEntity = assessmentEntity(answers)
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessmentEntity.episodes.first(),
          any()
        )
      } returns AssessmentEpisodeUpdateErrors()

      val episodeDto =
        assessmentUpdateService.deleteEpisodeTableRow(assessmentEntity.episodes.first(), "children_at_risk", 2)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode1]!!,
        "child name 1",
        "child name 2"
      )

      Verify.multiAnswers(
        episodeDto.answers[childQuestionCode2]!!,
        "child address 1",
        "child address 2"
      )
    }

    @Test
    fun `delete only row of table`() {
      val answers = mutableMapOf(
        questionCode1 to AnswerEntity.from("some free text"),
        questionCode2 to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        questionCode3 to AnswerEntity.from("not mapped to oasys"),
        childQuestionCode1 to AnswerEntity.from(listOf("child name 1")),
        childQuestionCode2 to AnswerEntity.from(listOf("child address 1"))
      )
      val assessmentEntity = assessmentEntity(answers)
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessmentEntity.episodes.first(),
          any()
        )
      } returns AssessmentEpisodeUpdateErrors()

      val episodeDto =
        assessmentUpdateService.deleteEpisodeTableRow(assessmentEntity.episodes.first(), "children_at_risk", 0)

      assertThat(episodeDto.answers).hasSize(5)
      Verify.emptyAnswer(episodeDto.answers[childQuestionCode1]!!)
      Verify.emptyAnswer(episodeDto.answers[childQuestionCode2]!!)
    }

    @Test
    fun `fail on bad table name`() {
      val answers = mutableMapOf(
        questionCode1 to AnswerEntity.from("some free text"),
        questionCode2 to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        questionCode3 to AnswerEntity.from("not mapped to oasys"),
        childQuestionCode1 to AnswerEntity.from(listOf("child name 1", "name of child 2", "child name 3")),
        childQuestionCode2 to AnswerEntity.from(listOf("child address 1", "address of child 2", "child address 3"))
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
      val assessmentEntity = assessmentEntity(mutableMapOf())
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity

      for (index in listOf(-3, -1, 0, 5)) {
        assertThatThrownBy {
          assessmentUpdateService.updateEpisodeTableRow(
            assessmentEntity.episodes.first(),
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
      val assessmentEntity = assessmentEntity(mutableMapOf())
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessmentEntity

      for (index in listOf(-3, -1, 0, 5)) {
        assertThatThrownBy {
          assessmentUpdateService.deleteEpisodeTableRow(
            assessmentEntity.episodes.first(),
            "children_at_risk",
            index
          )
        }
          .isInstanceOf(IllegalStateException::class.java)
          .hasMessage("Bad index $index for table children_at_risk")
      }
    }
  }

  private fun assessmentEntity(answers: MutableMap<String, AnswerEntity>): AssessmentEntity {
    return AssessmentEntity(
      assessmentId = assessmentId,
      episodes = mutableListOf(
        AssessmentEpisodeEntity(
          episodeUuid = episodeUuid,
          episodeId = episodeId2,
          changeReason = "Change of Circs 2",
          answers = answers,
          createdDate = LocalDateTime.now(),
          assessmentSchemaCode = AssessmentSchemaCode.ROSH
        ),
      )
    )
  }

  private fun makeQuestion(
    questionSchemaId: Long,
    questionCode: String
  ): QuestionSchemaEntity {
    return QuestionSchemaEntity(
      questionSchemaId = questionSchemaId,
      questionSchemaUuid = UUID.randomUUID(),
      questionCode = questionCode,
      answerType = "free text"
    )
  }
}
