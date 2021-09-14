package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode.ROSH
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Tables Tests")
class AssessmentUpdateServiceTablesTest {
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

  private val tableName = "test_table"

  private val tableFields = QuestionSchemaEntities(
    listOf(
      QuestionSchemaEntity(
        questionSchemaId = 1L,
        questionSchemaUuid = UUID.randomUUID(),
        questionCode = "first_question",
      ),
      QuestionSchemaEntity(
        questionSchemaId = 2L,
        questionSchemaUuid = UUID.randomUUID(),
        questionCode = "second_question",
      )
    )
  )

  private val assessmentEpisode = AssessmentEpisodeEntity(
    episodeId = 1L,
    episodeUuid = UUID.randomUUID(),
    assessmentSchemaCode = ROSH
  )

  @BeforeEach
  fun setup() {
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.ROSH) } returns OasysAssessmentType.SHORT_FORM_PSR
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.RSR) } returns OasysAssessmentType.SOMETHING_IN_OASYS
    every { episodeRepository.save(any()) } returns null
  }

  @Test
  fun `adds an entry to a table`() {
    val requestBody = UpdateAssessmentEpisodeDto(
      answers = mapOf(
        "first_question" to listOf("first_answer"),
        "second_question" to listOf("second_answer"),
      )
    )

    every { questionService.getAllGroupQuestionsByGroupCode(tableName) } returns tableFields

    val updatedEpisode = assessmentUpdateService.addEntryToTable(
      assessmentEpisode,
      tableName,
      requestBody,
    )

    assertThat(updatedEpisode.tables.size).isEqualTo(1)
    assertThat(updatedEpisode.tables[tableName]?.size).isEqualTo(1)
    assertThat(updatedEpisode.tables[tableName]?.first()).isEqualTo(
      mapOf(
        "first_question" to listOf("first_answer"),
        "second_question" to listOf("second_answer"),
      )
    )
  }

  @Test
  fun `only stores fields defined in the question group`() {
    val requestBody = UpdateAssessmentEpisodeDto(
      answers = mapOf(
        "first_question" to listOf("first_answer"),
        "second_question" to listOf("second_answer"),
        "third_question" to listOf("third_answer"),
      )
    )

    every { questionService.getAllGroupQuestionsByGroupCode(tableName) } returns tableFields

    val updatedEpisode = assessmentUpdateService.addEntryToTable(
      assessmentEpisode,
      tableName,
      requestBody,
    )

    assertThat(updatedEpisode.tables.size).isEqualTo(1)
    assertThat(updatedEpisode.tables[tableName]?.size).isEqualTo(1)
    assertThat(updatedEpisode.tables[tableName]?.first()).isEqualTo(
      mapOf(
        "first_question" to listOf("first_answer"),
        "second_question" to listOf("second_answer"),
      )
    )
  }

  @Test
  fun `updates entries for a given table`() {
    val assessmentEpisodeWithExistingTable = AssessmentEpisodeEntity(
      episodeId = 1L,
      episodeUuid = UUID.randomUUID(),
      assessmentSchemaCode = ROSH,
      tables = mutableMapOf(
        tableName to mutableListOf(
          mapOf(
            "first_question" to listOf("first_answer"),
            "second_question" to listOf("second_answer"),
          )
        )
      )
    )

    val requestBody = UpdateAssessmentEpisodeDto(
      answers = mapOf(
        "first_question" to listOf("updated_answer"),
      )
    )

    every { questionService.getAllGroupQuestionsByGroupCode(tableName) } returns tableFields

    val updatedEpisode = assessmentUpdateService.updateTableEntry(
      assessmentEpisodeWithExistingTable,
      tableName,
      requestBody,
      0,
    )

    assertThat(updatedEpisode.tables.size).isEqualTo(1)
    assertThat(updatedEpisode.tables[tableName]?.size).isEqualTo(1)
    assertThat(updatedEpisode.tables[tableName]?.first()).isEqualTo(
      mapOf(
        "first_question" to listOf("updated_answer"),
        "second_question" to listOf("second_answer"),
      )
    )
  }

  @Test
  fun `remove an entry for a given index`() {
    val assessmentEpisodeWithExistingTable = AssessmentEpisodeEntity(
      episodeId = 1L,
      episodeUuid = UUID.randomUUID(),
      assessmentSchemaCode = ROSH,
      tables = mutableMapOf(
        tableName to mutableListOf(
          mapOf(
            "first_question" to listOf("for_first_entry"),
          ),
          mapOf(
            "first_question" to listOf("for_second_entry"),
          ),
          mapOf(
            "first_question" to listOf("for_third_entry"),
          )
        )
      )
    )

    every { questionService.getAllGroupQuestionsByGroupCode(tableName) } returns tableFields

    val updatedEpisode = assessmentUpdateService.deleteTableEntry(
      assessmentEpisodeWithExistingTable,
      tableName,
      1,
    )

    assertThat(updatedEpisode.tables.size).isEqualTo(1)
    assertThat(updatedEpisode.tables[tableName]?.size).isEqualTo(2)
    assertThat(updatedEpisode.tables[tableName]).isEqualTo(
      mutableListOf(
        mapOf(
          "first_question" to listOf("for_first_entry"),
        ),
        mapOf(
          "first_question" to listOf("for_third_entry"),
        )
      )
    )
  }
}
