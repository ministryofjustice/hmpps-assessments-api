package uk.gov.justice.digital.assessments.restclient

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiAuthorisationException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiEntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiInvalidRequestException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.time.LocalDate

class CommunityApiClientTest : IntegrationTest() {
  @Autowired
  internal lateinit var communityApiRestClient: CommunityApiRestClient

  @Nested
  @DisplayName("get Delius offender details")
  inner class GetDeliusOffenderDetails {

    val crn = "DX12340A"

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
      assertThat(convictions?.get(0)?.convictionId).isEqualTo(2500000223L)

      assertThat(convictions?.get(0)?.offences?.get(0)?.mainOffence).isEqualTo(true)
      assertThat(convictions?.get(0)?.offences?.get(0)?.offenceId).isEqualTo("M2500000223")

      assertThat(convictions?.get(0)?.offences?.get(0)?.detail?.mainCategoryCode).isEqualTo("046")
      assertThat(convictions?.get(0)?.offences?.get(0)?.detail?.mainCategoryDescription).isEqualTo("Stealing from shops and stalls (shoplifting)")
      assertThat(convictions?.get(0)?.offences?.get(0)?.detail?.subCategoryCode).isEqualTo("00")
      assertThat(convictions?.get(0)?.offences?.get(0)?.detail?.subCategoryDescription).isEqualTo("Stealing from shops and stalls (shoplifting)")

      assertThat(convictions?.get(0)?.sentence?.startDate).isEqualTo(LocalDate.of(2014, 8, 25))
    }
  }

  @Nested
  @DisplayName("Delius LAO rules")
  inner class DeliusLAORules {

    val validCRN = "DX12340A"
    val invalidCRN = "OX123456"

    @Test
    fun `verify Offender Access returns DTO`() {
      val userAccessDto = communityApiRestClient.verifyUserAccess(validCRN, "USER")
      assertThat(userAccessDto.userExcluded).isFalse()
      assertThat(userAccessDto.userRestricted).isFalse()
    }

    @Test
    fun `verify Offender Access returns forbidden`() {
      val exception = assertThrows<ExternalApiForbiddenException> {
        communityApiRestClient.verifyUserAccess(invalidCRN, "user1")
      }
      assertThat(exception.moreInfo).containsAll(listOf("excluded", "restricted"))
    }

    @Test
    fun `verify Offender Access not found`() {
      assertThrows<ExternalApiEntityNotFoundException> {
        communityApiRestClient.verifyUserAccess("invalidNotFound", "user1")
      }
    }

    @Test
    fun `verify Offender Access bad request`() {
      assertThrows<ExternalApiInvalidRequestException> {
        communityApiRestClient.verifyUserAccess("invalidBadRequest", "user1")
      }
    }

    @Test
    fun `verify Offender Access returns unauthorised`() {
      assertThrows<ExternalApiAuthorisationException> {
        communityApiRestClient.verifyUserAccess("invalidUnauthorized", "user1")
      }
    }

    @Test
    fun `verify Offender Access returns unknown exception`() {
      assertThrows<ExternalApiUnknownException> {
        communityApiRestClient.verifyUserAccess("invalidNotKnow", "user1")
      }
    }
  }
}
