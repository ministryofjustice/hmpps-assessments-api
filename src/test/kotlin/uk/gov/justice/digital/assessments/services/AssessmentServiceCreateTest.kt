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
import uk.gov.justice.digital.assessments.api.DeliusEventType
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.assessments.CreateAssessmentDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.ExternalService
import uk.gov.justice.digital.assessments.restclient.audit.AuditType.ARN_ASSESSMENT_CREATED
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
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
  private val questionService: QuestionService = mockk()
  private val episodeService: EpisodeService = mockk()
  private val offenderService: OffenderService = mockk()
  private val auditService: AuditService = mockk()
  private val telemetryService: TelemetryService = mockk()
  private val clock: Clock = Clock.fixed(Instant.now(), ZoneId.of("Europe/London"))

  private val assessmentsService = AssessmentService(
    assessmentRepository,
    subjectRepository,
    authorService,
    questionService,
    episodeService,
    offenderService,
    auditService,
    telemetryService,
    clock
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val assessmentType = AssessmentType.UPW

  private val crn = "X12345"
  private var communityOffenderDto = CommunityOffenderDto(dateOfBirth = LocalDate.of(1989, 1, 1).toString())

  private val eventId = 1L

  @BeforeEach
  fun setup() {
    MDC.put(RequestData.USER_NAME_HEADER, "User name")
    val offenceDto = OffenceDto(
      convictionId = 123,
      convictionIndex = eventId,
      offenceCode = "Code",
      codeDescription = "Code description",
      offenceSubCode = "Sub-code",
      subCodeDescription = "Sub-code description"
    )
    every { offenderService.getOffence(any(), crn, eventId) } returns offenceDto
    justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
    justRun { telemetryService.trackAssessmentEvent(ASSESSMENT_CREATED, any(), any(), any(), any(), any()) }
    every { offenderService.validateUserAccess(crn) } returns mockk()
    every { offenderService.getCommunityOffender(crn) } returns communityOffenderDto
  }

  @Test
  fun `create new offender with new assessment from delius event index and crn`() {
    // Given
    every { subjectRepository.findByCrn(crn) } returns null
    every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
      convictionId = 123,
      convictionIndex = eventId,
      offenceCode = "Code",
      codeDescription = "Code description",
      offenceSubCode = "Sub-code",
      subCodeDescription = "Sub-code description"
    )
    every { episodeService.prePopulateEpisodeFromDelius(any(), communityOffenderDto) } returnsArgument 0
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

    // When
    assessmentsService.createNewAssessment(
      CreateAssessmentDto(
        deliusEventId = eventId,
        crn = crn,
        assessmentSchemaCode = assessmentType
      )
    )
    // Then
    verify(exactly = 1) { assessmentRepository.save(any()) }
  }

  private fun createOffenderDto(): CommunityOffenderDto {
    return CommunityOffenderDto(dateOfBirth = "12-03-1976")
  }

  @Test
  fun `create new offender with new assessment from delius event ID and crn`() {
    every { subjectRepository.findByCrn(crn) } returns null

    val offenceDto = OffenceDto(
      convictionId = 123,
      convictionIndex = eventId,
      offenceCode = "Code",
      codeDescription = "Code description",
      offenceSubCode = "Sub-code",
      subCodeDescription = "Sub-code description"
    )
    every { offenderService.getOffenceFromConvictionId(crn, eventId) } returns offenceDto
    every { offenderService.getOffence(DeliusEventType.EVENT_ID, crn, eventId) } returns offenceDto
    every { episodeService.prePopulateEpisodeFromDelius(any(), communityOffenderDto) } returnsArgument 0
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
        deliusEventType = DeliusEventType.EVENT_ID
      )
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
        crn = crn
      )
    every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
      convictionId = 123,
      convictionIndex = eventId,
      offenceCode = "Code",
      codeDescription = "Code description",
      offenceSubCode = "Sub-code",
      subCodeDescription = "Sub-code description"
    )
    every { episodeService.prePopulateEpisodeFromDelius(any(), communityOffenderDto) } returnsArgument 0
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
        crn = crn
      )
    every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
      convictionId = 123,
      convictionIndex = eventId,
      offenceCode = "Code",
      codeDescription = "Code description",
      offenceSubCode = "Sub-code",
      subCodeDescription = "Sub-code description"
    )
    every { episodeService.prePopulateEpisodeFromDelius(any(), createOffenderDto()) } returnsArgument 0
    every { subjectRepository.save(any()) } returns SubjectEntity(
      name = "name",
      pnc = "PNC",
      crn = crn,
      dateOfBirth = LocalDate.of(1989, 1, 1),
      createdDate = LocalDateTime.now(),
    )

    // When
    assessmentsService.createNewAssessment(
      CreateAssessmentDto(deliusEventId = eventId, crn = crn, assessmentSchemaCode = assessmentType)
    )
    // Then
    verify(exactly = 0) { episodeService.prePopulateEpisodeFromDelius(any(), createOffenderDto()) }
  }

  @Test
  fun `throw exception if offender is not returned from Delius`() {
    every { subjectRepository.findByCrn(crn) } returns null
    every { offenderService.getCommunityOffender("X12345") } throws EntityNotFoundException("")

    assertThrows<EntityNotFoundException> {
      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = eventId,
          crn = crn,
          assessmentSchemaCode = assessmentType
        )
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
      ExternalService.COMMUNITY_API
    )

    val exception = assertThrows<ExternalApiForbiddenException> {
      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = eventId,
          crn = crn,
          assessmentSchemaCode = assessmentType
        )
      )
    }
    assertThat(exception.message).isEqualTo("User does not have permission to access offender with CRN $crn")
    verify(exactly = 0) { assessmentRepository.save(any()) }
  }

  @Test
  fun `audit create assessment from delius`() {
    every { subjectRepository.findByCrn(crn) } returns null
    every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
      convictionId = 123,
      convictionIndex = eventId,
      offenceCode = "Code",
      codeDescription = "Code description",
      offenceSubCode = "Sub-code",
      subCodeDescription = "Sub-code description"
    )
    every { episodeService.prePopulateEpisodeFromDelius(any(), communityOffenderDto) } returnsArgument 0
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
        assessmentSchemaCode = assessmentType
      )
    )
    verify(exactly = 1) {
      auditService.createAuditEvent(
        ARN_ASSESSMENT_CREATED,
        assessment.assessmentUuid,
        assessment.episodes?.first()?.episodeUuid,
        crn,
        any(),
        any()
      )
    }
    verify(exactly = 1) {
      telemetryService.trackAssessmentEvent(
        ASSESSMENT_CREATED,
        crn,
        author,
        assessment.assessmentUuid,
        assessment.episodes?.first()?.episodeUuid!!,
        assessmentType
      )
    }
  }

  @Test
  fun `do not audit existing assessment if one already exists`() {
    val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
    val assessment = AssessmentEntity(
      assessmentId = assessmentId,
      assessmentUuid = assessmentUuid
    )
    assessment.newEpisode("reason", assessmentType, null, author)

    every { subjectRepository.findByCrn(crn) } returns
      SubjectEntity(
        assessments = listOf(assessment),
        dateOfBirth = LocalDate.of(1989, 1, 1),
        crn = crn
      )
    every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
      convictionId = 123,
      convictionIndex = eventId,
      offenceCode = "Code",
      codeDescription = "Code description",
      offenceSubCode = "Sub-code",
      subCodeDescription = "Sub-code description"
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
        assessmentSchemaCode = assessmentType
      )
    )
    verify(exactly = 0) { assessmentRepository.save(any()) }
    verify(exactly = 0) { auditService.createAuditEvent(any(), any(), any(), any(), any()) }
    verify(exactly = 0) { telemetryService.trackAssessmentEvent(any(), any(), any(), any(), any(), any()) }
  }
}
