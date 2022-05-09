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
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import uk.gov.justice.digital.assessments.services.dto.AssessmentEpisodeUpdateErrors
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Tables Tests")
class AssessmentUpdateServiceTablesTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val episodeRepository: EpisodeRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val riskPredictorsService: RiskPredictorsService = mockk()
  private val assessmentReferenceDataService: AssessmentReferenceDataService = mockk()
  private val oasysAssessmentUpdateService: OasysAssessmentUpdateService = mockk()
  private val assessmentService: AssessmentService = mockk()
  private val authorService: AuthorService = mockk()
  private val auditService: AuditService = mockk()
  private val telemetryService: TelemetryService = mockk()

  private val assessmentUpdateService = AssessmentUpdateService(
    assessmentRepository,
    episodeRepository,
    questionService,
    riskPredictorsService,
    oasysAssessmentUpdateService,
    assessmentService,
    authorService,
    auditService,
    telemetryService
  )

  private val tableName = "test_table"

  private val tableFields = QuestionSchemaEntities(
    listOf(
      QuestionEntity(
        questionId = 1L,
        questionUuid = UUID.randomUUID(),
        questionCode = "first_question",
      ),
      QuestionEntity(
        questionId = 2L,
        questionUuid = UUID.randomUUID(),
        questionCode = "second_question",
      )
    )
  )

  private val assessmentUuid = UUID.randomUUID()
  private val episodeUuid = UUID.randomUUID()
  private val assessmentEpisode = AssessmentEpisodeEntity(
    episodeId = 1L,
    episodeUuid = episodeUuid,
    assessmentType = AssessmentType.ROSH,
    author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
    assessment = AssessmentEntity()
  )

  @BeforeEach
  fun setup() {
    every { assessmentReferenceDataService.toOasysAssessmentType(AssessmentType.ROSH) } returns OasysAssessmentType.SHORT_FORM_PSR
    every { assessmentReferenceDataService.toOasysAssessmentType(AssessmentType.RSR) } returns OasysAssessmentType.SOMETHING_IN_OASYS
    every { episodeRepository.save(any()) } returns null
    every { assessmentRepository.save(any()) } returns null
    every {
      oasysAssessmentUpdateService.updateOASysAssessment(any())
    } returns AssessmentEpisodeUpdateErrors()
    every { assessmentService.getCurrentEpisode(assessmentUuid) } returns assessmentEpisode
    every { assessmentService.getEpisode(assessmentUuid, episodeUuid) } returns assessmentEpisode
    every { assessmentService.shouldPushToOasys(AssessmentType.ROSH) } returns true
  }

  @Test
  fun `adds an entry to a table for a given episode`() {
    val requestBody = UpdateAssessmentEpisodeDto(
      answers = mapOf(
        "first_question" to listOf("first_answer"),
        "second_question" to listOf("second_answer"),
      )
    )

    every { questionService.getAllGroupQuestionsByGroupCode(tableName) } returns tableFields

    val updatedEpisode = assessmentUpdateService.addEntryToTableForEpisode(
      assessmentUuid,
      episodeUuid,
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
  fun `adds an entry to a table for the current episode`() {
    val requestBody = UpdateAssessmentEpisodeDto(
      answers = mapOf(
        "first_question" to listOf("first_answer"),
        "second_question" to listOf("second_answer"),
      )
    )

    every { questionService.getAllGroupQuestionsByGroupCode(tableName) } returns tableFields

    val updatedEpisode = assessmentUpdateService.addEntryToTableForCurrentEpisode(
      assessmentUuid,
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

    val updatedEpisode = assessmentUpdateService.addEntryToTableForEpisode(
      assessmentUuid,
      episodeUuid,
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
  fun `updates entries for a given table in an episode`() {
    val episodeUuid = UUID.randomUUID()
    val assessmentEpisodeWithExistingTable = AssessmentEpisodeEntity(
      episodeId = 1L,
      episodeUuid = episodeUuid,
      assessmentType = AssessmentType.ROSH,
      tables = mutableMapOf(
        tableName to mutableListOf(
          mapOf(
            "first_question" to listOf("first_answer"),
            "second_question" to listOf("second_answer"),
          )
        )
      ),
      author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      assessment = AssessmentEntity()
    )

    val requestBody = UpdateAssessmentEpisodeDto(
      answers = mapOf(
        "first_question" to listOf("updated_answer"),
      )
    )

    every { questionService.getAllGroupQuestionsByGroupCode(tableName) } returns tableFields
    every { assessmentService.getEpisode(assessmentUuid, episodeUuid) } returns assessmentEpisodeWithExistingTable

    val updatedEpisode = assessmentUpdateService.updateEntryToTableForEpisode(
      assessmentUuid,
      episodeUuid,
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
  fun `updates entries for a given table in the current episode`() {
    val episodeUuid = UUID.randomUUID()
    val assessmentEpisodeWithExistingTable = AssessmentEpisodeEntity(
      episodeId = 1L,
      episodeUuid = episodeUuid,
      assessmentType = AssessmentType.ROSH,
      author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      assessment = AssessmentEntity(),
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
    every { assessmentService.getCurrentEpisode(assessmentUuid) } returns assessmentEpisodeWithExistingTable

    val updatedEpisode = assessmentUpdateService.updateEntryToTableForCurrentEpisode(
      assessmentUuid,
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
    val episodeUuid = UUID.randomUUID()
    val assessmentEpisodeWithExistingTable = AssessmentEpisodeEntity(
      episodeId = 1L,
      episodeUuid = episodeUuid,
      assessmentType = AssessmentType.ROSH,
      author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      assessment = AssessmentEntity(),
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
    every { assessmentService.getEpisode(assessmentUuid, episodeUuid) } returns assessmentEpisodeWithExistingTable

    val updatedEpisode = assessmentUpdateService.deleteEntryToTableForEpisode(
      assessmentUuid,
      episodeUuid,
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

  @Test
  fun `remove an entry for a given index in the current episode`() {
    val episodeUuid = UUID.randomUUID()
    val assessmentEpisodeWithExistingTable = AssessmentEpisodeEntity(
      episodeId = 1L,
      episodeUuid = episodeUuid,
      assessmentType = AssessmentType.ROSH,
      author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      assessment = AssessmentEntity(),
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
    every { assessmentService.getCurrentEpisode(assessmentUuid) } returns assessmentEpisodeWithExistingTable

    val updatedEpisode = assessmentUpdateService.deleteEntryToTableForCurrentEpisode(
      assessmentUuid,
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
