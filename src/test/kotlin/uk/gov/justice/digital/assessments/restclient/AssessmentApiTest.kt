package uk.gov.justice.digital.assessments.restclient

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.restclient.assessmentapi.Roles
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiAuthorisationException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiEntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiInvalidRequestException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.utils.RequestData
import uk.gov.justice.digital.assessments.utils.RequestData.Companion.USER_AREA_HEADER

class AssessmentApiTest : IntegrationTest() {
  @Autowired
  internal lateinit var assessmentApiRestClient: AssessmentApiRestClient

  val oasysSetPk = 1L

  @BeforeEach
  fun setup() {
    MDC.put(USER_AREA_HEADER, "WWS")
    MDC.put(RequestData.USER_ID_HEADER, "1")
  }

  @Test
  fun `retrieve OASys Assessment`() {
    val returnedAssessment = assessmentApiRestClient.getOASysAssessment(oasysSetPk)
    assertThat(returnedAssessment?.assessmentId).isEqualTo(1)
  }

  @Test
  fun `retrieve OASys Assessment throws exception when forbidden response received`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getOASysAssessment(2)
    }.isInstanceOf(ExternalApiEntityNotFoundException::class.java)
  }

  @Test
  fun `retrieve OASys Assessment throws exception on server error`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getOASysAssessment(3)
    }.isInstanceOf(ExternalApiUnknownException::class.java)
  }

  @Test
  fun `retrieve OASys Assessment throws exception on unknown client error`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getOASysAssessment(3)
    }.isInstanceOf(ExternalApiUnknownException::class.java)
  }

  @Test
  fun `retrieve OASys Assessment throws exception on unknown client error without body`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getOASysAssessment(3)
    }.isInstanceOf(ExternalApiUnknownException::class.java)
  }

  @Test
  fun `retrieve OASys Rbac Permissions for create offender assessment missing offenderPk throws exception`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getOASysRBACPermissions(
        roleChecks = setOf(Roles.OFF_ASSESSMENT_CREATE),
        assessmentType = AssessmentType.SHORT_FORM_PSR
      )
    }.isInstanceOf(ExternalApiInvalidRequestException::class.java)
  }

  @Test
  fun `retrieve OASys filtered reference data`() {
    MDC.put(USER_AREA_HEADER, "WWS")
    val returnedReferenceData =
      assessmentApiRestClient.getFilteredReferenceData(
        1,
        123456,
        "SHORT_FORM_PSR",
        "RSR",
        "assessor_office",
        mapOf("assessor" to "OASYS_ADMIN")
      )
    assertThat(returnedReferenceData?.get("assessor_office")).isNotEmpty
  }

  @Test
  fun `retrieve OASys filtered reference data throws exception on 500 errors`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getFilteredReferenceData(
        2,
        123456,
        "SHORT_FORM_PSR",
        "RSR",
        "assessor_office",
        mapOf("assessor" to "OASYS_ADMIN")
      )
    }.isInstanceOf(ExternalApiUnknownException::class.java)
  }

  @Test
  fun `retrieve OASys filtered reference data throws exception on 400 errors`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getFilteredReferenceData(
        3,
        123456,
        "SHORT_FORM_PSR",
        "RSR",
        "assessor_office",
        mapOf("assessor" to "OASYS_ADMIN")
      )
    }.isInstanceOf(ExternalApiInvalidRequestException::class.java)
  }

  @Test
  fun `retrieve OASys filtered reference data throws exception on 401 errors`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getFilteredReferenceData(
        4,
        123456,
        "SHORT_FORM_PSR",
        "RSR",
        "assessor_office",
        mapOf("assessor" to "OASYS_ADMIN")
      )
    }.isInstanceOf(ExternalApiAuthorisationException::class.java)
  }

  @Test
  fun `retrieve OASys filtered reference data throws exception on 404 errors`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getFilteredReferenceData(
        5,
        123456,
        "SHORT_FORM_PSR",
        "RSR",
        "assessor_office",
        mapOf("assessor" to "OASYS_ADMIN")
      )
    }.isInstanceOf(ExternalApiEntityNotFoundException::class.java)
  }
}
