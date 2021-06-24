package uk.gov.justice.digital.assessments.restclient

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient.OffenderContext
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.services.exceptions.DuplicateOffenderRecordException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import uk.gov.justice.digital.assessments.services.exceptions.OASysUserPermissionException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.utils.RequestData

class OASysUpdateClientTest : IntegrationTest() {
  @Autowired
  internal lateinit var assessmentUpdateRestClient: AssessmentUpdateRestClient

  val crn = "DX12340A"
  val offenderPk = 1L
  val oasysSetPk = 1L
  val assessmentType = AssessmentType.SHORT_FORM_PSR
  val forbiddenCrn = "DX12340B"
  val duplicateCrn = "DX12340C"
  val serverErrorCrn = "DX12340D"
  val clientErrorCrn = "DX12340E"
  val clientErrorCrnNoBody = "DX12340F"
  val forbiddenOffenderPk = 2L
  val serverErrorOffenderPk = 4L
  val validationErrorOffenderPk = 5L

  val offenderContext = OffenderContext(1L)
  val forbiddenOffender = OffenderContext(2L)
  val duplicateOffender = OffenderContext(3L)
  val serverErrorOffender = OffenderContext(4L)

  @BeforeEach
  fun setup() {
    MDC.put(RequestData.USER_AREA_HEADER, "WWS")
    MDC.put(RequestData.USER_ID_HEADER, "1")
  }

  @Test
  fun `create OASys Offender`() {
    val returnOffenderPk = assessmentUpdateRestClient.createOasysOffender(crn)
    assertThat(returnOffenderPk).isEqualTo(1)
  }

  @Test
  fun `create OASys Offender throws exception when forbidden response received`() {
    assertThatThrownBy {
      assessmentUpdateRestClient.createOasysOffender(forbiddenCrn)
    }.isInstanceOf(OASysUserPermissionException::class.java)
  }

  @Test
  fun `create OASys Offender throws exception when duplicate error response received`() {
    assertThatThrownBy { assessmentUpdateRestClient.createOasysOffender(duplicateCrn) }
      .isInstanceOf(DuplicateOffenderRecordException::class.java)
  }

  @Test
  fun `create OASys Offender throws exception on server error`() {
    assertThatThrownBy { assessmentUpdateRestClient.createOasysOffender(serverErrorCrn) }
      .isInstanceOf(ExternalApiUnknownException::class.java)
  }

  @Test
  fun `create OASys Offender throws exception on unknown client error`() {
    assertThatThrownBy { assessmentUpdateRestClient.createOasysOffender(clientErrorCrn) }
      .isInstanceOf(ExternalApiUnknownException::class.java)
  }

  @Test
  fun `create OASys Offender throws exception on unknown client error without body`() {
    assertThatThrownBy { assessmentUpdateRestClient.createOasysOffender(clientErrorCrnNoBody) }
      .isInstanceOf(ExternalApiUnknownException::class.java)
  }

  @Test
  fun `create OASys Assessment`() {
    val returnAssessmentPk = assessmentUpdateRestClient.createAssessment(offenderContext, assessmentType)
    assertThat(returnAssessmentPk).isEqualTo(1)
  }

  @Test
  fun `create OASys Assessment throws exception when forbidden response received`() {
    assertThatThrownBy { assessmentUpdateRestClient.createAssessment(forbiddenOffender, assessmentType) }
      .isInstanceOf(OASysUserPermissionException::class.java)
  }

  @Test
  fun `create OASys Assessment throws exception when duplicate error response received`() {
    assertThatThrownBy { assessmentUpdateRestClient.createAssessment(duplicateOffender, assessmentType) }
      .isInstanceOf(DuplicateOffenderRecordException::class.java)
  }

  @Test
  fun `create OASys Assessment throws exception on server error`() {
    assertThatThrownBy { assessmentUpdateRestClient.createAssessment(serverErrorOffender, assessmentType) }
      .isInstanceOf(ExternalApiUnknownException::class.java)
  }

  @Test
  fun `update OASys Assessment`() {
    val answers = setOf(
      OasysAnswer("ROSH", 1, "R1.3", "YES", false)
    )
    val returnAssessment = assessmentUpdateRestClient.updateAssessment(offenderPk, oasysSetPk, assessmentType, answers)
    assertThat(returnAssessment?.oasysSetPk).isEqualTo(1)
  }

  @Test
  fun `update OASys Assessment throws exception when forbidden response received`() {
    assertThatThrownBy { assessmentUpdateRestClient.createAssessment(forbiddenOffender, assessmentType) }
      .isInstanceOf(OASysUserPermissionException::class.java)
  }

  @Test
  fun `update OASys Assessment throws exception on server error`() {
    assertThatThrownBy { assessmentUpdateRestClient.createAssessment(serverErrorOffender, assessmentType) }
      .isInstanceOf(ExternalApiUnknownException::class.java)
  }

  @Test
  fun `complete OASys Assessment`() {
    val returnAssessment = assessmentUpdateRestClient.completeAssessment(offenderPk, oasysSetPk, assessmentType)
    assertThat(returnAssessment?.oasysSetPk).isEqualTo(1)
  }

  @Test
  fun `complete OASys Assessment with validation errors`() {
    val returnAssessment = assessmentUpdateRestClient.completeAssessment(validationErrorOffenderPk, oasysSetPk, assessmentType)
    assertThat(returnAssessment?.oasysSetPk).isEqualTo(1)
    assertThat(returnAssessment?.validationErrorDtos).hasSize(1)
  }

  @Test
  fun `complete OASys Assessment throws exception when forbidden response received`() {
    assertThatThrownBy { assessmentUpdateRestClient.completeAssessment(forbiddenOffenderPk, oasysSetPk, assessmentType) }
      .isInstanceOf(OASysUserPermissionException::class.java)
  }

  @Test
  fun `complete OASys Assessment throws exception on server error`() {
    assertThatThrownBy { assessmentUpdateRestClient.completeAssessment(serverErrorOffenderPk, oasysSetPk, assessmentType) }
      .isInstanceOf(ExternalApiUnknownException::class.java)
  }
}
