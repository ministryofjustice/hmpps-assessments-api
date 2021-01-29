package uk.gov.justice.digital.assessments.restclient

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

class CommunityApiClientTest: IntegrationTest() {
    @Autowired
    internal lateinit var communityApiRestClient: CommunityApiRestClient

    val crn = "DX12340A"

    @Test
    fun `get OASys Offender`() {
        val offenderDto = communityApiRestClient.getOffender(crn)
        assertThat(offenderDto?.offenderId).isEqualTo(101L)
        assertThat(offenderDto?.otherIds?.crn).isEqualTo(crn)
    }
}
