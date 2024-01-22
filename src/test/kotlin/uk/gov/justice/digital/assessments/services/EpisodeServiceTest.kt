package uk.gov.justice.digital.assessments.services

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.answers.EmergencyContactDetailsAnswerDto
import uk.gov.justice.digital.assessments.api.answers.GPDetailsAnswerDto
import uk.gov.justice.digital.assessments.api.groups.GroupQuestionDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType.UPW
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.CloneAssessmentExcludedQuestionsEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.CloneAssessmentExcludedQuestionsRepository
import uk.gov.justice.digital.assessments.restclient.AuditType
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Address
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Name
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.PersonalContact
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.RelationshipType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Episode Service Tests")
class EpisodeServiceTest {

  private val assessmentReferenceDataService: AssessmentReferenceDataService = mockk()
  private val cloneAssessmentExcludedQuestionsRepository: CloneAssessmentExcludedQuestionsRepository = mockk()
  private val telemetryService: TelemetryService = mockk()
  private val auditService: AuditService = mockk()

  private val episodeService = EpisodeService(
    assessmentReferenceDataService,
    cloneAssessmentExcludedQuestionsRepository,
    telemetryService,
    auditService,
  )

  private lateinit var newEpisode: AssessmentEpisodeEntity

  private val authorEntity = AuthorEntity(
    userId = "1",
    userName = "USER",
    userAuthSource = "source",
    userFullName = "full name",
  )

  private lateinit var objectMapper: ObjectMapper

  companion object {
    private const val CRN: String = "someCrn"
  }

  @BeforeEach
  fun setup() {
    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentType(UPW) } returns emptyList()
    newEpisode = createEpisode(UPW)
    objectMapper = objectMapper()
  }

  private fun objectMapper(): ObjectMapper {
    return ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .setSerializationInclusion(JsonInclude.Include.NON_NULL)
      .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
      .registerModule(Jdk8Module())
      .registerModule(JavaTimeModule())
      .registerKotlinModule()
  }

  private fun createEpisode(assessmentType: AssessmentType): AssessmentEpisodeEntity {
    return AssessmentEpisodeEntity(
      episodeId = 1,
      assessmentType = assessmentType,
      author = authorEntity,
      assessment = AssessmentEntity(
        subject = SubjectEntity(
          crn = CRN,
          dateOfBirth = LocalDate.parse("1999-12-31"),
        ),
      ),
    )
  }

  @Test
  fun `should pre-populate gender identity answers from Delius as an external source for a UPW assessment type`() {
    newEpisode = createEpisode(UPW)
    val caseDetails = CaseDetails(
      crn = "crn",
      name = Name(
        forename = "forename",
        middleName = "middlename",
        surname = "surname",
      ),
      dateOfBirth = LocalDate.of(1989, 1, 1),
      genderIdentity = "PREFER TO SELF DESCRIBE",
    )
    episodeService.prePopulateEpisodeFromDelius(newEpisode, caseDetails)

    assertThat(newEpisode.answers).containsEntry("gender_identity", listOf("PREFER_TO_SELF_DESCRIBE"))
  }

  @Test
  fun `should not add contact address from previous episode if present in Delius`() {
    // Given
    newEpisode = createEpisode(UPW)
    val caseDetails = caseDetails()

    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentType(UPW) } returns emptyList()

    justRun { auditService.createAuditEvent(AuditType.ARN_ASSESSMENT_CLONED, any(), any(), any(), any(), any()) }
    justRun { telemetryService.trackAssessmentClonedEvent(any(), any(), any(), any(), any(), any(), any()) }

    val questionDtos = listOf(
      GroupQuestionDto(questionCode = "contact_address_house_number"),
      GroupQuestionDto(questionCode = "contact_address_building_name"),
      GroupQuestionDto(questionCode = "contact_address_street_name"),
      GroupQuestionDto(questionCode = "contact_address_postcode"),
      GroupQuestionDto(questionCode = "contact_address_town_or_city"),
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questionDtos

    val previousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentType = UPW,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(
          "contact_address_house_number" to listOf("12"),
          "contact_address_building_name" to listOf("The Gables"),
          "contact_address_street_name" to listOf("High Street"),
          "contact_address_postcode" to listOf("S11 8JK"),
          "contact_address_town_or_city" to listOf("Leeds"),
        ),
      ),
    )

    // When
    episodeService.prePopulateEpisodeFromDelius(newEpisode, caseDetails)
    episodeService.prePopulateFromPreviousEpisodes(newEpisode, previousEpisodes)

    // Then
    val expectedAnswers = mutableMapOf(
      "contact_address_house_number" to listOf("32"),
      "contact_address_building_name" to listOf("HMPPS Digital Studio"),
      "contact_address_street_name" to emptyList(),
      "contact_address_postcode" to listOf("S3 7BS"),
      "contact_address_town_or_city" to listOf("Sheffield"),
    )
    assertThat(newEpisode.answers).containsAllEntriesOf(expectedAnswers)
  }

  @Test
  fun `should not add personal contacts from previous episode if present in Delius`() {
    // Given
    newEpisode = createEpisode(UPW)
    val caseDetails = caseDetails()

    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentType(UPW) } returns emptyList()

    val questionDtos = listOf(
      GroupQuestionDto(questionCode = "gp_details"),
      GroupQuestionDto(questionCode = "emergency_contact_details"),
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questionDtos

    justRun { auditService.createAuditEvent(AuditType.ARN_ASSESSMENT_CLONED, any(), any(), any(), any(), any()) }
    justRun { telemetryService.trackAssessmentClonedEvent(any(), any(), any(), any(), any(), any(), any()) }

    val previousEpisodes = createPreviousEpisodePersonalContacts()

    // When
    episodeService.prePopulateEpisodeFromDelius(newEpisode, caseDetails)
    episodeService.prePopulateFromPreviousEpisodes(newEpisode, previousEpisodes)

    // Then
    val gpDetailsAnswerDtos = newEpisode.answers["gp_details"] as List<GPDetailsAnswerDto>
    assertThat(gpDetailsAnswerDtos.size).isEqualTo(1)
    assertThat(gpDetailsAnswerDtos[0].name).isEqualTo(listOf("Charles Europe"))
    assertThat(gpDetailsAnswerDtos[0].buildingName).isEmpty()

    val emergencyContactDetailsAnswerDtos =
      newEpisode.answers["emergency_contact_details"] as List<EmergencyContactDetailsAnswerDto>
    assertThat(emergencyContactDetailsAnswerDtos.size).isEqualTo(1)
    assertThat(emergencyContactDetailsAnswerDtos[0].relationship).isEqualTo(listOf("Emergency Contact"))
    assertThat(emergencyContactDetailsAnswerDtos[0].buildingName).isEqualTo(listOf("Petty France"))
  }

  private fun createPreviousEpisodePersonalContacts() = listOf(
    AssessmentEpisodeEntity(
      episodeId = 2,
      assessmentType = UPW,
      author = authorEntity,
      assessment = AssessmentEntity(),
      endDate = LocalDateTime.now().minusDays(1),
      answers = mutableMapOf(
        "gp_details" to listOf(
          GPDetailsAnswerDto(
            name = listOf("Some previous episode name"),
            buildingName = listOf("Some previous episode building name"),
          ),
          GPDetailsAnswerDto(
            name = listOf("Some other previous episode name"),
            buildingName = listOf("Some other previous episode building name"),
          ),
        ),
        "emergency_contact_details" to listOf(
          EmergencyContactDetailsAnswerDto(
            relationship = listOf("Some previous episode relationship"),
            buildingName = listOf("Some previous episode building name"),
          ),
          EmergencyContactDetailsAnswerDto(
            relationship = listOf("Some other previous episode relationship"),
            buildingName = listOf("Some other previous episode building name"),
          ),
        ),
      ),
    ),
  )

  @Test
  fun `should not add answers from previous episode if present in Delius`() {
    // Given
    newEpisode = createEpisode(UPW)
    val caseDetails = caseDetails()

    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentType(UPW) } returns emptyList()

    justRun { auditService.createAuditEvent(AuditType.ARN_ASSESSMENT_CLONED, any(), any(), any(), any(), any()) }
    justRun { telemetryService.trackAssessmentClonedEvent(any(), any(), any(), any(), any(), any(), any()) }

    val questionDtos = listOf(
      GroupQuestionDto(questionCode = "gender_identity"),
      GroupQuestionDto(questionCode = "question_2"),
    )

    val previousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentType = UPW,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(
          "gender_identity" to listOf("NON_BINARY"),
          "question_2" to listOf("answer_2"),
        ),
      ),
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questionDtos

    // When
    episodeService.prePopulateEpisodeFromDelius(newEpisode, caseDetails)
    episodeService.prePopulateFromPreviousEpisodes(newEpisode, previousEpisodes)

    // Then
    val expectedAnswers = mutableMapOf(
      "gender_identity" to listOf("PREFER_TO_SELF_DESCRIBE"),
      "question_2" to listOf("answer_2"),
    )
    assertThat(newEpisode.answers).containsAllEntriesOf(expectedAnswers)
  }

  @Test
  fun `copies answers from previous episode ignoring excluded questions`() {
    val schemaQuestions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
    )

    val mixedPreviousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentType = UPW,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(
          "question_1" to listOf("answer_1"),
          "question_2" to listOf("answer_2"),
        ),
      ),
    )
    justRun { auditService.createAuditEvent(AuditType.ARN_ASSESSMENT_CLONED, any(), any(), any(), any(), any()) }
    justRun { telemetryService.trackAssessmentClonedEvent(any(), any(), any(), any(), any(), any(), any()) }
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns schemaQuestions
    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentType(UPW) } returns listOf(
      CloneAssessmentExcludedQuestionsEntity(
        12345,
        UPW,
        "question_2",
      ),
    )

    val result = episodeService.prePopulateFromPreviousEpisodes(newEpisode, mixedPreviousEpisodes)

    val expectedAnswers = mutableMapOf(
      "question_1" to listOf("answer_1"),
    )

    assertThat(result.answers).containsExactlyEntriesOf(expectedAnswers)
  }

  @Test
  fun `copies answers from previous episode`() {
    // Given
    justRun { auditService.createAuditEvent(AuditType.ARN_ASSESSMENT_CLONED, any(), any(), any(), any(), any()) }
    justRun { telemetryService.trackAssessmentClonedEvent(any(), any(), any(), any(), any(), any(), any()) }
    val questions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
    )

    val mixedPreviousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentType = UPW,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(
          "question_1" to listOf("answer_1"),
          "question_2" to listOf("answer_2"),
        ),
      ),
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questions

    // When
    val result = episodeService.prePopulateFromPreviousEpisodes(newEpisode, mixedPreviousEpisodes)

    val expectedAnswers = mutableMapOf(
      "question_1" to listOf("answer_1"),
      "question_2" to listOf("answer_2"),
    )

    // Then
    assertThat(result.answers).containsExactlyInAnyOrderEntriesOf(expectedAnswers)
  }

  @Test
  fun `new episode unchanged as no previous episodes`() {
    val questions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questions

    val emptyPreviousEpisode = emptyList<AssessmentEpisodeEntity>()

    val result = episodeService.prePopulateFromPreviousEpisodes(newEpisode, emptyPreviousEpisode).answers

    assertThat(result).isEmpty()
  }

  @Test
  fun `new episode unchanged as empty answers in previous episodes`() {
    val previousEpisodesNoAnswers = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentType = UPW,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(),
      ),
    )

    val questions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questions
    justRun { auditService.createAuditEvent(AuditType.ARN_ASSESSMENT_CLONED, any(), any(), any(), any(), any()) }
    justRun { telemetryService.trackAssessmentClonedEvent(any(), any(), any(), any(), any(), any(), any()) }

    val result = episodeService.prePopulateFromPreviousEpisodes(newEpisode, previousEpisodesNoAnswers).answers

    assertThat(result).isEmpty()
  }

  @Test
  fun `existing episode older than 55 weeks will be ignored`() {
    justRun { auditService.createAuditEvent(AuditType.ARN_ASSESSMENT_CLONED, any(), any(), any(), any(), any()) }
    justRun { telemetryService.trackAssessmentClonedEvent(any(), any(), any(), any(), any(), any(), any()) }

    val previousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentType = UPW,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusWeeks(1),
        answers = mutableMapOf(
          "question_1" to listOf("answer_1"),
          "question_2" to listOf("answer_2"),
        ),
      ),
      AssessmentEpisodeEntity(
        episodeId = 3,
        assessmentType = UPW,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusWeeks(55).minusDays(1),
        answers = mutableMapOf(
          "question_3" to listOf("answer_3"),
          "question_4" to listOf("answer_4"),
        ),
      ),
    )

    val schemaQuestions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
      GroupQuestionDto(questionCode = "question_3"),
      GroupQuestionDto(questionCode = "question_4"),
    )

    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns schemaQuestions

    val result = episodeService.prePopulateFromPreviousEpisodes(newEpisode, previousEpisodes).answers

    val expectedAnswers = mutableMapOf(
      "question_1" to listOf("answer_1"),
      "question_2" to listOf("answer_2"),
    )

    assertThat(result).containsExactlyInAnyOrderEntriesOf(expectedAnswers)
    assertThat(result).doesNotContainKey("question_3")
    assertThat(result).doesNotContainKey("question_4")
  }

  @Nested
  @DisplayName("telemetry and audit episode cloning")
  inner class AuditEpisodeCloning {
    @Test
    fun `submits audit event when new episode cloned from previous episode`() {
      val questions = listOf(
        GroupQuestionDto(questionCode = "question_1"),
        GroupQuestionDto(questionCode = "question_2"),
      )

      val previousEpisodeEndDate = LocalDateTime.now().minusDays(1)
      val previousEpisodeUuid = UUID.randomUUID()

      val mixedPreviousEpisodes = listOf(
        AssessmentEpisodeEntity(
          episodeId = 2,
          episodeUuid = previousEpisodeUuid,
          assessmentType = UPW,
          author = authorEntity,
          assessment = AssessmentEntity(),
          endDate = previousEpisodeEndDate,
          answers = mutableMapOf(
            "question_1" to listOf("answer_1"),
            "question_2" to listOf("answer_2"),
          ),
        ),
      )
      every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questions
      justRun { auditService.createAuditEvent(AuditType.ARN_ASSESSMENT_CLONED, any(), any(), any(), any(), any()) }
      justRun { telemetryService.trackAssessmentClonedEvent(any(), any(), any(), any(), any(), any(), any()) }

      episodeService.prePopulateFromPreviousEpisodes(newEpisode, mixedPreviousEpisodes)

      verify(exactly = 1) {
        auditService.createAuditEvent(
          AuditType.ARN_ASSESSMENT_CLONED,
          any(),
          newEpisode.episodeUuid,
          CRN,
          authorEntity,
          mapOf(
            "previousEpisodeUUID" to previousEpisodeUuid,
            "previousEpisodeCompletedDate" to previousEpisodeEndDate,
          ),
        )
      }
      verify(exactly = 1) {
        telemetryService.trackAssessmentClonedEvent(
          CRN,
          authorEntity,
          any(),
          newEpisode.episodeUuid,
          UPW,
          previousEpisodeUuid,
          previousEpisodeEndDate,
        )
      }
    }
  }

  @Test
  fun `no audit event when new episode unchanged as no previous episodes`() {
    val questions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questions

    val emptyPreviousEpisode = emptyList<AssessmentEpisodeEntity>()

    episodeService.prePopulateFromPreviousEpisodes(newEpisode, emptyPreviousEpisode).answers

    verify(exactly = 0) { auditService.createAuditEvent(any(), any(), any(), any(), any()) }
    verify(exactly = 0) {
      telemetryService.trackAssessmentClonedEvent(
        any(),
        any(),
        any(),
        any(),
        any(),
        any(),
        any(),
      )
    }
  }

  private fun caseDetails(): CaseDetails {
    return CaseDetails(
      crn = "crn",
      name = Name(
        forename = "forename",
        middleName = "middlename",
        surname = "surname",
      ),
      dateOfBirth = LocalDate.of(1989, 1, 1),
      genderIdentity = "PREFER TO SELF DESCRIBE",

      mainAddress = Address(
        buildingName = "HMPPS Digital Studio",
        addressNumber = "32",
        district = "Sheffield City Centre",
        county = "South Yorkshire",
        postcode = "S3 7BS",
        town = "Sheffield",
      ),
      personalContacts = listOf(
        PersonalContact(
          relationship = "GP",
          relationshipType = RelationshipType(
            code = "RT02",
            description = "Primary GP",
          ),
          name = Name(
            forename = "Charles",
            surname = "Europe",
          ),
          mobileNumber = "07123456789",
          address = Address(
            addressNumber = "32",
            streetName = "Scotland Street",
            district = "Sheffield",
            town = "Sheffield",
            county = "South Yorkshire",
            postcode = "S3 7DQ",
          ),
        ),
        PersonalContact(
          relationship = "Emergency Contact",
          relationshipType = RelationshipType(
            code = "ME",
            description = "Father",
          ),
          name = Name(
            forename = "UPW",
            surname = "Testing",
          ),
          telephoneNumber = "020 2000 0000",
          address = Address(
            buildingName = "Petty France",
            addressNumber = "102",
            streetName = "Central London",
            district = "London",
            town = "London",
            county = "London",
            postcode = "SW1H 9AJ",
          ),
        ),
      ),
    )
  }
}
