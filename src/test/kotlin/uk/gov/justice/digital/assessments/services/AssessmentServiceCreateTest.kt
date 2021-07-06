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
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.utils.RequestData
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
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient = mockk()

  private val assessmentsService = AssessmentService(
    assessmentRepository,
    subjectRepository,
    questionService,
    episodeService,
    courtCaseRestClient,
    assessmentUpdateRestClient,
    offenderService
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val assessmentType = OasysAssessmentType.SHORT_FORM_PSR

  private val oasysOffenderPk = 1L
  private val crn = "X12345"
  private val oasysSetPk = 1L

  private val courtCode = "SHF06"
  private val caseNumber = "668911253"

  private val deliusSource = "DELIUS"
  private val eventId = 1L

  @BeforeEach
  fun setup() {
    MDC.put(RequestData.USER_NAME_HEADER, "User name")
  }

  @Nested
  @DisplayName("creating assessments from Delius")
  inner class CreatingDeliusAssessments {
    @Test
    fun `create new offender with new assessment from delius event id and crn`() {
      every { subjectRepository.findBySourceAndSourceIdAndCrn(deliusSource, eventId.toString(), crn) } returns null
      every { offenderService.getOffender("X12345") } returns OffenderDto()
      every { assessmentUpdateRestClient.createOasysOffender(crn = crn, deliusEvent = eventId) } returns oasysOffenderPk
      every { assessmentUpdateRestClient.createAssessment(oasysOffenderPk, assessmentType) } returns oasysSetPk
      every { episodeService.prepopulate(any()) } returnsArgument 0
      every { assessmentRepository.save(any()) } returns AssessmentEntity(assessmentId = assessmentId)

      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = eventId,
          crn = crn,
          oasysAssessmentType = assessmentType
        )
      )
      verify(exactly = 1) { assessmentRepository.save(any()) }
    }

    @Test
    fun `return existing assessment from delius event id and crn if one already exists`() {
      every { subjectRepository.findBySourceAndSourceIdAndCrn(deliusSource, eventId.toString(), crn) } returns
        SubjectEntity(assessment = AssessmentEntity(assessmentId = assessmentId, assessmentUuid = assessmentUuid))

      val assessmentDto =
        assessmentsService.createNewAssessment(
          CreateAssessmentDto(
            deliusEventId = eventId,
            crn = crn,
            oasysAssessmentType = assessmentType
          )
        )
      assertThat(assessmentDto.assessmentUuid).isEqualTo(assessmentUuid)
      verify(exactly = 0) { assessmentRepository.save(any()) }
    }

    @Test
    fun `throw exception if crn is null`() {
      assertThrows<IllegalStateException> {
        assessmentsService.createNewAssessment(
          CreateAssessmentDto(
            deliusEventId = eventId,
            crn = null,
            oasysAssessmentType = assessmentType
          )
        )
      }
      verify(exactly = 0) { assessmentRepository.save(any()) }
    }

    @Test
    fun `throw exception if delius event id is null`() {
      assertThrows<IllegalStateException> {
        assessmentsService.createNewAssessment(
          CreateAssessmentDto(
            deliusEventId = null,
            crn = crn,
            oasysAssessmentType = assessmentType
          )
        )
      }
      verify(exactly = 0) { assessmentRepository.save(any()) }
    }

    @Test
    fun `throw exception if offender is not returned from Delius`() {
      every { subjectRepository.findBySourceAndSourceIdAndCrn(deliusSource, eventId.toString(), crn) } returns null
      every { offenderService.getOffender("X12345") } throws EntityNotFoundException("")

      assertThrows<EntityNotFoundException> {
        assessmentsService.createNewAssessment(
          CreateAssessmentDto(
            deliusEventId = eventId,
            crn = crn,
            oasysAssessmentType = assessmentType
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
      every {
        subjectRepository.findBySourceAndSourceId(
          AssessmentService.courtSource,
          "$courtCode|$caseNumber"
        )
      } returns null
      every { assessmentRepository.save(any()) } returns AssessmentEntity(assessmentId = assessmentId)
      every { courtCaseRestClient.getCourtCase(courtCode, caseNumber) } returns CourtCase(crn = crn)
      every { assessmentUpdateRestClient.createOasysOffender(crn) } returns oasysOffenderPk
      every { assessmentUpdateRestClient.createAssessment(oasysOffenderPk, assessmentType) } returns oasysSetPk
      every { episodeService.prepopulate(any()) } returnsArgument 0

      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          courtCode = courtCode,
          caseNumber = caseNumber,
          oasysAssessmentType = assessmentType
        )
      )

      verify(exactly = 1) { assessmentRepository.save(any()) }
      verify(exactly = 1) { courtCaseRestClient.getCourtCase(courtCode, caseNumber) }
    }

    @Test
    fun `return existing assessment if one exists from court`() {
      every {
        subjectRepository.findBySourceAndSourceId(
          AssessmentService.courtSource,
          "$courtCode|$caseNumber"
        )
      } returns SubjectEntity(assessment = AssessmentEntity(assessmentId = 1))

      assessmentsService.createNewAssessment(
        CreateAssessmentDto(
          courtCode = courtCode,
          caseNumber = caseNumber,
          oasysAssessmentType = OasysAssessmentType.SHORT_FORM_PSR
        )
      )

      verify(exactly = 0) { assessmentRepository.save(any()) }
      verify(exactly = 0) { courtCaseRestClient.getCourtCase(courtCode, caseNumber) }
      verify(exactly = 0) { assessmentRepository.save(any()) }
    }
  }
}
