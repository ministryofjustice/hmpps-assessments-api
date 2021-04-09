package uk.gov.justice.digital.assessments.restclient

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.OASysClientException
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
}
