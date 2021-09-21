package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
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
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.utils.RequestData
import java.time.LocalDate
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Create Tests")
class AssessmentServiceCreateTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val subjectRepository: SubjectRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val courtCaseRestClient: CourtCaseRestClient = mockk()
  private val episodeService: EpisodeService = mockk()
  private val offenderService: OffenderService = mockk()
  private val oasysAssessmentUpdateService: OasysAssessmentUpdateService = mockk()
  private val assessmentSchemaService: AssessmentSchemaService = mockk()

  private val assessmentsService = AssessmentService(
    assessmentRepository,
    subjectRepository,
    questionService,
    episodeService,
    courtCaseRestClient,
    oasysAssessmentUpdateService,
    offenderService
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
    fun `create new offender with new assessment from delius event id and crn`() {
      every { subjectRepository.findByCrn(crn) } returns null
      every { offenderService.getOffender("X12345") } returns OffenderDto(dateOfBirth = LocalDate.of(1989, 1, 1))
      every { oasysAssessmentUpdateService.createOasysAssessment(crn, eventId, assessmentSchemaCode) } returns Pair(
        oasysOffenderPk,
        oasysSetPk
      )
      every { offenderService.getOffence(crn, eventId) } returns OffenceDto(
        offenceCode = "Code",
        codeDescription = "Code description",
        offenceSubCode = "Sub-code",
        subCodeDescription = "Sub-code description"
      )
      every { episodeService.prepopulate(any()) } returnsArgument 0
      every { assessmentRepository.save(any()) } returns AssessmentEntity(assessmentId = assessmentId)

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
    fun `return existing assessment from delius event id and crn if one already exists`() {
      every { subjectRepository.findByCrn(crn) } returns
        SubjectEntity(
          assessment = AssessmentEntity(assessmentId = assessmentId, assessmentUuid = assessmentUuid),
          dateOfBirth = LocalDate.of(1989, 1, 1),
          crn = "X1345"
        )

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
      every { subjectRepository.findByCrn(crn) } returns null
      every { assessmentRepository.save(any()) } returns AssessmentEntity(assessmentId = assessmentId)
      every { courtCaseRestClient.getCourtCase(courtCode, caseNumber) } returns CourtCase(
        crn = crn,
        defendantDob = LocalDate.of(1989, 1, 1)
      )
      every {
        oasysAssessmentUpdateService.createOasysAssessment(
          crn = crn,
          assessmentSchemaCode = assessmentSchemaCode
        )
      } returns Pair(oasysOffenderPk, oasysSetPk)

      every { offenderService.getOffence(crn, eventId) } returns OffenceDto(
        offenceCode = "Code",
        codeDescription = "Code description",
        offenceSubCode = "Sub-code",
        subCodeDescription = "Sub-code description"
      )
      every { episodeService.prepopulate(any()) } returnsArgument 0

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
      every { subjectRepository.findByCrn(crn) } returns SubjectEntity(
        assessment = AssessmentEntity(assessmentId = 1),
        dateOfBirth = LocalDate.of(1989, 1, 1),
        crn = "X1345"
      )
      every { courtCaseRestClient.getCourtCase(courtCode, existingCaseNumber) } returns CourtCase(
        defendantDob = LocalDate.now(),
        crn = crn
      )

      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          courtCode = courtCode,
          caseNumber = existingCaseNumber,
          assessmentSchemaCode = assessmentSchemaCode
        )
      )

      verify(exactly = 0) { assessmentRepository.save(any()) }
      verify(exactly = 0) { assessmentRepository.save(any()) }
    }
  }
}
