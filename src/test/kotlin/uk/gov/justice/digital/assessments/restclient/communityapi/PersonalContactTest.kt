package uk.gov.justice.digital.assessments.restclient.communityapi

// import com.fasterxml.jackson.annotation.JsonInclude
// import com.fasterxml.jackson.databind.DeserializationFeature
// import com.fasterxml.jackson.databind.ObjectMapper
// import com.fasterxml.jackson.databind.SerializationFeature
// import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
// import com.fasterxml.jackson.module.kotlin.KotlinModule
// import com.fasterxml.jackson.module.kotlin.readValue
// import org.assertj.core.api.Assertions.assertThat
// import org.junit.jupiter.api.BeforeEach
// import org.junit.jupiter.api.Test
// import uk.gov.justice.digital.assessments.api.answers.EmergencyContactDetailsAnswerDto
// import uk.gov.justice.digital.assessments.api.answers.GPDetailsAnswerDto
// import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
// import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
// import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
// import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
// import java.time.LocalDateTime
// import java.util.UUID
//
// class PersonalContactTest {
//
//   private lateinit var objectMapper: ObjectMapper
//
//   @BeforeEach
//   fun setUp() {
//     objectMapper = objectMapper()
//   }
//
//   private fun objectMapper(): ObjectMapper {
//     return ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//       .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
//       .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
//       .setSerializationInclusion(JsonInclude.Include.NON_NULL).setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
//       .registerModules(Jdk8Module(), JavaTimeModule(), KotlinModule())
//   }
//
//   @Test
//   fun `should map PersonalContactDto list to episode answers`() {
//     // Given
//     val json = this::class.java.getResource("/json/deliusPersonalContacts.json")?.readText()!!
//     val personalContacts: List<PersonalContact> = objectMapper.readValue(json)
//
//     val episodeEntity = createAssessmentEpisodeEntity()
//
//     // When
//     PersonalContact.from(personalContacts, episodeEntity)
//
//     // Then
//     val answers = episodeEntity.answers
//
//     val gpDetailsAnswer = GPDetailsAnswerDto(
//       name = listOf("Charles Europe"),
//       buildingName = emptyList(),
//       addressNumber = listOf("32"),
//       streetName = listOf("Scotland Street"),
//       town = listOf("Sheffield"),
//       telephoneNumber = listOf("020 2123 5678"),
//       district = listOf("Sheffield"),
//       county = listOf("South Yorkshire"),
//       postcode = listOf("S3 7DQ")
//     )
//
//     assertThat(answers["gp_details"]).containsExactlyInAnyOrder(gpDetailsAnswer)
//
//     val emergencyContactDetails = EmergencyContactDetailsAnswerDto(
//       firstName = listOf("UPW"),
//       familyName = listOf("TESTING"),
//       relationship = listOf("Friend"),
//       addressNumber = listOf("102"),
//       buildingName = listOf("Petty France"),
//       streetName = listOf("Central London"),
//       district = listOf("London"),
//       town = listOf("London"),
//       county = listOf("London"),
//       postcode = listOf("SW1H 9AJ"),
//       telephoneNumber = listOf("020 2000 0000"),
//       mobileNumber = listOf("07123456789")
//     )
//
//     assertThat(answers["emergency_contact_details"]).containsExactlyInAnyOrder(emergencyContactDetails)
//   }
//
//   private fun createAssessmentEpisodeEntity(): AssessmentEpisodeEntity {
//     val episodeEntity = AssessmentEpisodeEntity(
//       123456L,
//       UUID.randomUUID(),
//       AssessmentEntity(),
//       AssessmentType.UPW,
//       1L,
//       AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
//       LocalDateTime.of(2019, 8, 1, 8, 0),
//       null,
//       "Change of Circs",
//       null
//     )
//     return episodeEntity
//   }
//
//   @Test
//   fun `should map empty delius values to empty list episode answers`() {
//     // Given
//     val personalContacts: List<PersonalContact> = objectMapper.readValue("[]")
//
//     val episodeEntity = createAssessmentEpisodeEntity()
//
//     // When
//     PersonalContact.from(personalContacts, episodeEntity)
//
//     // Then
//     val answers = episodeEntity.answers
//     assertThat(answers["gp_details"]).isEmpty()
//     assertThat(answers["emergency_contact_details"]).isEmpty()
//   }
// }
