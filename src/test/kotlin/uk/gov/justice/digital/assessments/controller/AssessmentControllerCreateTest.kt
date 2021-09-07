package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.AssessmentSubjectDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.ErrorResponse
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.utils.RequestData
import java.time.LocalDateTime
import java.util.UUID

@SqlGroup(
  Sql(
    scripts = ["classpath:assessments/before-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
  ),
  Sql(
    scripts = ["classpath:assessments/after-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
  )
)
@AutoConfigureWebTestClient(timeout = "360000")
class AssessmentControllerCreateTest : IntegrationTest() {

  @Nested
  @DisplayName("creating court assessments")
  inner class CreatingAssessment {
    @Test
    fun `access forbidden when no authority`() {
      webTestClient.get().uri("/assessments/court")
        .header("Content-Type", "application/json")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `should return bad request when no user area header is set when creating court assessment`() {
      webTestClient.post().uri("/assessments")
        .bodyValue(
          CreateAssessmentDto(
            courtCode = "SHF06",
            caseNumber = "668911253",
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          )
        )
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isBadRequest
        .expectBody<ErrorResponse>()
        .consumeWith {
          assertThat(it.responseBody?.status).isEqualTo(400)
          assertThat(it.responseBody?.developerMessage).isEqualTo("Area Code Header is mandatory")
        }
    }

    @Test
    fun `create a new assessment from court details, creates subject and episode, returns assessment`() {
      val assessment = createCourtAssessment("SHF06", "668911253")

      assertThat(assessment.assessmentId).isNotNull
      assertThat(assessment.assessmentUuid).isNotNull
      assertThat(assessment.createdDate).isEqualToIgnoringMinutes(LocalDateTime.now())

      val subject = fetchAssessmentSubject(assessment.assessmentUuid!!)
      assertThat(subject.assessmentUuid).isEqualTo(assessment.assessmentUuid)
      assertThat(subject.name).isEqualTo("John Smith")
      assertThat(subject.dob).isEqualTo("1979-08-18")
      assertThat(subject.crn).isEqualTo("DX12340A")
      assertThat(subject.pnc).isEqualTo("A/1234560BA")
      assertThat(subject.createdDate).isEqualToIgnoringMinutes(LocalDateTime.now())

      val episodes = fetchEpisodes(assessment.assessmentUuid!!.toString())
      assertThat(episodes).hasSize(1)
      assertThat(episodes[0].oasysAssessmentId).isEqualTo(1)
    }

    @Test
    fun `creating an assessment from court details when one already exists returns existing assessment`() {
      val assessment = createCourtAssessment("courtCode", "caseNumber")

      assertThat(assessment.assessmentId).isEqualTo(2)
      assertThat(assessment.assessmentUuid).isEqualTo(UUID.fromString("19c8d211-68dc-4692-a6e2-d58468127056"))
    }
  }

  @Nested
  @DisplayName("creating assessments from crn")
  inner class CreatingAssessmentFromCrn {

    private val crn = "DX12340A"
    private val eventID = 1L

    @Test
    fun `access forbidden when no authority`() {
      webTestClient.get().uri("/assessments/delius")
        .header("Content-Type", "application/json")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `should return bad request when no user area header is set when creating assessment from delius`() {
      webTestClient.post().uri("/assessments")
        .bodyValue(
          CreateAssessmentDto(
            crn = crn,
            deliusEventId = eventID,
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          )
        )
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isBadRequest
        .expectBody<ErrorResponse>()
        .consumeWith {
          assertThat(it.responseBody?.status).isEqualTo(400)
          assertThat(it.responseBody?.developerMessage).isEqualTo("Area Code Header is mandatory")
        }
    }

    @Test
    fun `creating a new assessment from crn and delius event id returns assessment`() {

      val assessment = createDeliusAssessment(crn, eventID)
      assertThat(assessment.assessmentId).isNotNull
      assertThat(assessment.assessmentUuid).isNotNull
      assertThat(assessment.createdDate).isEqualToIgnoringMinutes(LocalDateTime.now())
    }

    @Test
    fun `creating an assessment for a delius event id and crn when one already exists in ARN returns the existing assessment`() {
      val existingCrn = "CRN1"
      val existingEventId = 12345L
      val existingAssessment = createDeliusAssessment(existingCrn, existingEventId)
      val assessmentDto = createDeliusAssessment(existingCrn, existingEventId)

      assertThat(assessmentDto.assessmentId).isEqualTo(existingAssessment.assessmentId)
      assertThat(assessmentDto.assessmentUuid).isEqualTo(existingAssessment.assessmentUuid)
      assertThat(assessmentDto.createdDate).isEqualTo(existingAssessment.createdDate)
    }
  }

  @Nested
  @DisplayName("creating episode")
  inner class CreatingEpisode {
    @Test
    fun `creates new episode on existing assessment`() {
      val episode = webTestClient.post().uri("/assessments/f9a07b3f-91b7-45a7-a5ca-2d98cf1147d8/episodes")
        .bodyValue(CreateAssessmentEpisodeDto("Change of Circs", AssessmentSchemaCode.ROSH))
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      assertThat(episode?.assessmentUuid).isEqualTo(UUID.fromString("f9a07b3f-91b7-45a7-a5ca-2d98cf1147d8"))
      assertThat(episode?.created).isEqualToIgnoringMinutes(LocalDateTime.now())
      assertThat(episode?.answers).isEmpty()
    }
  }

  private fun createDeliusAssessment(crn: String, deliusId: Long): AssessmentDto {
    return createDeliusAssessment(
      CreateAssessmentDto(
        crn = crn,
        deliusEventId = deliusId,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH
      )
    )
  }

  private fun createDeliusAssessment(cad: CreateAssessmentDto): AssessmentDto {
    val assessment = webTestClient.post().uri("/assessments")
      .bodyValue(cad)
      .header(RequestData.USER_AREA_HEADER_NAME, "WWS")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentDto>()
      .returnResult()
      .responseBody
    return assessment!!
  }

  private fun createCourtAssessment(courtCode: String, caseNumber: String): AssessmentDto {
    return createCourtAssessment(
      CreateAssessmentDto(
        courtCode = courtCode,
        caseNumber = caseNumber,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH
      )
    )
  }

  private fun createCourtAssessment(cad: CreateAssessmentDto): AssessmentDto {
    val assessment = webTestClient.post().uri("/assessments")
      .bodyValue(cad)
      .header(RequestData.USER_AREA_HEADER_NAME, "WWS")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentDto>()
      .returnResult()
      .responseBody
    return assessment!!
  }

  private fun fetchAssessmentSubject(assessmentUuid: UUID): AssessmentSubjectDto {
    return fetchAssessmentSubject(assessmentUuid.toString())
  }

  private fun fetchAssessmentSubject(assessmentUuid: String): AssessmentSubjectDto {
    val subject = webTestClient.get().uri("/assessments/$assessmentUuid/subject")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentSubjectDto>()
      .returnResult()
      .responseBody
    return subject!!
  }

  private fun fetchEpisodes(assessmentUuid: String): List<AssessmentEpisodeDto> {
    return webTestClient.get().uri("/assessments/$assessmentUuid/episodes")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<List<AssessmentEpisodeDto>>()
      .returnResult()
      .responseBody!!
  }
}
