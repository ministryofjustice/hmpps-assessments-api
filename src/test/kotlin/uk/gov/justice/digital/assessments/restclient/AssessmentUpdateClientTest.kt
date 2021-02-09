package uk.gov.justice.digital.assessments.restclient

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.services.exceptions.DuplicateOffenderRecordException
import uk.gov.justice.digital.assessments.services.exceptions.OASysClientException
import uk.gov.justice.digital.assessments.services.exceptions.UserNotAuthorisedException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

class AssessmentUpdateClientTest : IntegrationTest() {
  @Autowired
  internal lateinit var assessmentUpdateRestClient: AssessmentUpdateRestClient

  val crn = "DX12340A"
  val offenderPk = 1L
  val assessmentType = AssessmentType.SHORT_FORM_PSR
  val forbiddenCrn = "DX12340B"
  val duplicateCrn = "DX12340C"
  val serverErrorCrn = "DX12340D"
  val clientErrorCrn = "DX12340E"
  val clientErrorCrnNoBody = "DX12340F"
  val forbiddenOffenderPk = 2L
  val duplicateOffenderPk = 3L
  val serverErrorOffenderPk = 4L

  @Test
  fun `create OASys Offender`() {
    val returnOffenderPk = assessmentUpdateRestClient.createOasysOffender(crn)
    assertThat(returnOffenderPk).isEqualTo(1)
  }

  @Test
  fun `create OASys Offender throws exception when forbidden response received`() {
    assertThrows<UserNotAuthorisedException> {
      assessmentUpdateRestClient.createOasysOffender(forbiddenCrn)
    }
  }

  @Test
  fun `create OASys Offender throws exception when duplicate error response received`() {
    assertThrows<DuplicateOffenderRecordException> {
      assessmentUpdateRestClient.createOasysOffender(duplicateCrn)
    }
  }

  @Test
  fun `create OASys Offender throws exception on server error`() {
    assertThrows<OASysClientException> {
      assessmentUpdateRestClient.createOasysOffender(serverErrorCrn)
    }
  }

  @Test
  fun `create OASys Offender throws exception on unknown client error`() {
    assertThrows<OASysClientException> {
      assessmentUpdateRestClient.createOasysOffender(clientErrorCrn)
    }
  }

  @Test
  fun `create OASys Offender throws exception on unknown client error without body`() {
    assertThrows<OASysClientException> {
      assessmentUpdateRestClient.createOasysOffender(clientErrorCrnNoBody)
    }
  }

  @Test
  fun `create OASys Asessment`() {
    val returnAssessmentPk = assessmentUpdateRestClient.createAssessment(offenderPk, assessmentType)
    assertThat(returnAssessmentPk).isEqualTo(1)
  }

  @Test
  fun `create OASys Assessment throws exception when forbidden response received`() {
    assertThrows<UserNotAuthorisedException> {
      assessmentUpdateRestClient.createAssessment(forbiddenOffenderPk, assessmentType)
    }
  }

  @Test
  fun `create OASys Assessment throws exception when duplicate error response received`() {
    assertThrows<DuplicateOffenderRecordException> {
      assessmentUpdateRestClient.createAssessment(duplicateOffenderPk, assessmentType)
    }
  }

  @Test
  fun `create OASys Assessment throws exception on server error`() {
    assertThrows<OASysClientException> {
      assessmentUpdateRestClient.createAssessment(serverErrorOffenderPk, assessmentType)
    }
  }
}
