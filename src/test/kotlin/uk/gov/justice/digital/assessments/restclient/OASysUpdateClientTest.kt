package uk.gov.justice.digital.assessments.restclient

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OasysAssessmentType
import uk.gov.justice.digital.assessments.restclient.assessmentapi.RoleNames
import uk.gov.justice.digital.assessments.restclient.assessmentapi.Roles
import uk.gov.justice.digital.assessments.services.dto.OasysAnswer
import uk.gov.justice.digital.assessments.services.exceptions.DuplicateOffenderRecordException
import uk.gov.justice.digital.assessments.services.exceptions.ExceptionReason
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
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
  val assessmentType = OasysAssessmentType.SHORT_FORM_PSR
  val forbiddenCrn = "DX12340B"
  val duplicateCrn = "DX12340C"
  val serverErrorCrn = "DX12340D"
  val clientErrorCrn = "DX12340E"
  val clientErrorCrnNoBody = "DX12340F"
  val forbiddenOffenderPk = 2L
  val duplicateOffenderPk = 3L
  val serverErrorOffenderPk = 4L
  val validationErrorOffenderPk = 5L

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
  fun `create OASys Offender is Forbidden when RBAC_OTHER with CREATE_OFFENDER is not authorised`() {
    assessmentApiMockServer.stubRBACUnauthorisedPermissions(
      permission = Roles.RBAC_OTHER.name,
      roleName = RoleNames.CREATE_OFFENDER.name,
      assessmentType = null
    )

    val exception =
      assertThrows<ExternalApiForbiddenException> {
        assessmentUpdateRestClient.createOasysOffender(crn)
      }
    assertEquals(exception.message, "One of the permissions is Unauthorized")
    assertEquals(exception.method, HttpMethod.POST)
    assertEquals(exception.url, "/authorisation/permissions")
    assertEquals(exception.client, ExternalService.ASSESSMENTS_API)
    assertEquals(
      exception.moreInfo,
      "STUART WHITLAM in Warwickshire is currently doing an assessment on this offender, created on 12/04/2021."
    )
    assertEquals(exception.reason, ExceptionReason.OASYS_PERMISSION)
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
    val returnAssessmentPk = assessmentUpdateRestClient.createAssessment(offenderPk, assessmentType)
    assertThat(returnAssessmentPk).isEqualTo(1)
  }

  @Test
  fun `create OASys Assessment is Forbidden when OFF_ASSESSMENT_CREATE is not authorised`() {
    val exception =
      assertThrows<ExternalApiForbiddenException> {
        assessmentUpdateRestClient.createAssessment(
          7276800,
          assessmentType
        )
      }
    assertEquals(exception.message, "One of the permissions is Unauthorized")
    assertEquals(exception.method, HttpMethod.POST)
    assertEquals(exception.url, "/authorisation/permissions")
    assertEquals(exception.client, ExternalService.ASSESSMENTS_API)
    assertEquals(
      exception.moreInfo,
      "STUART WHITLAM in Warwickshire is currently doing an assessment on this offender, created on 12/04/2021."
    )
    assertEquals(exception.reason, ExceptionReason.OASYS_PERMISSION)
  }

  @Test
  fun `create OASys Assessment throws exception when forbidden response received`() {
    assertThatThrownBy { assessmentUpdateRestClient.createAssessment(forbiddenOffenderPk, assessmentType) }
      .isInstanceOf(OASysUserPermissionException::class.java)
  }

  @Test
  fun `create OASys Assessment throws exception when duplicate error response received`() {
    assertThatThrownBy { assessmentUpdateRestClient.createAssessment(duplicateOffenderPk, assessmentType) }
      .isInstanceOf(DuplicateOffenderRecordException::class.java)
  }

  @Test
  fun `create OASys Assessment throws exception on server error`() {
    assertThatThrownBy { assessmentUpdateRestClient.createAssessment(serverErrorOffenderPk, assessmentType) }
      .isInstanceOf(ExternalApiUnknownException::class.java)
  }

  @Test
  fun `update OASys Assessment`() {
    val answers = setOf(
      OasysAnswer("ROSH", 1, "R1.3", "YES", false)
    )
    val returnAssessment = assessmentUpdateRestClient.updateAssessment(offenderPk, assessmentType, oasysSetPk, answers)
    assertThat(returnAssessment?.oasysSetPk).isEqualTo(1)
  }

  @Test
  fun `update OASys Assessment for user that doesn't have Rbac permission ASSESSMENT_EDIT throws ExternalApiForbiddenException`() {
    val answers = setOf(
      OasysAnswer("ROSH", 1, "R1.3", "YES", false)
    )

    val exception =
      assertThrows<ExternalApiForbiddenException> {
        assessmentUpdateRestClient.updateAssessment(7276800, assessmentType, oasysSetPk, answers)
      }
    assertEquals(exception.message, "One of the permissions is Unauthorized")
    assertEquals(exception.method, HttpMethod.POST)
    assertEquals(exception.url, "/authorisation/permissions")
    assertEquals(exception.client, ExternalService.ASSESSMENTS_API)
    assertEquals(
      exception.moreInfo,
      "STUART WHITLAM in Warwickshire is currently doing an assessment on this offender, created on 12/04/2021."
    )
    assertEquals(exception.reason, ExceptionReason.OASYS_PERMISSION)
  }

  @Test
  fun `update OASys Assessment throws exception when forbidden response received`() {
    assertThatThrownBy { assessmentUpdateRestClient.createAssessment(forbiddenOffenderPk, assessmentType) }
      .isInstanceOf(OASysUserPermissionException::class.java)
  }

  @Test
  fun `update OASys Assessment throws exception on server error`() {
    assertThatThrownBy { assessmentUpdateRestClient.createAssessment(serverErrorOffenderPk, assessmentType) }
      .isInstanceOf(ExternalApiUnknownException::class.java)
  }

  @Test
  fun `complete OASys Assessment`() {
    val returnAssessment = assessmentUpdateRestClient.completeAssessment(offenderPk, assessmentType, oasysSetPk)
    assertThat(returnAssessment?.oasysSetPk).isEqualTo(1)
  }

  @Test
  fun `complete OASys Assessment for user that doesn't have Rbac permission ASSESSMENT_EDIT throws ExternalApiForbiddenException`() {
    val exception =
      assertThrows<ExternalApiForbiddenException> {
        assessmentUpdateRestClient.completeAssessment(7276800, assessmentType, oasysSetPk)
      }
    assertEquals(exception.message, "One of the permissions is Unauthorized")
    assertEquals(exception.method, HttpMethod.POST)
    assertEquals(exception.url, "/authorisation/permissions")
    assertEquals(exception.client, ExternalService.ASSESSMENTS_API)
    assertEquals(
      exception.moreInfo,
      "STUART WHITLAM in Warwickshire is currently doing an assessment on this offender, created on 12/04/2021."
    )
    assertEquals(exception.reason, ExceptionReason.OASYS_PERMISSION)
  }

  @Test
  fun `complete OASys Assessment with validation errors`() {
    val returnAssessment =
      assessmentUpdateRestClient.completeAssessment(validationErrorOffenderPk, assessmentType, oasysSetPk)
    assertThat(returnAssessment?.oasysSetPk).isEqualTo(1)
    assertThat(returnAssessment?.validationErrorDtos).hasSize(1)
  }

  @Test
  fun `complete OASys Assessment throws exception when forbidden response received`() {
    assertThatThrownBy {
      assessmentUpdateRestClient.completeAssessment(
        forbiddenOffenderPk,
        assessmentType,
        oasysSetPk
      )
    }
      .isInstanceOf(OASysUserPermissionException::class.java)
  }

  @Test
  fun `complete OASys Assessment throws exception on server error`() {
    assertThatThrownBy {
      assessmentUpdateRestClient.completeAssessment(
        serverErrorOffenderPk,
        assessmentType,
        oasysSetPk
      )
    }
      .isInstanceOf(ExternalApiUnknownException::class.java)
  }
}
