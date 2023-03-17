package uk.gov.justice.digital.assessments.restclient.deliusintegrationapi

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.answers.CarerCommitmentsAnswerDto
import uk.gov.justice.digital.assessments.api.answers.DisabilityAnswerDto
import uk.gov.justice.digital.assessments.api.answers.EmergencyContactDetailsAnswerDto
import uk.gov.justice.digital.assessments.api.answers.GPDetailsAnswerDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import java.time.LocalDateTime
import java.util.UUID

class CaseDetailsDtoTest {

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
      .registerModules(
        Jdk8Module(), JavaTimeModule(), KotlinModule.Builder().build()
      )
  }

  @Test
  fun `should map empty delius values to empty list episode answers`() {
    val json = this::class.java.getResource("/json/caseDetails.json")?.readText()
    val caseDetails = objectMapper.readValue(json, CaseDetails::class.java)
    val episodeEntity = createAssessmentEpisodeEntity()

    // When
    episodeEntity.updateFrom(caseDetails)

    // Then
    val answers = episodeEntity.answers
    assertThat(answers["pnc"]).isEmpty()
    assertThat(answers["ethnicity"]).isEmpty()
    assertThat(answers["language"]).isEmpty()
    assertThat(answers["gender_identity"]).isEmpty()
    assertThat(answers["first_name"]).isEmpty()
    assertThat(answers["family_name"]).isEmpty()
    assertThat(answers["crn"]).isEmpty()
    assertThat(answers["requires_interpreter"]).isEqualTo(listOf("false"))
    assertThat(answers["contact_email_addresses"]).isEmpty()
    assertThat(answers["dob_aliases"]).isEmpty()
    assertThat(answers["contact_address_building_name"]).isEmpty()
    assertThat(answers["contact_address_house_number"]).isEmpty()
    assertThat(answers["contact_address_street_name"]).isEmpty()
    assertThat(answers["contact_address_district"]).isEmpty()
    assertThat(answers["contact_address_town_or_city"]).isEmpty()
    assertThat(answers["contact_address_county"]).isEmpty()
    assertThat(answers["contact_address_postcode"]).isEmpty()
    assertThat(answers["contact_mobile_phone_number"]).isEmpty()
    assertThat(answers["contact_phone_number"]).isEmpty()
    assertThat(answers["physical_disability"]).isEmpty()
    assertThat(answers["physical_disability_details"]).isEmpty()
    assertThat(answers["learning_disability"]).isEmpty()
    assertThat(answers["learning_disability_details"]).isEmpty()
    assertThat(answers["learning_difficulty"]).isEmpty()
    assertThat(answers["learning_difficulty_details"]).isEmpty()
    assertThat(answers["mental_health_condition"]).isEmpty()
    assertThat(answers["mental_health_condition_details"]).isEmpty()
    assertThat(answers["active_disabilities"]).isEmpty()
  }

  @Test
  fun `should map CommunityOffenderDto to episode answers`() {
    // Given
    val json = this::class.java.getResource("/json/caseDetails.json")?.readText()
    val caseDetails = objectMapper.readValue(json, CaseDetails::class.java)

    val episodeEntity = createAssessmentEpisodeEntity()

    // When
    episodeEntity.updateFrom(caseDetails)

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

  // -------------------

  @Test
  fun `should map empty delius personal circumstance values to empty list episode answers`() {
    // Given
    val json = this::class.java.getResource("/json/caseDetailsEmptyPersonalCircumstances.json")?.readText()
    val caseDetails = objectMapper.readValue(json, CaseDetails::class.java)
    val episodeEntity = createAssessmentEpisodeEntity()

    // When
    episodeEntity.updateFrom(caseDetails)

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
    val json = this::class.java.getResource("/json/caseDetailsEmptyPersonalCircumstances.json")?.readText()
    val caseDetails = objectMapper.readValue(json, CaseDetails::class.java)
    val episodeEntity = createAssessmentEpisodeEntity()

    // When
    episodeEntity.updateFrom(caseDetails)
    // Then
    val answers = episodeEntity.answers
    assertThat(answers["pregnancy"]).isEqualTo(listOf("NO"))
  }

  @Test
  fun `should map personal circumstance values to episode answers`() {
    // Given
    val json = this::class.java.getResource("/json/caseDetails.json")?.readText()
    val caseDetails = objectMapper.readValue(json, CaseDetails::class.java)

    val episodeEntity = createAssessmentEpisodeEntity()

    // When
    episodeEntity.updateFrom(caseDetails)

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
    assertThat(answers["reading_writing_difficulties_details"])
      .isEqualTo(listOf("Cannot read", "Numeracy difficulties", "Communication difficulties"))
  }

  // -----------------------

  @Test
  fun `should map Personal Contacts to episode answers`() {
    // Given
    val json = this::class.java.getResource("/json/caseDetails.json")?.readText()!!
    val caseDetails: CaseDetails = objectMapper.readValue(json)

    val episodeEntity = createAssessmentEpisodeEntity()

    // When
    episodeEntity.updateFrom(caseDetails)

    // Then
    val answers = episodeEntity.answers

    val gpDetailsAnswer = GPDetailsAnswerDto(
      name = listOf("Charles Europe"),
      buildingName = emptyList(),
      addressNumber = listOf("32"),
      streetName = listOf("Scotland Street"),
      town = listOf("Sheffield"),
      telephoneNumber = listOf("020 2123 5678"),
      district = listOf("Sheffield"),
      county = listOf("South Yorkshire"),
      postcode = listOf("S3 7DQ")
    )

    assertThat(answers["gp_details"]).containsExactlyInAnyOrder(gpDetailsAnswer)

    val emergencyContactDetails = EmergencyContactDetailsAnswerDto(
      firstName = listOf("UPW"),
      familyName = listOf("TESTING"),
      relationship = listOf("Friend"),
      addressNumber = listOf("102"),
      buildingName = listOf("Petty France"),
      streetName = listOf("Central London"),
      district = listOf("London"),
      town = listOf("London"),
      county = listOf("London"),
      postcode = listOf("SW1H 9AJ"),
      telephoneNumber = listOf("020 2000 0000"),
      mobileNumber = listOf("07123456789")
    )

    assertThat(answers["emergency_contact_details"]).containsExactlyInAnyOrder(emergencyContactDetails)
  }

  @Test
  fun `should map empty delius personal contacts values to empty list episode answers`() {
    // Given
    val json = this::class.java.getResource("/json/caseDetails.json")?.readText()!!
    val caseDetails: CaseDetails = objectMapper.readValue(json)

    val episodeEntity = createAssessmentEpisodeEntity()

    // When
    episodeEntity.updateFrom(caseDetails)

    // Then
    val answers = episodeEntity.answers
    assertThat(answers["gp_details"]).isEmpty()
    assertThat(answers["emergency_contact_details"]).isEmpty()
  }

  private fun createAssessmentEpisodeEntity(): AssessmentEpisodeEntity {
    return AssessmentEpisodeEntity(
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
  }
}
