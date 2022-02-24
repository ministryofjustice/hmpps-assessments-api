package uk.gov.justice.digital.assessments.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
@AutoConfigureWebTestClient(timeout = "6000000")
class AssessmentControllerCreateTest : IntegrationTest() {

  private val objectMapper = ObjectMapper()

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
    fun `creating a new RSR assessment from crn returns assessment with prepopulated OASys answers`() {

      val dto = CreateAssessmentDto(
        crn = crn,
        deliusEventId = eventID,
        assessmentSchemaCode = AssessmentSchemaCode.RSR
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

      val answers = assessment.episodes.first().answers
      assertThat(answers).hasSize(34)

      assertThat(answers["date_first_sanction"]).isEqualTo(listOf("2020-01-01"))
      assertThat(answers["age_first_conviction"]).isEqualTo(listOf("26"))
      assertThat(answers["suitable_accommodation"]).isEqualTo(listOf("SIGNIFICANT_PROBLEMS"))
      assertThat(answers["unemployed_on_release"]).isEqualTo(listOf("NOT_AVAILABLE_FOR_WORK"))
      assertThat(answers["current_relationship_with_partner"]).isEqualTo(listOf("SOME_PROBLEMS"))
      assertThat(answers["total_sanctions"]).isEqualTo(listOf("5"))
      assertThat(answers["date_current_conviction"]).isEqualTo(listOf("2021-01-01"))
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
      val answers = assessment.episodes.first().answers
      assertThat(answers["first_name"]).isEqualTo(listOf("John"))
      assertThat(answers["first_name_aliases"]).isEqualTo(listOf("John", "Jonny"))
      assertThat(answers["family_name"]).isEqualTo(listOf("Smith"))
      assertThat(answers["family_name_aliases"]).isEqualTo(listOf("Smithy"))
      assertThat(answers["dob"]).isEqualTo(listOf("1979-08-18"))
      assertThat(answers["dob_aliases"]).isEqualTo(listOf("1979-09-18"))
      assertThat(answers["crn"]).isEqualTo(listOf("DX5678A"))
      assertThat(answers["pnc"]).isEqualTo(listOf("A/1234560BA"))
      assertThat(answers["ethnicity"]).isEqualTo(listOf("Asian"))
      assertThat(answers["gender"]).isEqualTo(listOf("MALE"))
      assertThat(answers["gender_identity"]).isEqualTo(listOf("NON_BINARY"))
      assertThat(answers["contact_email_addresses"]).isEqualTo(listOf("address1@gmail.com", "address2@gmail.com"))
      assertThat(answers["contact_mobile_phone_number"]).isEqualTo(listOf("1838893"))
      assertThat(answers["contact_phone_number"]).isEqualTo(listOf("0123456999"))
      assertThat(answers["contact_address_building_name"]).isEqualTo(listOf("HMPPS Digital Studio"))
      assertThat(answers["contact_address_house_number"]).isEqualTo(listOf("32"))
      assertThat(answers["contact_address_street_name"]).isEqualTo(listOf("Scotland Street"))
      assertThat(answers["contact_address_district"]).isEqualTo(listOf("Sheffield City Centre"))
      assertThat(answers["contact_address_town_or_city"]).isEqualTo(listOf("Sheffield"))
      assertThat(answers["contact_address_county"]).isEqualTo(listOf("South Yorkshire"))
      assertThat(answers["contact_address_postcode"]).isEqualTo(listOf("S3 7BS"))

      assertThat(answers["physical_disability"]).isEqualTo(listOf("D", "D02", "RM", "RC", "PC", "VI", "HD"))
      assertThat(answers["physical_disability_details"]).isEqualTo(
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
      assertThat(answers["learning_disability"]).isEqualTo(listOf("LA"))
      assertThat(answers["learning_disability_details"]).isEqualTo(listOf("learning disability"))
      assertThat(answers["learning_difficulty"]).isEqualTo(listOf("LD"))
      assertThat(answers["learning_difficulty_details"]).isEqualTo(listOf("learning difficulties"))
      assertThat(answers["mental_health_condition"]).isEqualTo(listOf("D", "D01", "MI"))
      assertThat(answers["mental_health_condition_details"]).isEqualTo(
        listOf(
          "general health",
          "mental health",
          "mental illness"
        )
      )
      assertThat(answers["language"]).isEqualTo(listOf("French"))
      assertThat(answers["requires_interpreter"]).isEqualTo(listOf("true"))

      assertThat(answers["emergency_contact_first_name"]).isEqualTo(listOf("Brian"))
      assertThat(answers["emergency_contact_family_name"]).isEqualTo(listOf("Contact"))
      assertThat(answers["emergency_contact_relationship"]).isEqualTo(listOf("Father"))
      assertThat(answers["emergency_contact_mobile_phone_number"]).isEqualTo(listOf("07333567890"))
      assertThat(answers["emergency_contact_phone_number"]).isEqualTo(listOf("0133456789"))

      assertThat(answers["allergies"]).isEqualTo(listOf("YES"))
      assertThat(answers["allergies_details"]).isEqualTo(listOf("Nut Allergy"))
      assertThat(answers["pregnancy"]).isEqualTo(listOf("NO"))
      assertThat(answers["pregnancy_pregnant_details"]).isEqualTo(emptyList<String>())
      assertThat(answers["caring_commitments"]).isEqualTo(listOf("YES"))
      assertThat(answers["caring_commitments_details"]).isEqualTo(listOf("Primary Carer"))
      assertThat(answers["reading_writing_difficulties"]).isEqualTo(listOf("YES"))
      assertThat(answers["reading_writing_difficulties_details"]).isEqualTo(listOf("Cannot read"))

      val gpDetails = answers["gp_details"]
      val gpJson1 = objectMapper.readValue<Map<String, List<String>>>(gpDetails?.get(0)!!)
      assertThat(gpJson1["gp_first_name"]).isEqualTo(listOf("Nick"))
      assertThat(gpJson1["gp_family_name"]).isEqualTo(listOf("Riviera"))
      assertThat(gpJson1["gp_address_building_name"]).isEqualTo(listOf("The practice"))
      assertThat(gpJson1["gp_address_house_number"]).isEqualTo(listOf("38"))
      assertThat(gpJson1["gp_address_street_name"]).isEqualTo(listOf("East Street"))
      assertThat(gpJson1["gp_address_district"]).isEqualTo(listOf("East City Centre"))
      assertThat(gpJson1["gp_address_town_or_city"]).isEqualTo(listOf("Bristol"))
      assertThat(gpJson1["gp_address_county"]).isEqualTo(listOf("East London"))
      assertThat(gpJson1["gp_address_postcode"]).isEqualTo(listOf("E5 7BS"))
      assertThat(gpJson1["gp_phone_number"]).isEqualTo(listOf("0233456789"))

      val gpJson2 = objectMapper.readValue<Map<String, List<String>>>(gpDetails[1])
      assertThat(gpJson2["gp_first_name"]).isEqualTo(listOf("Steve"))
      assertThat(gpJson2["gp_family_name"]).isEqualTo(listOf("Wilson"))
      assertThat(gpJson2["gp_address_building_name"]).isEqualTo(listOf("The Building"))
      assertThat(gpJson2["gp_address_house_number"]).isEqualTo(listOf("77"))
      assertThat(gpJson2["gp_address_street_name"]).isEqualTo(listOf("Some Street"))
      assertThat(gpJson2["gp_address_district"]).isEqualTo(listOf("Some City Centre"))
      assertThat(gpJson2["gp_address_town_or_city"]).isEqualTo(listOf("London"))
      assertThat(gpJson2["gp_address_county"]).isEqualTo(listOf("Essex"))
      assertThat(gpJson2["gp_address_postcode"]).isEqualTo(listOf("NW10 1EP"))
      assertThat(gpJson2["gp_phone_number"]).isEqualTo(listOf("0776 666 6666"))
    }

    @Test
    fun `creating a new UPW assessment from Delius only returns GPs where active flag is true`() {

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

      val answers = assessment.episodes.first().answers
      val gpDetails = answers["gp_details"]

      val gpJson1 = objectMapper.readValue<Map<String, List<String>>>(gpDetails?.get(0)!!)
      assertThat(gpJson1["gp_first_name"]).isEqualTo(listOf("Nick"))
      assertThat(gpJson1["gp_family_name"]).isEqualTo(listOf("Riviera"))

      val gpJson2 = objectMapper.readValue<Map<String, List<String>>>(gpDetails[1])
      assertThat(gpJson2["gp_first_name"]).isEqualTo(listOf("Steve"))
      assertThat(gpJson2["gp_family_name"]).isEqualTo(listOf("Wilson"))

      assertThat(gpDetails).hasSize(2)
    }

    @Test
    fun `should pre-populate answers with the 'mapped' type`() {

      val dto = CreateAssessmentDto(
        crn = "DX5678B",
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
      val answers = assessment.episodes.first().answers
      assertThat(answers["pregnancy"]).isEqualTo(listOf("PREGNANT"))
      assertThat(answers["pregnancy_pregnant_details"]).isEqualTo(listOf("Some notes"))
    }

    @Test
    fun `should pre-populate answers with the 'mapped' type and  when the 'ifEmpty' flag is set to 'true'`() {

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
      val answers = assessment.episodes.first().answers
      assertThat(answers["pregnancy"]).isEqualTo(listOf("NO"))
      assertThat(answers["pregnancy_pregnant_details"]).isEqualTo(emptyList<String>())
      assertThat(answers["pregnancy_recently_given_birth_details"]).isEqualTo(emptyList<String>())
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
