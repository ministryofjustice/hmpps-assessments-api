package uk.gov.justice.digital.assessments.restclient

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.OASysClientException
import uk.gov.justice.digital.assessments.services.exceptions.ReferenceDataAuthorisationException
import uk.gov.justice.digital.assessments.services.exceptions.ReferenceDataInvalidRequestException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

class AssessmentApiTest : IntegrationTest() {
  @Autowired
  internal lateinit var assessmentApiRestClient: AssessmentApiRestClient

  val oasysSetPk = 1L

  @Test
  fun `retrieve OASys Assessment`() {
    val returnedAssessment = assessmentApiRestClient.getOASysAssessment(oasysSetPk)
    assertThat(returnedAssessment?.assessmentId).isEqualTo(1)
  }

  @Test
  fun `retrieve OASys Assessment throws exception when forbidden response received`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getOASysAssessment(2)
    }.isInstanceOf(EntityNotFoundException::class.java)
  }

  @Test
  fun `retrieve OASys Assessment throws exception on server error`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getOASysAssessment(3)
    }.isInstanceOf(OASysClientException::class.java)
  }

  @Test
  fun `retrieve OASys Assessment throws exception on unknown client error`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getOASysAssessment(3)
    }.isInstanceOf(OASysClientException::class.java)
  }

  @Test
  fun `retrieve OASys Assessment throws exception on unknown client error without body`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getOASysAssessment(3)
    }.isInstanceOf(OASysClientException::class.java)
  }

  @Test
  fun `retrieve OASys filtered reference data`() {
    val returnedReferenceData =
      assessmentApiRestClient.getFilteredReferenceData(
        1,
        "TEST_USER",
        "WWS",
        "Team1",
        123456,
        "TEST_USER",
        "SHORT_FORM_PSR",
        "CONTINUE",
        "RSR",
        "assessor_office",
        mapOf("assessor" to "OASYS_ADMIN" )
      )
    assertThat(returnedReferenceData?.get("assessor_office")).isNotEmpty
  }

  @Test
  fun `retrieve OASys filtered reference data throws exception on 500 errors`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getFilteredReferenceData(
        2,
        "TEST_USER",
        "WWS",
        "Team1",
        123456,
        "TEST_USER",
        "SHORT_FORM_PSR",
        "CONTINUE",
        "RSR",
        "assessor_office",
        mapOf("assessor" to "OASYS_ADMIN" )
      )
    }.isInstanceOf(OASysClientException::class.java)
  }

  @Test
  fun `retrieve OASys filtered reference data throws exception on 400 errors`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getFilteredReferenceData(
        3,
        "TEST_USER",
        "WWS",
        "Team1",
        123456,
        "TEST_USER",
        "SHORT_FORM_PSR",
        "CONTINUE",
        "RSR",
        "assessor_office",
        mapOf("assessor" to "OASYS_ADMIN" )
      )
    }.isInstanceOf(ReferenceDataInvalidRequestException::class.java)
  }


  @Test
  fun `retrieve OASys filtered reference data throws exception on 401 errors`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getFilteredReferenceData(
        4,
        "TEST_USER",
        "WWS",
        "Team1",
        123456,
        "TEST_USER",
        "SHORT_FORM_PSR",
        "CONTINUE",
        "RSR",
        "assessor_office",
        mapOf("assessor" to "OASYS_ADMIN" )
      )
    }.isInstanceOf(ReferenceDataAuthorisationException::class.java)
  }

  @Test
  fun `retrieve OASys filtered reference data throws exception on 404 errors`() {
    Assertions.assertThatThrownBy {
      assessmentApiRestClient.getFilteredReferenceData(
        5,
        "TEST_USER",
        "WWS",
        "Team1",
        123456,
        "TEST_USER",
        "SHORT_FORM_PSR",
        "CONTINUE",
        "RSR",
        "assessor_office",
        mapOf("assessor" to "OASYS_ADMIN" )
      )
    }.isInstanceOf(EntityNotFoundException::class.java)
  }
}
