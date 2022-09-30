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
import uk.gov.justice.digital.assessments.api.CarerCommitmentsAnswerDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import java.time.LocalDateTime
import java.util.UUID

class DeliusPersonalCircumstanceDtoTest {

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
  fun `should map empty delius values to empty list episode answers`() {
    // Given
    val json = this::class.java.getResource("/json/deliusPersonalCircumstancesEmptyValues.json")?.readText()
    val personalCircumstancesDto = objectMapper.readValue(json, DeliusPersonalCircumstancesDto::class.java)
    val episodeEntity = createAssessmentEpisodeEntity()

    // When
    DeliusPersonalCircumstancesDto.from(personalCircumstancesDto, episodeEntity)

    // Then
    val answers = episodeEntity.answers
    assertThat(answers["active_carer_commitments"]).isEmpty()
    assertThat(answers["allergies"]).isEmpty()
    assertThat(answers["allergies_details"]).isEmpty()
    assertThat(answers["caring_commitments"]).isEmpty()
    assertThat(answers["caring_commitments_details"]).isEmpty()
    assertThat(answers["language_communication_concerns"]).isEmpty()
    assertThat(answers["language_communication_concerns_details"]).isEmpty()
    assertThat(answers["numeracy_concerns"]).isEmpty()
    assertThat(answers["numeracy_concerns_details"]).isEmpty()
    assertThat(answers["pregnancy_pregnant_details"]).isEmpty()
    assertThat(answers["pregnancy_recently_given_birth_details"]).isEmpty()
    assertThat(answers["reading_literacy_concerns"]).isEmpty()
    assertThat(answers["reading_literacy_concerns_details"]).isEmpty()
    assertThat(answers["reading_writing_difficulties"]).isEmpty()
    assertThat(answers["reading_writing_difficulties_details"]).isEmpty()
  }

  @Test
  fun `should map to NO answer for pregnancy when pregnancy information is not present`() {
    // Given
    val personalCircumstancesDto = DeliusPersonalCircumstancesDto(emptyList())
    val episodeEntity = createAssessmentEpisodeEntity()

    // When
    DeliusPersonalCircumstancesDto.from(personalCircumstancesDto, episodeEntity)

    // Then
    val answers = episodeEntity.answers
    assertThat(answers["pregnancy"]).isEqualTo(listOf("NO"))
  }

  @Test
  fun `should map DeliusPersonalCircumstancesDto to episode answers`() {
    // Given
    val json = this::class.java.getResource("/json/deliusPersonalCircumstances.json")?.readText()
    val personalCircumstancesDto = objectMapper.readValue(json, DeliusPersonalCircumstancesDto::class.java)

    val episodeEntity = createAssessmentEpisodeEntity()

    // When
    DeliusPersonalCircumstancesDto.from(personalCircumstancesDto, episodeEntity)

    // Then
    val answers = episodeEntity.answers

    val carerCommitmentsAnswerDto = CarerCommitmentsAnswerDto(
      description = "Dependents",
      code = "I",
      notes = "Primary Carer",
      subType = "Is a Primary Carer",
      subTypeCode = "I02",
      isEvidenced = true
    )
    assertThat(answers["active_carer_commitments"]).isEqualTo(listOf(carerCommitmentsAnswerDto))
    assertThat(answers["allergies"]).isEqualTo(listOf("YES"))
    assertThat(answers["allergies_details"]).isEqualTo(listOf("Nut Allergy"))
    assertThat(answers["caring_commitments"]).isEqualTo(listOf("YES"))
    assertThat(answers["caring_commitments_details"]).isEqualTo(listOf("Primary Carer"))
    assertThat(answers["language_communication_concerns"]).isEqualTo(listOf("YES"))
    assertThat(answers["language_communication_concerns_details"]).isEqualTo(listOf("Communication difficulties"))
    assertThat(answers["numeracy_concerns"]).isEqualTo(listOf("YES"))
    assertThat(answers["numeracy_concerns_details"]).isEqualTo(listOf("Numeracy difficulties"))
    assertThat(answers["pregnancy"]).isEqualTo(listOf("RECENTLY_GIVEN_BIRTH"))
    assertThat(answers["pregnancy_pregnant_details"]).isEmpty()
    assertThat(answers["pregnancy_recently_given_birth_details"]).isEqualTo(listOf("Recently given birth"))
    assertThat(answers["reading_literacy_concerns"]).isEqualTo(listOf("YES"))
    assertThat(answers["reading_literacy_concerns_details"]).isEqualTo(listOf("Cannot read"))
    assertThat(answers["reading_writing_difficulties"]).isEqualTo(listOf("YES"))
    assertThat(answers["reading_writing_difficulties_details"]).isEqualTo(listOf("Cannot read", "Numeracy difficulties", "Communication difficulties"))
  }

  private fun createAssessmentEpisodeEntity(): AssessmentEpisodeEntity {
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
    return episodeEntity
  }
}
