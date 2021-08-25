package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.utils.offenderStubResource.OffenderStubDto
import java.time.LocalDate

@ActiveProfiles
class OffenderStubControllerTest : IntegrationTest() {

  @Test
  fun `get offender stub creates and returns new offender stub`() {
    assessmentApiMockServer.stubOffenderStubs()
    communityApiMockServer.stubGetPrimaryIds()
    assessmentUpdateMockServer.stubOffenderStub()
    val offenderDto = webTestClient.get().uri("/offender/stub/")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<OffenderStubDto>()
      .returnResult()
      .responseBody

    assertThat(offenderDto).isEqualTo(
      OffenderStubDto(
        crn = "DX12340A",
        pnc = "A/1234560BA",
        familyName = "Smith",
        forename1 = "John",
        gender = "F",
        dateOfBirth = LocalDate.of(1979,8,18),
        areaCode = "WWS"
      )
    )
  }
}
