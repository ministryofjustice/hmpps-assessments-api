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
import java.time.LocalDate
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
@AutoConfigureWebTestClient
class AssessmentControllerCreateTest : IntegrationTest() {

  @Nested
  @DisplayName("Creating assessments from court")
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
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
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
      val dto = CreateAssessmentDto(
        courtCode = "SHF06",
        caseNumber = "668911253",
        assessmentSchemaCode = AssessmentSchemaCode.ROSH
      )
      val assessment = webTestClient.post().uri("/assessments")
        .bodyValue(dto)
        .header(RequestData.USER_AREA_HEADER_NAME, "WWS")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentDto>()
        .returnResult()
        .responseBody

      assertThat(assessment?.assessmentUuid).isNotNull
      assertThat(assessment?.createdDate).isEqualToIgnoringMinutes(LocalDateTime.now())

      val subject = fetchAssessmentSubject(assessment?.assessmentUuid)
      assertThat(subject?.name).isEqualTo("John Smith")
      assertThat(subject?.dob).isEqualTo("1979-08-18")
      assertThat(subject?.crn).isEqualTo("DX5678A")
      assertThat(subject?.pnc).isEqualTo("A/1234560BA")
      assertThat(subject?.createdDate).isEqualToIgnoringMinutes(LocalDateTime.now())

      val episodes = fetchEpisodes(assessment?.assessmentUuid!!)
      assertThat(episodes).hasSize(1)
      assertThat(episodes?.get(0)?.oasysAssessmentId).isEqualTo(1)
    }

    @Test
    fun `creating an assessment from court details when one already exists returns existing assessment`() {
      val dto = CreateAssessmentDto(
        courtCode = "SHF06",
        caseNumber = "existingAssessment",
        assessmentSchemaCode = AssessmentSchemaCode.ROSH
      )
      val assessment = webTestClient.post().uri("/assessments")
        .bodyValue(dto)
        .header(RequestData.USER_AREA_HEADER_NAME, "WWS")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentDto>()
        .returnResult()
        .responseBody

      assertThat(assessment?.assessmentUuid).isEqualTo(UUID.fromString("2e020e28-a21c-207f-bc78-e5f284e237e5"))
    }
  }

  @Nested
  @DisplayName("Creating assessments from Delius")
  inner class CreatingAssessmentFromCrn {

    private val crn = "DX5678A"
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
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isBadRequest
        .expectBody<ErrorResponse>()
        .consumeWith {
          assertThat(it.responseBody?.status).isEqualTo(400)
          assertThat(it.responseBody?.developerMessage).isEqualTo("Area Code Header is mandatory")
        }
    }

    @Test
    fun `should return forbidden when user does not have LAO permissions on offender`() {
      webTestClient.post().uri("/assessments")
        .bodyValue(
          CreateAssessmentDto(
            crn = "OX1232456",
            deliusEventId = eventID,
            assessmentSchemaCode = AssessmentSchemaCode.ROSH
          )
        )
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isForbidden
        .expectBody<ErrorResponse>()
        .consumeWith {
          assertThat(it.responseBody?.status).isEqualTo(403)
          assertThat(it.responseBody?.reason).isEqualTo("LAO_PERMISSION")
        }
    }

    @Test
    fun `creating a new UPW assessment from crn and delius event id returns assessment with prepopulated Delius answers`() {

      val dto = CreateAssessmentDto(
        crn = crn,
        deliusEventId = eventID,
        assessmentSchemaCode = AssessmentSchemaCode.UPW
      )
      val assessment = webTestClient.post().uri("/assessments")
        .bodyValue(dto)
        .header(RequestData.USER_AREA_HEADER_NAME, "WWS")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentDto>()
        .returnResult()
        .responseBody

      assertThat(assessment?.assessmentUuid).isNotNull
      assertThat(assessment?.episodes).hasSize(1)
      val answers = assessment.episodes?.first()?.answers
      assertThat(answers?.get("first_name")).isEqualTo(listOf("John"))
      assertThat(answers?.get("first_name_aliases")).isEqualTo(listOf("John", "Jonny"))
      assertThat(answers?.get("family_name")).isEqualTo(listOf("Smith"))
      assertThat(answers?.get("family_name_aliases")).isEqualTo(listOf("Smithy"))
      assertThat(answers?.get("dob")).isEqualTo(listOf("1979-08-18"))
      assertThat(answers?.get("dob_aliases")).isEqualTo(listOf("1979-09-18"))
      assertThat(answers?.get("crn")).isEqualTo(listOf("DX5678A"))
      assertThat(answers?.get("pnc")).isEqualTo(listOf("A/1234560BA"))
      assertThat(answers?.get("ethnicity")).isEqualTo(listOf("Asian"))
      assertThat(answers?.get("gender")).isEqualTo(listOf("F"))
      assertThat(answers?.get("email_addresses")).isEqualTo(listOf("address1@gmail.com", "address2@gmail.com"))
      assertThat(answers?.get("mobile_phone_number")).isEqualTo(listOf("1838893"))
      assertThat(answers?.get("telephone_number")).isEqualTo(listOf("0123456999"))
      assertThat(answers?.get("main_address_building_name")).isEqualTo(listOf("HMPPS Digital Studio"))
      assertThat(answers?.get("main_address_house_number")).isEqualTo(listOf("32"))
      assertThat(answers?.get("main_address_street_name")).isEqualTo(listOf("Scotland Street"))
      assertThat(answers?.get("main_address_district")).isEqualTo(listOf("Sheffield City Centre"))
      assertThat(answers?.get("main_address_town_city")).isEqualTo(listOf("Sheffield"))
      assertThat(answers?.get("main_address_county")).isEqualTo(listOf("South Yorkshire"))
      assertThat(answers?.get("main_address_postcode")).isEqualTo(listOf("S3 7BS"))

      assertThat(answers?.get("upw_physical_disability")).isEqualTo(listOf("D", "D02", "RM", "RC", "PC", "VI", "HD"))
      assertThat(answers?.get("upw_physical_disability_details")).isEqualTo(
        listOf(
          "general health",
          "physical health concerns",
          "reduced mobility",
          "reduced physical capacity",
          "progressive condition",
          "visual impairment",
          "hearing difficulties"
        )
      )
      assertThat(answers?.get("upw_learning_disability")).isEqualTo(listOf("LA"))
      assertThat(answers?.get("upw_learning_disability_details")).isEqualTo(listOf("learning disability"))
      assertThat(answers?.get("upw_learning_difficulty")).isEqualTo(listOf("LD"))
      assertThat(answers?.get("upw_learning_difficulty_details")).isEqualTo(listOf("learning difficulties"))
      assertThat(answers?.get("upw_mental_health_condition")).isEqualTo(listOf("D", "D01", "MI"))
      assertThat(answers?.get("upw_mental_health_condition_details")).isEqualTo(
        listOf(
          "general health",
          "mental health",
          "mental illness"
        )
      )
      assertThat(answers?.get("language")).isEqualTo(listOf("French"))
      assertThat(answers?.get("requires_interpreter")).isEqualTo(listOf("true"))

    }

    @Test
    fun `creating a new assessment from crn and delius event id returns assessment`() {

      val dto = CreateAssessmentDto(
        crn = crn,
        deliusEventId = eventID,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH
      )
      val assessment = webTestClient.post().uri("/assessments")
        .bodyValue(dto)
        .header(RequestData.USER_AREA_HEADER_NAME, "WWS")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentDto>()
        .returnResult()
        .responseBody

      assertThat(assessment?.assessmentUuid).isNotNull
      assertThat(assessment?.createdDate).isEqualToIgnoringMinutes(LocalDateTime.now())
    }

    @Test
    fun `creating an assessment for a delius event id and crn when one already exists in ARN returns the existing assessment`() {
      val existingCrn = "CRN1"
      val existingEventId = 12345L
      val existingAssessment = createDeliusAssessment(existingCrn, existingEventId)
      val assessmentDto = createDeliusAssessment(existingCrn, existingEventId)

      assertThat(assessmentDto?.assessmentUuid).isEqualTo(existingAssessment?.assessmentUuid)
      assertThat(assessmentDto?.createdDate).isEqualTo(existingAssessment?.createdDate)
    }
  }

  @Nested
  @DisplayName("Creating an episode")
  inner class CreatingEpisode {
    @Test
    fun `creates new episode on existing assessment`() {
      val episode = webTestClient.post().uri("/assessments/49c8d211-68dc-4692-a6e2-d58468127356/episodes")
        .bodyValue(CreateAssessmentEpisodeDto("Change of Circs", 1L, AssessmentSchemaCode.ROSH))
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      assertThat(episode?.assessmentUuid).isEqualTo(UUID.fromString("49c8d211-68dc-4692-a6e2-d58468127356"))
      assertThat(episode?.created).isEqualToIgnoringMinutes(LocalDateTime.now())
      assertThat(episode?.answers).isEmpty()

      assertThat(episode?.offence?.offenceCode).isEqualTo("046")
      assertThat(episode?.offence?.codeDescription).isEqualTo("Stealing from shops and stalls (shoplifting)")
      assertThat(episode?.offence?.offenceSubCode).isEqualTo("00")
      assertThat(episode?.offence?.subCodeDescription).isEqualTo("Stealing from shops and stalls (shoplifting)")
      assertThat(episode?.offence?.sentenceDate).isEqualTo(LocalDate.of(2014, 8, 25))
    }
  }

  private fun createDeliusAssessment(crn: String, deliusId: Long): AssessmentDto? {
    val dto = CreateAssessmentDto(
      crn = crn,
      deliusEventId = deliusId,
      assessmentSchemaCode = AssessmentSchemaCode.ROSH
    )
    return webTestClient.post().uri("/assessments")
      .bodyValue(dto)
      .header(RequestData.USER_AREA_HEADER_NAME, "WWS")
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentDto>()
      .returnResult()
      .responseBody
  }

  private fun fetchAssessmentSubject(assessmentUuid: UUID?): AssessmentSubjectDto? {
    val subject = webTestClient.get().uri("/assessments/$assessmentUuid/subject")
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentSubjectDto>()
      .returnResult()
      .responseBody
    return subject!!
  }

  private fun fetchEpisodes(assessmentUuid: UUID?): List<AssessmentEpisodeDto>? {
    return webTestClient.get().uri("/assessments/$assessmentUuid/episodes")
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<List<AssessmentEpisodeDto>>()
      .returnResult()
      .responseBody!!
  }
}
