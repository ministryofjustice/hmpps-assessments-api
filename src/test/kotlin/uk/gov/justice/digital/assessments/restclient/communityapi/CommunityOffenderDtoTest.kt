package uk.gov.justice.digital.assessments.restclient.communityapi

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.DisabilityAnswerDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import java.time.LocalDateTime
import java.util.UUID

class CommunityOffenderDtoTest {

  private lateinit var objectMapper: ObjectMapper

  @BeforeEach
  fun setUp() {
    objectMapper = objectMapper()
  }

  private fun objectMapper(): ObjectMapper {
    return ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .setSerializationInclusion(JsonInclude.Include.NON_NULL).setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
      .registerModules(Jdk8Module(), JavaTimeModule(), KotlinModule())
  }

  @Test
  fun `should map CommunityOffenderDTo to episode answers`() {
    // Given
    val json = this::class.java.getResource("/json/deliusOffender.json")?.readText()
    val offenderDto = objectMapper.readValue(json, CommunityOffenderDto::class.java)

    val episodeEntity = AssessmentEpisodeEntity(
      123456L,
      UUID.randomUUID(),
      AssessmentEntity(),
      AssessmentType.UPW,
      1L,
      AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      LocalDateTime.of(2019, 8, 1, 8, 0),
      null,
      "Change of Circs",
      null
    )

    // When
    CommunityOffenderDto.from(offenderDto, episodeEntity)

    // Then
    val answers = episodeEntity.answers
    assertThat(answers["first_name"]).isEqualTo(listOf("John"))
    assertThat(answers["first_name_aliases"]).isEqualTo(listOf("Robert"))
    assertThat(answers["family_name"]).isEqualTo(listOf("Smith"))
    assertThat(answers["family_name_aliases"]).isEqualTo(listOf("De Niro"))
    assertThat(answers["dob"]).isEqualTo(listOf("1982-10-24"))
    assertThat(answers["dob_aliases"]).isEqualTo(listOf("2022-05-19"))
    assertThat(answers["contact_address_building_name"]).isEqualTo(listOf("HMPPS Digital Studio"))
    assertThat(answers["contact_address_house_number"]).isEqualTo(listOf("32"))
    assertThat(answers["contact_address_street_name"]).isEmpty()
    assertThat(answers["contact_address_district"]).isEqualTo(listOf("Sheffield City Centre"))
    assertThat(answers["contact_address_town_or_city"]).isEqualTo(listOf("Sheffield"))
    assertThat(answers["contact_address_county"]).isEqualTo(listOf("South Yorkshire"))
    assertThat(answers["contact_address_postcode"]).isEqualTo(listOf("S3 7BS"))
    assertThat(answers["crn"]).isEqualTo(listOf("12345C"))
    assertThat(answers["pnc"]).isEqualTo(listOf("2004/0712343H"))
    assertThat(answers["ethnicity"]).isEqualTo(listOf("white"))
    assertThat(answers["gender"]).isEqualTo(listOf("MALE"))
    assertThat(answers["gender_identity"]).isEqualTo(listOf("PREFER_TO_SELF_DESCRIBE"))
    assertThat(answers["language"]).isEqualTo(listOf("Welsh"))
    assertThat(answers["requires_interpreter"]).isEqualTo(listOf("true"))
    assertThat(answers["contact_email_addresses"]).isEqualTo(listOf("jsmith@email.com"))
    assertThat(answers["contact_mobile_phone_number"]).isEqualTo(listOf("07999123456"))
    assertThat(answers["contact_phone_number"]).isEqualTo(listOf("0207123556"))
    assertThat(answers["physical_disability"]).isEqualTo(listOf("PC"))
    assertThat(answers["physical_disability_details"]).isEqualTo(listOf("Physical disability"))
    assertThat(answers["learning_disability"]).isEqualTo(listOf("LA"))
    assertThat(answers["learning_disability_details"]).isEqualTo(listOf("Learning disability"))
    assertThat(answers["learning_difficulty"]).isEqualTo(listOf("LD"))
    assertThat(answers["learning_difficulty_details"]).isEqualTo(listOf("Learning difficulty"))
    assertThat(answers["mental_health_condition"]).isEqualTo(listOf("M1"))
    assertThat(answers["mental_health_condition_details"]).isEqualTo(listOf("Mental health condition"))
    assertThat(answers["active_disabilities"]).containsExactlyInAnyOrder(
      DisabilityAnswerDto(code = "LA", description = "Learning disability", notes = "notes", emptyList()),
      DisabilityAnswerDto(code = "LD", description = "Learning difficulty", notes = "notes", emptyList()),
      DisabilityAnswerDto(code = "PC", description = "Physical disability", notes = "notes", emptyList())
    )
  }
}
