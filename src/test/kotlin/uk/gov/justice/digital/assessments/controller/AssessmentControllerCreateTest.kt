package uk.gov.justice.digital.assessments.controller

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.ErrorResponse
import uk.gov.justice.digital.assessments.api.answers.AnswersDto
import uk.gov.justice.digital.assessments.api.answers.DisabilityAnswerDto
import uk.gov.justice.digital.assessments.api.assessments.AssessmentDto
import uk.gov.justice.digital.assessments.api.assessments.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.assessments.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.assessments.CreateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@SqlGroup(
  Sql(
    scripts = ["classpath:assessments/before-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
  ),
  Sql(
    scripts = ["classpath:assessments/after-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
  ),
)
@AutoConfigureWebTestClient(timeout = "6000000")
class AssessmentControllerCreateTest : IntegrationTest() {

  private fun objectMapper(): ObjectMapper {
    return ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .setSerializationInclusion(JsonInclude.Include.NON_NULL).setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
      .registerModules(
        Jdk8Module(),
        JavaTimeModule(),
        KotlinModule.Builder().build(),
      )
  }

  private fun toJsonString(input: Any): String {
    return objectMapper().writeValueAsString(input)
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
    fun `should return forbidden when user does not have LAO permissions on offender`() {
      webTestClient.post().uri("/assessments")
        .bodyValue(
          CreateAssessmentDto(
            crn = "OX1232456",
            deliusEventId = eventID,
            assessmentSchemaCode = AssessmentType.UPW,
          ),
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
    fun `should return bad request when invalid assessment type of ROSH is in request`() {
      webTestClient.post().uri("/assessments").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
          """{
            "crn": "CRN1",
            "deliusEventId": "1",
            "assessmentSchemaCode": "ROSH"
          }""",
        )
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isBadRequest
        .expectBody<ErrorResponse>()
        .consumeWith {
          assertThat(it.responseBody?.developerMessage).contains(
            "Cannot deserialize value of type `uk.gov.justice.digital.assessments.jpa.entities.AssessmentType` from String \"ROSH\"",
          )
        }
    }

    @Test
    fun `creating a new UPW assessment from crn and delius event id returns assessment with pre-populated Delius answers`() {
      val assessment = createDeliusAssessment(crn, eventID)

      assertThat(assessment?.assessmentUuid).isNotNull
      assertThat(assessment?.episodes).hasSize(1)
      val answers = assessment?.episodes?.first()?.answers.orEmpty()
      assertThat(answers["first_name"]).isEqualTo(listOf("John"))
      assertThat(answers["first_name_aliases"]).isEqualTo(listOf("John", "Jonny"))
      assertThat(answers["family_name"]).isEqualTo(listOf("Smith"))
      assertThat(answers["family_name_aliases"]).isEqualTo(listOf("Smithy", "Smith"))
      assertThat(answers["dob"]).isEqualTo(listOf("1979-08-18"))
      assertThat(answers["dob_aliases"]).isEqualTo(listOf("1979-09-18", "1979-08-17"))
      assertThat(answers["crn"]).isEqualTo(listOf("DX5678A"))
      assertThat(answers["pnc"]).isEqualTo(listOf("A/1234560BA"))
      assertThat(answers["ethnicity"]).isEqualTo(listOf("Asian or Asian British: Pakistani"))
      assertThat(answers["gender"]).isEqualTo(listOf("MALE"))
      assertThat(answers["gender_identity"]).isEqualTo(listOf("NON_BINARY"))
      assertThat(answers["contact_email_addresses"]).isEqualTo(listOf("address1@gmail.com"))
      assertThat(answers["contact_mobile_phone_number"]).isEqualTo(listOf("071838893"))
      assertThat(answers["contact_phone_number"]).isEqualTo(listOf("0123456999"))
      assertThat(answers["contact_address_building_name"]).isEqualTo(listOf("HMPPS Digital Studio"))
      assertThat(answers["contact_address_house_number"]).isEqualTo(listOf("32"))
      assertThat(answers["contact_address_street_name"]).isEqualTo(listOf("Scotland Street"))
      assertThat(answers["contact_address_district"]).isEqualTo(listOf("Sheffield City Centre"))
      assertThat(answers["contact_address_town_or_city"]).isEqualTo(listOf("Sheffield"))
      assertThat(answers["contact_address_county"]).isEqualTo(listOf("South Yorkshire"))
      assertThat(answers["contact_address_postcode"]).isEqualTo(listOf("S3 7BS"))
      assertThat(answers["language"]).isEqualTo(listOf("Urdu"))
      assertThat(answers["requires_interpreter"]).isEqualTo(listOf("true"))

      assertThat(answers["allergies"]).isEqualTo(listOf("YES"))
      assertThat(answers["allergies_details"]).isEqualTo(listOf("Comment added by Natalie Wood on 14/03/2023 at 10:07\nNut Allergy"))
      assertThat(answers["pregnancy"]).isEqualTo(listOf("NO"))
      assertThat(answers["pregnancy_pregnant_details"]).isEqualTo(emptyList<String>())
      assertThat(answers["caring_commitments"]).isEqualTo(listOf("YES"))
      assertThat(answers["caring_commitments_details"]).isEqualTo(listOf("Comment added by Natalie Wood on 14/03/2023 at 10:07\nPrimary Carer"))
      assertThat(answers["reading_writing_difficulties"]).isEqualTo(listOf("YES"))
      assertThat(answers["reading_writing_difficulties_details"]).isEqualTo(listOf("Comment added by Natalie Wood on 14/03/2023 at 10:06\nCannot read\n---------------------------------------------------------\nComment added by Natalie Wood on 14/03/2023 at 10:06\nNumeracy difficulties\n---------------------------------------------------------\nComment added by Natalie Wood on 14/03/2023 at 10:06\nCommunication difficulties"))

      val contact = getStructuredDataFromAnswer(answers, "emergency_contact_details")
      assertThat(contact["emergency_contact_first_name"]).isEqualTo(listOf("Brian"))
      assertThat(contact["emergency_contact_family_name"]).isEqualTo(listOf("Smith"))
      assertThat(contact["emergency_contact_relationship"]).isEqualTo(listOf("Father"))
      assertThat(contact["emergency_contact_address_house_number"]).isEqualTo(listOf("36"))
      assertThat(contact["emergency_contact_address_street_name"]).isEqualTo(listOf("Fifth Street"))
      assertThat(contact["emergency_contact_address_district"]).isEqualTo(listOf("South City Centre"))
      assertThat(contact["emergency_contact_address_town_or_city"]).isEqualTo(listOf("London"))
      assertThat(contact["emergency_contact_address_county"]).isEqualTo(listOf("South London"))
      assertThat(contact["emergency_contact_mobile_phone_number"]).isEqualTo(listOf("07333567890"))
      assertThat(contact["emergency_contact_address_postcode"]).isEqualTo(listOf("W10 4DN"))
      assertThat(contact["emergency_contact_phone_number"]).isEqualTo(listOf("0133456789"))

      val gp1 = getStructuredDataFromAnswer(answers, "gp_details", 1)
      assertThat(gp1["gp_name"]).isEqualTo(listOf("Nick Riviera"))
      assertThat(gp1["gp_practice_name"]).isEqualTo(emptyList<String>())
      assertThat(gp1["gp_address_building_name"]).isEqualTo(listOf("The practice"))
      assertThat(gp1["gp_address_house_number"]).isEqualTo(listOf("38"))
      assertThat(gp1["gp_address_street_name"]).isEqualTo(listOf("East Street"))
      assertThat(gp1["gp_address_district"]).isEqualTo(listOf("East City Centre"))
      assertThat(gp1["gp_address_town_or_city"]).isEqualTo(listOf("Bristol"))
      assertThat(gp1["gp_address_county"]).isEqualTo(listOf("East London"))
      assertThat(gp1["gp_address_postcode"]).isEqualTo(listOf("E5 7BS"))
      assertThat(gp1["gp_phone_number"]).isEqualTo(listOf("0233456789"))

      val gp2 = getStructuredDataFromAnswer(answers, "gp_details")
      assertThat(gp2["gp_name"]).isEqualTo(listOf("Steve Wilson"))
      assertThat(gp2["gp_address_building_name"]).isEqualTo(listOf("The Building"))
      assertThat(gp2["gp_address_house_number"]).isEqualTo(listOf("77"))
      assertThat(gp2["gp_address_street_name"]).isEqualTo(listOf("Some Street"))
      assertThat(gp2["gp_address_district"]).isEqualTo(listOf("Some City Centre"))
      assertThat(gp2["gp_address_town_or_city"]).isEqualTo(listOf("London"))
      assertThat(gp2["gp_address_county"]).isEqualTo(listOf("Essex"))
      assertThat(gp2["gp_address_postcode"]).isEqualTo(listOf("NW10 1EP"))
      assertThat(gp2["gp_phone_number"]).isEqualTo(listOf("0776 666 6666"))
    }

    private fun getStructuredDataFromAnswer(answers: AnswersDto, questionCode: String, position: Int = 0): Map<*, *> {
      val structuredAnswer = answers[questionCode] as List<*>
      return structuredAnswer[position] as Map<*, *>
    }

    @Test
    fun `creating a new UPW assessment from Delius returns disabilities`() {
      val assessment = createDeliusAssessment(crn, eventID)

      val answers = assessment?.episodes?.first()?.answers.orEmpty()

      val activeDisabilities = answers["current_disabilities"].orEmpty().map { objectMapper().readValue(toJsonString(it), DisabilityAnswerDto::class.java) }
      assertThat(activeDisabilities).hasSize(3)

      val learning = activeDisabilities[0]
      assertThat(learning.type.code).isEqualTo("LDI")
      assertThat(learning.type.description).isEqualTo("Learning Disability")
      assertThat(learning.condition.code).isEqualTo("LD2")
      assertThat(learning.condition.description).isEqualTo("Learning Disability Condition")
      assertThat(learning.notes).isEqualTo("notes about this disability")

      val mentalHealth = activeDisabilities[1]
      assertThat(mentalHealth.type.code).isEqualTo("MHR")
      assertThat(mentalHealth.type.description).isEqualTo("Mental Health related disabilities")
      assertThat(mentalHealth.condition.code).isEqualTo("MHR2")
      assertThat(mentalHealth.condition.description).isEqualTo("Mental Health related disabilities condition")
      assertThat(mentalHealth.notes).isEqualTo("notes about this disability")

      val mobility = activeDisabilities[2]
      assertThat(mobility.type.code).isEqualTo("MRD")
      assertThat(mobility.type.description).isEqualTo("Mobility related Disabilities")
      // Test when condition not specified
      assertThat(mobility.condition.code).isEqualTo("EMPTY")
      assertThat(mobility.condition.description).isEqualTo("Not specified")
      assertThat(mobility.notes).isEqualTo("notes about this disability")
    }

    @Test
    fun `creating a new UPW assessment from Delius returns carer commitments`() {
      val assessment = createDeliusAssessment("DX5678B", eventID)

      val answers = assessment?.episodes?.first()?.answers!!

      val activeCarerCommitments = answers["active_carer_commitments"] as List<*>
      assertThat(activeCarerCommitments).hasSize(1)

      val carerCommitment = activeCarerCommitments[0] as Map<*, *>
      assertThat(carerCommitment["description"]).isEqualTo("Dependents")
      assertThat(carerCommitment["code"]).isEqualTo("I")
      assertThat(carerCommitment["subType"]).isEqualTo("Is a Primary Carer")
      assertThat(carerCommitment["subTypeCode"]).isEqualTo("I02")
      assertThat(carerCommitment["notes"]).isEqualTo("Comment added by Natalie Wood on 14/03/2023 at 10:07\nPrimary Carer")
      assertThat(carerCommitment["isEvidenced"]).isEqualTo(true)
    }

    @Test
    fun `should pre-populate answers with the 'mapped' type`() {
      val dto = CreateAssessmentDto(
        crn = "DX5678B",
        deliusEventId = eventID,
        assessmentSchemaCode = AssessmentType.UPW,
      )
      val assessment = webTestClient.post().uri("/assessments")
        .bodyValue(dto)
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentDto>()
        .returnResult()
        .responseBody

      assertThat(assessment?.assessmentUuid).isNotNull
      assertThat(assessment?.episodes).hasSize(1)
      val answers = assessment!!.episodes.first().answers
      assertThat(answers["gender"]).isEqualTo(listOf("MALE"))
      assertThat(answers["gender_identity"]).isEqualTo(listOf("NON_BINARY"))
    }

    @Test
    fun `should not populate pregnancy related details if offender is not pregnant`() {
      val dto = CreateAssessmentDto(
        crn = crn,
        deliusEventId = eventID,
        assessmentSchemaCode = AssessmentType.UPW,
      )
      val assessment = webTestClient.post().uri("/assessments")
        .bodyValue(dto)
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentDto>()
        .returnResult()
        .responseBody

      assertThat(assessment?.assessmentUuid).isNotNull
      assertThat(assessment?.episodes).hasSize(1)
      val answers = assessment!!.episodes.first().answers

      assertThat(answers["pregnancy"]).isEqualTo(listOf("NO"))
      assertThat(answers["pregnancy_pregnant_details"]).isEmpty()
      assertThat(answers["pregnancy_recently_given_birth_details"]).isEmpty()
    }

    @Test
    fun `creating a new assessment from crn and delius event id returns assessment`() {
      val assessment = createDeliusAssessment(crn, eventID)

      assertThat(assessment?.assessmentUuid).isNotNull
      assertThat(assessment?.createdDate).isEqualToIgnoringMinutes(LocalDateTime.now())
    }

    @Test
    fun `creating an assessment for a delius event id and crn when one already exists in ARN returns the existing assessment`() {
      val existingCrn = "CRN1"
      val existingEventId = 123456L
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
        .bodyValue(CreateAssessmentEpisodeDto("Change of Circs", 1L, AssessmentType.UPW))
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      assertThat(episode?.assessmentUuid).isEqualTo(UUID.fromString("49c8d211-68dc-4692-a6e2-d58468127356"))
      assertThat(episode?.created).isEqualToIgnoringMinutes(LocalDateTime.now())

      assertThat(episode?.offence?.offenceCode).isEqualTo("150")
      assertThat(episode?.offence?.codeDescription).isEqualTo("Merchant Shipping Acts")
      assertThat(episode?.offence?.offenceSubCode).isEqualTo("00")
      assertThat(episode?.offence?.subCodeDescription).isEqualTo("Merchant Shipping Acts")
      assertThat(episode?.offence?.sentenceDate).isEqualTo(LocalDate.of(2023, 1, 26))
    }
  }

  private fun createDeliusAssessment(crn: String, deliusId: Long, assessmentSchemaCode: AssessmentType = AssessmentType.UPW): AssessmentDto? {
    val dto = CreateAssessmentDto(
      crn = crn,
      deliusEventId = deliusId,
      assessmentSchemaCode = assessmentSchemaCode,
    )
    return webTestClient.post().uri("/assessments")
      .bodyValue(dto)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentDto>()
      .returnResult()
      .responseBody
  }
}
