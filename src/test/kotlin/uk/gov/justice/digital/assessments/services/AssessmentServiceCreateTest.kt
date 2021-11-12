package uk.gov.justice.digital.assessments.services

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
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.MDC
import org.springframework.http.HttpMethod
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.DeliusEventType
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.ExternalService
import uk.gov.justice.digital.assessments.restclient.audit.AuditType.ARN_ASSESSMENT_CREATED
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase
import uk.gov.justice.digital.assessments.services.TelemetryEventType.ASSESSMENT_CREATED
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.utils.RequestData
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Create Tests")
class AssessmentServiceCreateTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val subjectRepository: SubjectRepository = mockk()
  private val authorService: AuthorService = mockk()
  private val questionService: QuestionService = mockk()
  private val courtCaseRestClient: CourtCaseRestClient = mockk()
  private val episodeService: EpisodeService = mockk()
  private val offenderService: OffenderService = mockk()
  private val oasysAssessmentUpdateService: OasysAssessmentUpdateService = mockk()
  private val assessmentSchemaService: AssessmentSchemaService = mockk()
  private val auditService: AuditService = mockk()
  private val telemetryService: TelemetryService = mockk()

  private val assessmentsService = AssessmentService(
    assessmentRepository,
    subjectRepository,
    authorService,
    questionService,
    episodeService,
    courtCaseRestClient,
    oasysAssessmentUpdateService,
    offenderService,
    auditService,
    telemetryService
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val assessmentSchemaCode = AssessmentSchemaCode.ROSH

  private val oasysOffenderPk = 1L
  private val crn = "X12345"
  private val oasysSetPk = 1L

  private val courtCode = "SHF06"
  private val caseNumber = "668911253"
  private val existingCaseNumber = "existingAssessment"

  private val eventId = 1L

  @BeforeEach
  fun setup() {
    MDC.put(RequestData.USER_NAME_HEADER, "User name")
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.ROSH) } returns OasysAssessmentType.SHORT_FORM_PSR
  }

  @Nested
  @DisplayName("creating assessments from Delius")
  inner class CreatingDeliusAssessments {
    @Test
    fun `create new offender with new assessment from delius event index and crn`() {
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      justRun {
        telemetryService.trackAssessmentEvent(
          ASSESSMENT_CREATED,
          any(),
          any(),
          any(),
          any()
        )
      }
      every { subjectRepository.findByCrn(crn) } returns null
      every { offenderService.validateUserAccess("X12345") } returns mockk()
      every { offenderService.getOffender("X12345") } returns OffenderDto(dateOfBirth = LocalDate.of(1989, 1, 1))
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
          crn,
          eventId,
          assessmentSchemaCode
        )
      } returns Pair(
        oasysOffenderPk,
        oasysSetPk
      )
      every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
        convictionId = 123,
        convictionIndex = eventId,
        offenceCode = "Code",
        codeDescription = "Code description",
        offenceSubCode = "Sub-code",
        subCodeDescription = "Sub-code description"
      )
      every { episodeService.prepopulate(any(), assessmentSchemaCode) } returnsArgument 0
      every { subjectRepository.save(any()) } returns SubjectEntity(
        name = "name",
        pnc = "PNC",
        crn = "X12345",
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
          assessmentSchemaCode = assessmentSchemaCode
        )
      )
      verify(exactly = 1) { assessmentRepository.save(any()) }
    }

    @Test
    fun `create new offender with new assessment from delius event ID and crn`() {
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      justRun {
        telemetryService.trackAssessmentEvent(
          ASSESSMENT_CREATED,
          any(),
          any(),
          any(),
          any()
        )
      }
      every { subjectRepository.findByCrn(crn) } returns null
      every { offenderService.validateUserAccess("X12345") } returns mockk()
      every { offenderService.getOffender("X12345") } returns OffenderDto(dateOfBirth = LocalDate.of(1989, 1, 1))
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
          crn,
          eventId,
          assessmentSchemaCode
        )
      } returns Pair(
        oasysOffenderPk,
        oasysSetPk
      )
      every { offenderService.getOffenceFromConvictionId(crn, eventId) } returns OffenceDto(
        convictionId = 123,
        convictionIndex = eventId,
        offenceCode = "Code",
        codeDescription = "Code description",
        offenceSubCode = "Sub-code",
        subCodeDescription = "Sub-code description"
      )
      every { episodeService.prepopulate(any(), assessmentSchemaCode) } returnsArgument 0
      every { subjectRepository.save(any()) } returns SubjectEntity(
        name = "name",
        pnc = "PNC",
        crn = "X12345",
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
          assessmentSchemaCode = assessmentSchemaCode,
          deliusEventType = DeliusEventType.EVENT_ID
        )
      )
      verify(exactly = 1) { assessmentRepository.save(any()) }
    }

    @Test
    fun `return existing assessment from delius event id and crn if one already exists`() {
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      justRun {
        telemetryService.trackAssessmentEvent(
          ASSESSMENT_CREATED,
          any(),
          any(),
          any(),
          any()
        )
      }
      every { offenderService.validateUserAccess("X12345") } returns mockk()
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
      every { episodeService.prepopulate(any(), assessmentSchemaCode) } returnsArgument 0
      every { subjectRepository.save(any()) } returns SubjectEntity(
        name = "name",
        pnc = "PNC",
        crn = crn,
        dateOfBirth = LocalDate.of(1989, 1, 1),
        createdDate = LocalDateTime.now(),
      )
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
          crn,
          eventId,
          assessmentSchemaCode
        )
      } returns Pair(oasysOffenderPk, oasysSetPk)
      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author

      val assessmentDto =
        assessmentsService.createNewAssessment(
          CreateAssessmentDto(
            deliusEventId = eventId,
            crn = crn,
            assessmentSchemaCode = assessmentSchemaCode
          )
        )
      assertThat(assessmentDto.assessmentUuid).isEqualTo(assessmentUuid)
      verify(exactly = 0) { assessmentRepository.save(any()) }
    }

    @Test
    fun `throw exception if offender is not returned from Delius`() {
      every { subjectRepository.findByCrn(crn) } returns null
      every { offenderService.validateUserAccess("X12345") } returns mockk()
      every { offenderService.getOffender("X12345") } throws EntityNotFoundException("")

      assertThrows<EntityNotFoundException> {
        assessmentsService.createNewAssessment(
          CreateAssessmentDto(
            deliusEventId = eventId,
            crn = crn,
            assessmentSchemaCode = assessmentSchemaCode
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
            assessmentSchemaCode = assessmentSchemaCode
          )
        )
      }
      assertThat(exception.message).isEqualTo("User does not have permission to access offender with CRN $crn")
      verify(exactly = 0) { assessmentRepository.save(any()) }
    }

    @Test
    fun `audit create assessment from delius`() {
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      justRun { telemetryService.trackAssessmentEvent(ASSESSMENT_CREATED, any(), any(), any(), any()) }
      every { subjectRepository.findByCrn(crn) } returns null
      every { offenderService.validateUserAccess("X12345") } returns mockk()
      every { offenderService.getOffender("X12345") } returns OffenderDto(dateOfBirth = LocalDate.of(1989, 1, 1))
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
          crn,
          eventId,
          assessmentSchemaCode
        )
      } returns Pair(
        oasysOffenderPk,
        oasysSetPk
      )
      every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
        convictionId = 123,
        convictionIndex = eventId,
        offenceCode = "Code",
        codeDescription = "Code description",
        offenceSubCode = "Sub-code",
        subCodeDescription = "Sub-code description"
      )
      every { episodeService.prepopulate(any(), assessmentSchemaCode) } returnsArgument 0
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
          assessmentSchemaCode = assessmentSchemaCode
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
          assessment.episodes?.first()?.episodeUuid!!
        )
      }
    }

    @Test
    fun `do not audit existing assessment if one already exists`() {
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      justRun { telemetryService.trackAssessmentEvent(ASSESSMENT_CREATED, any(), any(), any(), any()) }
      every { offenderService.validateUserAccess("X12345") } returns mockk()
      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        assessmentUuid = assessmentUuid
      )
      assessment.newEpisode("reason", 1, assessmentSchemaCode, null, author)

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
      every { episodeService.prepopulate(any(), assessmentSchemaCode) } returnsArgument 0
      every { subjectRepository.save(any()) } returns SubjectEntity(
        name = "name",
        pnc = "PNC",
        crn = crn,
        dateOfBirth = LocalDate.of(1989, 1, 1),
        createdDate = LocalDateTime.now(),
      )
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
          crn,
          eventId,
          assessmentSchemaCode
        )
      } returns Pair(oasysOffenderPk, oasysSetPk)
      every { authorService.getOrCreateAuthor() } returns author
      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = eventId,
          crn = crn,
          assessmentSchemaCode = assessmentSchemaCode
        )
      )
      verify(exactly = 0) { assessmentRepository.save(any()) }
      verify(exactly = 0) { auditService.createAuditEvent(any(), any(), any(), any(), any()) }
      verify(exactly = 0) { telemetryService.trackAssessmentEvent(any(), any(), any(), any(), any()) }
    }
  }

  @Nested
  @DisplayName("creating assessments from court")
  inner class CreatingCourtAssessments {

    @BeforeEach
    fun setup() {
      MDC.put(RequestData.USER_NAME_HEADER, "User name")
    }

    @Test
    fun `create new assessment from court`() {
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      justRun {
        telemetryService.trackAssessmentEvent(
          ASSESSMENT_CREATED,
          any(),
          any(),
          any(),
          any()
        )
      }
      every { subjectRepository.findByCrn(crn) } returns null
      every { subjectRepository.save(any()) } returns SubjectEntity(
        name = "name",
        pnc = "PNC",
        crn = crn,
        dateOfBirth = LocalDate.of(1989, 1, 1),
        createdDate = LocalDateTime.now(),
      )
      every { assessmentRepository.save(any()) } returns AssessmentEntity(assessmentId = assessmentId)
      every { courtCaseRestClient.getCourtCase(courtCode, caseNumber) } returns CourtCase(
        crn = crn,
        defendantDob = LocalDate.of(1989, 1, 1)
      )
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
          crn = crn,
          assessmentSchemaCode = assessmentSchemaCode
        )
      } returns Pair(oasysOffenderPk, oasysSetPk)

      every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
        offenceCode = "Code",
        codeDescription = "Code description",
        offenceSubCode = "Sub-code",
        subCodeDescription = "Sub-code description"
      )
      every { episodeService.prepopulate(any(), assessmentSchemaCode) } returnsArgument 0
      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author

      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          courtCode = courtCode,
          caseNumber = caseNumber,
          assessmentSchemaCode = assessmentSchemaCode
        )
      )

      verify(exactly = 1) { assessmentRepository.save(any()) }
      verify(exactly = 1) { courtCaseRestClient.getCourtCase(courtCode, caseNumber) }
    }

    @Test
    fun `return existing assessment if one exists from court`() {
      val subjectEntity = SubjectEntity(
        dateOfBirth = LocalDate.of(1989, 1, 1),
        crn = crn
      )
      val assessment = AssessmentEntity(assessmentId = 1, subject = subjectEntity)
      val subject = subjectEntity.copy(assessments = listOf(assessment))
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      justRun {
        telemetryService.trackAssessmentEvent(
          ASSESSMENT_CREATED,
          any(),
          any(),
          any(),
          any()
        )
      }
      every { subjectRepository.findByCrn(crn) } returns subject
      every { courtCaseRestClient.getCourtCase(courtCode, existingCaseNumber) } returns CourtCase(
        defendantDob = LocalDate.now(),
        crn = crn
      )
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
          crn = crn,
          assessmentSchemaCode = assessmentSchemaCode
        )
      } returns Pair(oasysOffenderPk, oasysSetPk)

      val updatedSubject = subjectEntity.copy(oasysOffenderPk = oasysOffenderPk)
      every { subjectRepository.save(updatedSubject) } returns updatedSubject
      every { episodeService.prepopulate(any(), assessmentSchemaCode) } returnsArgument 0
      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author

      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          courtCode = courtCode,
          caseNumber = existingCaseNumber,
          assessmentSchemaCode = assessmentSchemaCode
        )
      )

      verify(exactly = 1) { subjectRepository.save(updatedSubject) }
      verify(exactly = 0) { assessmentRepository.save(any()) }
    }

    @Test
    fun `audit create assessment from court`() {
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      justRun {
        telemetryService.trackAssessmentEvent(
          ASSESSMENT_CREATED,
          any(),
          any(),
          any(),
          any()
        )
      }
      every { subjectRepository.findByCrn(crn) } returns null
      every { subjectRepository.save(any()) } returns SubjectEntity(
        name = "name",
        pnc = "PNC",
        crn = crn,
        dateOfBirth = LocalDate.of(1989, 1, 1),
        createdDate = LocalDateTime.now(),
      )
      every { assessmentRepository.save(any()) } returns AssessmentEntity(assessmentId = assessmentId)
      every { courtCaseRestClient.getCourtCase(courtCode, caseNumber) } returns CourtCase(
        crn = crn,
        defendantDob = LocalDate.of(1989, 1, 1)
      )
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
          crn = crn,
          assessmentSchemaCode = assessmentSchemaCode
        )
      } returns Pair(oasysOffenderPk, oasysSetPk)

      every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
        offenceCode = "Code",
        codeDescription = "Code description",
        offenceSubCode = "Sub-code",
        subCodeDescription = "Sub-code description"
      )
      every { episodeService.prepopulate(any(), assessmentSchemaCode) } returnsArgument 0
      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author

      val assessment = assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          courtCode = courtCode,
          caseNumber = caseNumber,
          assessmentSchemaCode = assessmentSchemaCode
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
          assessment.episodes?.first()?.episodeUuid!!
        )
      }
    }
  }
}
