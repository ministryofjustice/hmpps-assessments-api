package uk.gov.justice.digital.assessments.restclient

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

class CourtCaseClientTest: IntegrationTest() {
    @Autowired
    internal lateinit var courtCaseClient: CourtCaseRestClient

    val courtCode = "SHF06"
    val caseNumber = "668911253"

    @Test
    fun `pull court case data`() {
        val courtCase = courtCaseClient.getCourtCase(courtCode, caseNumber);

        assertThat(courtCase?.defendantName).isEqualTo("John Smith")
    }

}