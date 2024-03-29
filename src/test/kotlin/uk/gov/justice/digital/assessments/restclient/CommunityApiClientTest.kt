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

class CommunityApiClientTest : IntegrationTest() {
  @Autowired
  internal lateinit var communityApiRestClient: CommunityApiRestClient

  @Nested
  @DisplayName("Delius LAO rules")
  inner class DeliusLAORules {

    private val validCRN = "DX12340A"
    private val invalidCRN = "OX123456"

    @Test
    fun `verify Offender Access returns DTO`() {
      val userAccessDto = communityApiRestClient.verifyUserAccess(validCRN, "USER")
      assertThat(userAccessDto.userExcluded).isFalse
      assertThat(userAccessDto.userRestricted).isFalse
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
