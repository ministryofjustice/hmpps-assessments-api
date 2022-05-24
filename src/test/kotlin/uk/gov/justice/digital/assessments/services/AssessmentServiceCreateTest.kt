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
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
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
  private val assessmentReferenceDataService: AssessmentReferenceDataService = mockk()
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
  private val assessmentType = AssessmentType.ROSH

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
    every { assessmentReferenceDataService.toOasysAssessmentType(AssessmentType.ROSH) } returns OasysAssessmentType.SHORT_FORM_PSR
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
  }

  @Nested
  @DisplayName("creating assessments from Delius")
  inner class CreatingDeliusAssessments {

    @Test
    fun `should not create Oasys assessment for UPW assessment type`() {
      // Given
      every { subjectRepository.findByCrn(crn) } returns null
      every { offenderService.getOffender(crn) } returns OffenderDto(dateOfBirth = LocalDate.of(1989, 1, 1))
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
          crn,
          eventId,
          AssessmentType.UPW
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
      every { episodeService.prePopulateFromExternalSources(any(), AssessmentType.UPW) } returnsArgument 0
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

      // when
      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = eventId,
          crn = crn,
          assessmentSchemaCode = AssessmentType.UPW
        )
      )
      // Then
      verify(exactly = 0) { oasysAssessmentUpdateService.createOffenderAndOasysAssessment(any(), any(), any()) }
    }

    @Test
    fun `create new offender with new assessment from delius event index and crn`() {
      // Given
      every { subjectRepository.findByCrn(crn) } returns null
      every { offenderService.getOffender(crn) } returns OffenderDto(dateOfBirth = LocalDate.of(1989, 1, 1))
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(crn, eventId, assessmentType)
      } returns Pair(oasysOffenderPk, oasysSetPk)
      every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
        convictionId = 123,
        convictionIndex = eventId,
        offenceCode = "Code",
        codeDescription = "Code description",
        offenceSubCode = "Sub-code",
        subCodeDescription = "Sub-code description"
      )
      every { episodeService.prePopulateFromExternalSources(any(), assessmentType) } returnsArgument 0
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
      verify(exactly = 1) { oasysAssessmentUpdateService.createOffenderAndOasysAssessment(any(), any(), any()) }
    }

    @Test
    fun `create new offender with new assessment from delius event ID and crn`() {
      every { subjectRepository.findByCrn(crn) } returns null
      every { offenderService.getOffender(crn) } returns OffenderDto(dateOfBirth = LocalDate.of(1989, 1, 1))
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
          crn,
          eventId,
          assessmentType
        )
      } returns Pair(oasysOffenderPk, oasysSetPk)

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
      every { episodeService.prePopulateFromExternalSources(any(), assessmentType) } returnsArgument 0
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
      every { episodeService.prePopulateFromExternalSources(any(), assessmentType) } returnsArgument 0
      every { episodeService.prePopulateFromPreviousEpisodes(any(), any()) } returnsArgument 0
      every { subjectRepository.save(any()) } returns SubjectEntity(
        name = "name",
        pnc = "PNC",
        crn = crn,
        dateOfBirth = LocalDate.of(1989, 1, 1),
        createdDate = LocalDateTime.now(),
      )
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(crn, eventId, assessmentType)
      } returns Pair(oasysOffenderPk, oasysSetPk)

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
    fun `should return assessment with pre-populated answers from delius where subject already exists with previous episode`() {
      // Given
      val authorEntity = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns authorEntity

      val assessment = AssessmentEntity(assessmentId = assessmentId, assessmentUuid = assessmentUuid)
      val assessmentEpisodeEntity = AssessmentEpisodeEntity(
        oasysSetPk = 1,
        assessment = assessment,
        assessmentType = AssessmentType.UPW,
        author = authorEntity,
        endDate = LocalDateTime.now(),
        answers = mutableMapOf(
          "gender_identity" to listOf("Prefer to self-describe"),
          "question_2" to listOf("answer_2")
        )
      )
      assessment.episodes.add(assessmentEpisodeEntity)

      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(crn, eventId, AssessmentType.UPW)
      } returns Pair(oasysOffenderPk, oasysSetPk)

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

      every { episodeService.prePopulateFromExternalSources(any(), AssessmentType.UPW) } returns assessmentEpisodeEntity
//      every { episodeService.prePopulateFromPreviousEpisodes(any(), any()) } returnsArgument 0
      val assessmentEpisodeEntity1 = AssessmentEpisodeEntity(
        oasysSetPk = 2,
        assessment = assessment,
        assessmentType = AssessmentType.UPW,
        author = authorEntity,
        endDate = LocalDateTime.now(),
        answers = mutableMapOf(
          "gender_identity" to listOf("Male"),
          "question_3" to listOf("answer_3")
        )
      )
      every { episodeService.prePopulateFromPreviousEpisodes(any(), any()) } returns assessmentEpisodeEntity1

      every { subjectRepository.save(any()) } returns SubjectEntity(
        name = "name",
        pnc = "PNC",
        crn = crn,
        dateOfBirth = LocalDate.of(1989, 1, 1),
        createdDate = LocalDateTime.now(),
      )

      // When
      val assessmentDto =
        assessmentsService.createNewAssessment(CreateAssessmentDto(deliusEventId = eventId, crn = crn, assessmentSchemaCode = AssessmentType.UPW))

      // Then
      assertThat(assessmentDto.assessmentUuid).isEqualTo(assessmentUuid)
      assertThat(assessmentDto.episodes.size).isEqualTo(2)
      assertThat(assessmentDto.episodes.elementAt(1).answers).hasSize(2)
        .containsKeys("gender_identity", "question_2")
      verify(exactly = 0) { assessmentRepository.save(any()) }
    }

    @Test
    fun `do not prepopulate existing episode`() {
      // Given
      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author

      val assessment = AssessmentEntity(assessmentId = assessmentId, assessmentUuid = assessmentUuid)
      assessment.newEpisode("reason", 1, assessmentType, null, author)

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
      every { episodeService.prePopulateFromExternalSources(any(), assessmentType) } returnsArgument 0
      every { subjectRepository.save(any()) } returns SubjectEntity(
        name = "name",
        pnc = "PNC",
        crn = crn,
        dateOfBirth = LocalDate.of(1989, 1, 1),
        createdDate = LocalDateTime.now(),
      )
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(crn, eventId, assessmentType)
      } returns Pair(oasysOffenderPk, oasysSetPk)

      // When
      assessmentsService.createNewAssessment(
        CreateAssessmentDto(deliusEventId = eventId, crn = crn, assessmentSchemaCode = assessmentType)
      )
      // Then
      verify(exactly = 0) { episodeService.prePopulateFromExternalSources(any(), assessmentType) }
    }

    @Test
    fun `throw exception if offender is not returned from Delius`() {
      every { subjectRepository.findByCrn(crn) } returns null
      every { offenderService.getOffender("X12345") } throws EntityNotFoundException("")

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
      every {
        offenderService.getOffender("X12345")
      } returns OffenderDto(dateOfBirth = LocalDate.of(1989, 1, 1))

      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
          crn,
          eventId,
          assessmentType
        )
      } returns Pair(oasysOffenderPk, oasysSetPk)

      every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
        convictionId = 123,
        convictionIndex = eventId,
        offenceCode = "Code",
        codeDescription = "Code description",
        offenceSubCode = "Sub-code",
        subCodeDescription = "Sub-code description"
      )
      every { episodeService.prePopulateFromExternalSources(any(), assessmentType) } returnsArgument 0
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
      assessment.newEpisode("reason", 1, assessmentType, null, author)

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
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(
          crn,
          eventId,
          assessmentType
        )
      } returns Pair(oasysOffenderPk, oasysSetPk)
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

  @Nested
  @DisplayName("creating assessments from court")
  inner class CreatingCourtAssessments {

    @BeforeEach
    fun setup() {
      MDC.put(RequestData.USER_NAME_HEADER, "User name")
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      justRun { telemetryService.trackAssessmentEvent(ASSESSMENT_CREATED, any(), any(), any(), any(), any()) }
    }

    @Test
    fun `create new assessment from court`() {
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
          assessmentType = assessmentType
        )
      } returns Pair(oasysOffenderPk, oasysSetPk)

      every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
        offenceCode = "Code",
        codeDescription = "Code description",
        offenceSubCode = "Sub-code",
        subCodeDescription = "Sub-code description"
      )
      every { episodeService.prePopulateFromExternalSources(any(), assessmentType) } returnsArgument 0
      every { episodeService.prePopulateFromPreviousEpisodes(any(), any()) } returnsArgument 0
      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author

      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          courtCode = courtCode,
          caseNumber = caseNumber,
          assessmentSchemaCode = assessmentType
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

      every { subjectRepository.findByCrn(crn) } returns subject
      every { courtCaseRestClient.getCourtCase(courtCode, existingCaseNumber) } returns CourtCase(
        defendantDob = LocalDate.now(),
        crn = crn
      )
      every {
        oasysAssessmentUpdateService.createOffenderAndOasysAssessment(crn = crn, assessmentType = assessmentType)
      } returns Pair(oasysOffenderPk, oasysSetPk)

      val updatedSubject = subjectEntity.copy(oasysOffenderPk = oasysOffenderPk)
      every { subjectRepository.save(updatedSubject) } returns updatedSubject
      every { episodeService.prePopulateFromExternalSources(any(), assessmentType) } returnsArgument 0
      every { episodeService.prePopulateFromPreviousEpisodes(any(), any()) } returnsArgument 0
      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author

      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          courtCode = courtCode,
          caseNumber = existingCaseNumber,
          assessmentSchemaCode = assessmentType
        )
      )

      verify(exactly = 1) { subjectRepository.save(updatedSubject) }
      verify(exactly = 0) { assessmentRepository.save(any()) }
    }

    @Test
    fun `audit create assessment from court`() {
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
          assessmentType = assessmentType
        )
      } returns Pair(oasysOffenderPk, oasysSetPk)

      every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
        offenceCode = "Code",
        codeDescription = "Code description",
        offenceSubCode = "Sub-code",
        subCodeDescription = "Sub-code description"
      )
      every { episodeService.prePopulateFromExternalSources(any(), assessmentType) } returnsArgument 0
      every { episodeService.prePopulateFromPreviousEpisodes(any(), any()) } returnsArgument 0
      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author

      val assessment = assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          courtCode = courtCode,
          caseNumber = caseNumber,
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
  }
}
