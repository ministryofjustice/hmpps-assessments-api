package uk.gov.justice.digital.assessments.restclient

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiAuthorisationException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiEntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiInvalidRequestException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

class CommunityApiClientTest : IntegrationTest() {
  @Autowired
  internal lateinit var communityApiRestClient: CommunityApiRestClient

  val crn = "DX12340A"
  val eventId = 1L

  @Test
  fun `get Delius Offender returns offender DTO`() {
    val offenderDto = communityApiRestClient.getOffender(crn)
    assertThat(offenderDto?.offenderId).isEqualTo(101L)
    assertThat(offenderDto?.otherIds?.crn).isEqualTo(crn)
  }

  @Test
  fun `get Delius Offender returns not found`() {
    assertThrows<ExternalApiEntityNotFoundException> {
      communityApiRestClient.getOffender("invalidNotFound")
    }
  }

  @Test
  fun `get Delius Offender returns bad request`() {
    assertThrows<ExternalApiInvalidRequestException> {
      communityApiRestClient.getOffender("invalidBadRequest")
    }
  }

  @Test
  fun `get Delius Offender returns unauthorised`() {
    assertThrows<ExternalApiAuthorisationException> {
      communityApiRestClient.getOffender("invalidUnauthorized")
    }
  }

  @Test
  fun `get Delius Offender returns forbidden`() {
    assertThrows<ExternalApiForbiddenException> {
      communityApiRestClient.getOffender("invalidForbidden")
    }
  }

  @Test
  fun `get Delius Offender returns unknown exception`() {
    assertThrows<ExternalApiUnknownException> {
      communityApiRestClient.getOffender("invalidNotKnow")
    }
  }

  @Test
  fun `get Delius Offender returns offender DTO with aliases`() {
    val offenderDto = communityApiRestClient.getOffender(crn)
    assertThat(offenderDto?.offenderAliases?.get(0)?.firstName).isEqualTo("John")
    assertThat(offenderDto?.offenderAliases?.get(0)?.surname).isEqualTo("Smithy")
  }

  @Test
  fun `get Delius Conviction returns conviction DTO`() {
    val convictions = communityApiRestClient.getConvictions(crn)
    assertThat(convictions?.get(0)?.convictionId).isEqualTo(2500000001)
    assertThat(convictions?.get(0)?.offences?.get(0)?.mainOffence).isEqualTo(true)
    assertThat(convictions?.get(0)?.offences?.get(0)?.offenceId).isEqualTo("M2500000001")
    assertThat(convictions?.get(1)?.convictionId).isEqualTo(2500000002)
  }
}
