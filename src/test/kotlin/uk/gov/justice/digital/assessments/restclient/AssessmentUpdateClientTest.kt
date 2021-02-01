package uk.gov.justice.digital.assessments.restclient

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

class AssessmentUpdateClientTest : IntegrationTest() {
  @Autowired
  internal lateinit var assessmentUpdateRestClient: AssessmentUpdateRestClient

  val crn = "DX12340A"

  @Test
  fun `create OASys Offender`() {
    val offenderPk = assessmentUpdateRestClient.createOasysOffender(crn)
    assertThat(offenderPk).isEqualTo(1)
  }
}
