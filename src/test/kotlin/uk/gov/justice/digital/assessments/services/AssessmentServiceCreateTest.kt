package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.MDC
import org.springframework.http.HttpMethod
import uk.gov.justice.digital.assessments.api.assessments.CreateAssessmentDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.AuditType.ARN_ASSESSMENT_CREATED
import uk.gov.justice.digital.assessments.restclient.DeliusIntegrationRestClient
import uk.gov.justice.digital.assessments.restclient.ExternalService
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Address
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Name
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.PersonalContact
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.RelationshipType
import uk.gov.justice.digital.assessments.services.TelemetryEventType.ASSESSMENT_CREATED
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.utils.RequestData
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Create Tests")
class AssessmentServiceCreateTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val subjectRepository: SubjectRepository = mockk()
  private val authorService: AuthorService = mockk()
  private val episodeService: EpisodeService = mockk()
  private val offenderService: OffenderService = mockk()
  private val auditService: AuditService = mockk()
  private val telemetryService: TelemetryService = mockk()
  private val clock: Clock = Clock.fixed(Instant.now(), ZoneId.of("Europe/London"))
  private val episodeRepository: EpisodeRepository = mockk()
  private val deliusIntegrationRestClient: DeliusIntegrationRestClient = mockk()

  private val assessmentsService = AssessmentService(
    assessmentRepository,
    subjectRepository,
    authorService,
    episodeService,
    offenderService,
    auditService,
    telemetryService,
    clock,
    episodeRepository,
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val assessmentType = AssessmentType.UPW

  private val crn = "X12345"
  private val eventId = 1L

  @BeforeEach
  fun setup() {
    MDC.put(RequestData.USER_NAME_HEADER, "User name")
    justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
    justRun { telemetryService.trackAssessmentEvent(ASSESSMENT_CREATED, any(), any(), any(), any(), any()) }
    every { offenderService.validateUserAccess(crn) } returns mockk()
    every { offenderService.getDeliusCaseDetails(crn, eventId) } returns caseDetails()
    every { deliusIntegrationRestClient.getCaseDetails(crn, eventId) } returns caseDetails()
  }

  @Test
  fun `create new offender with new assessment from delius event ID and crn`() {
    every { subjectRepository.findByCrn(crn) } returns null
    every { episodeService.prePopulateEpisodeFromDelius(any(), any()) } returnsArgument 0
    every { episodeService.prePopulateFromPreviousEpisodes(any(), any()) } returnsArgument 0
    every { subjectRepository.save(any()) } returns SubjectEntity(
      name = "name",
      pnc = "PNC",
      crn = crn,
      dateOfBirth = LocalDate.of(1989, 1, 1),
      createdDate = LocalDateTime.now(),
    )
    every { assessmentRepository.save(any()) } returns AssessmentEntity(assessmentId = assessmentId)
    val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
    every { authorService.getOrCreateAuthor() } returns author

    assessmentsService.createNewAssessment(
      CreateAssessmentDto(
        deliusEventId = eventId,
        crn = crn,
        assessmentSchemaCode = assessmentType,
      ),
    )
    verify(exactly = 1) { assessmentRepository.save(any()) }
  }

  @Test
  fun `return existing assessment from delius event id and crn if one already exists`() {
    // Given
    every { subjectRepository.findByCrn(crn) } returns
      SubjectEntity(
        assessments = listOf(AssessmentEntity(assessmentId = assessmentId, assessmentUuid = assessmentUuid)),
        dateOfBirth = LocalDate.of(1989, 1, 1),
        crn = crn,
      )
    every { episodeService.prePopulateEpisodeFromDelius(any(), any()) } returnsArgument 0
    every { episodeService.prePopulateFromPreviousEpisodes(any(), any()) } returnsArgument 0
    every { subjectRepository.save(any()) } returns SubjectEntity(
      name = "name",
      pnc = "PNC",
      crn = crn,
      dateOfBirth = LocalDate.of(1989, 1, 1),
      createdDate = LocalDateTime.now(),
    )

    val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
    every { authorService.getOrCreateAuthor() } returns author

    // When
    val assessmentDto =
      assessmentsService.createNewAssessment(CreateAssessmentDto(deliusEventId = eventId, crn = crn, assessmentSchemaCode = assessmentType))

    // Then
    assertThat(assessmentDto.assessmentUuid).isEqualTo(assessmentUuid)
    verify(exactly = 0) { assessmentRepository.save(any()) }
  }

  @Test
  fun `do not prepopulate existing episode`() {
    // Given
    val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
    every { authorService.getOrCreateAuthor() } returns author

    val assessment = AssessmentEntity(assessmentId = assessmentId, assessmentUuid = assessmentUuid)
    assessment.newEpisode("reason", assessmentType, null, author)

    every { subjectRepository.findByCrn(crn) } returns
      SubjectEntity(
        assessments = listOf(assessment),
        dateOfBirth = LocalDate.of(1989, 1, 1),
        crn = crn,
      )
    every { episodeService.prePopulateEpisodeFromDelius(any(), caseDetails()) } returnsArgument 0
    every { subjectRepository.save(any()) } returns SubjectEntity(
      name = "name",
      pnc = "PNC",
      crn = crn,
      dateOfBirth = LocalDate.of(1989, 1, 1),
      createdDate = LocalDateTime.now(),
    )

    // When
    assessmentsService.createNewAssessment(
      CreateAssessmentDto(deliusEventId = eventId, crn = crn, assessmentSchemaCode = assessmentType),
    )
    // Then
    verify(exactly = 0) { episodeService.prePopulateEpisodeFromDelius(any(), caseDetails()) }
  }

  @Test
  fun `throw exception if offender is not returned from Delius`() {
    every { subjectRepository.findByCrn(crn) } returns null
    every { offenderService.getDeliusCaseDetails("X12345", eventId) } throws EntityNotFoundException("")

    assertThrows<EntityNotFoundException> {
      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = eventId,
          crn = crn,
          assessmentSchemaCode = assessmentType,
        ),
      )
    }

    verify(exactly = 0) { assessmentRepository.save(any()) }
  }

  @Test
  fun `throw exception if offender is LAO and user is excluded in Delius`() {
    every { offenderService.validateUserAccess("X12345") } throws ExternalApiForbiddenException(
      "User does not have permission to access offender with CRN $crn",
      HttpMethod.GET,
      "",
      ExternalService.COMMUNITY_API,
    )

    val exception = assertThrows<ExternalApiForbiddenException> {
      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = eventId,
          crn = crn,
          assessmentSchemaCode = assessmentType,
        ),
      )
    }
    assertThat(exception.message).isEqualTo("User does not have permission to access offender with CRN $crn")
    verify(exactly = 0) { assessmentRepository.save(any()) }
  }

  @Test
  fun `audit create assessment from delius`() {
    every { subjectRepository.findByCrn(crn) } returns null
    every { episodeService.prePopulateEpisodeFromDelius(any(), any()) } returnsArgument 0
    every { episodeService.prePopulateFromPreviousEpisodes(any(), any()) } returnsArgument 0
    every { subjectRepository.save(any()) } returns SubjectEntity(
      name = "name",
      pnc = "PNC",
      crn = crn,
      dateOfBirth = LocalDate.of(1989, 1, 1),
      createdDate = LocalDateTime.now(),
    )
    every { assessmentRepository.save(any()) } returns AssessmentEntity(assessmentId = assessmentId)
    val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
    every { authorService.getOrCreateAuthor() } returns author

    val assessment = assessmentsService.createNewAssessment(
      CreateAssessmentDto(
        deliusEventId = eventId,
        crn = crn,
        assessmentSchemaCode = assessmentType,
      ),
    )
    verify(exactly = 1) {
      auditService.createAuditEvent(
        ARN_ASSESSMENT_CREATED,
        assessment.assessmentUuid,
        assessment.episodes.first().episodeUuid,
        crn,
        any(),
        any(),
      )
    }
    verify(exactly = 1) {
      telemetryService.trackAssessmentEvent(
        ASSESSMENT_CREATED,
        crn,
        author,
        assessment.assessmentUuid,
        assessment.episodes.first().episodeUuid!!,
        assessmentType,
      )
    }
  }

  @Test
  fun `do not audit existing assessment if one already exists`() {
    val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
    val assessment = AssessmentEntity(
      assessmentId = assessmentId,
      assessmentUuid = assessmentUuid,
    )
    assessment.newEpisode("reason", assessmentType, null, author)

    every { subjectRepository.findByCrn(crn) } returns
      SubjectEntity(
        assessments = listOf(assessment),
        dateOfBirth = LocalDate.of(1989, 1, 1),
        crn = crn,
      )
    every { subjectRepository.save(any()) } returns SubjectEntity(
      name = "name",
      pnc = "PNC",
      crn = crn,
      dateOfBirth = LocalDate.of(1989, 1, 1),
      createdDate = LocalDateTime.now(),
    )

    every { authorService.getOrCreateAuthor() } returns author
    assessmentsService.createNewAssessment(
      CreateAssessmentDto(
        deliusEventId = eventId,
        crn = crn,
        assessmentSchemaCode = assessmentType,
      ),
    )
    verify(exactly = 0) { assessmentRepository.save(any()) }
    verify(exactly = 0) { auditService.createAuditEvent(any(), any(), any(), any(), any()) }
    verify(exactly = 0) { telemetryService.trackAssessmentEvent(any(), any(), any(), any(), any(), any()) }
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
