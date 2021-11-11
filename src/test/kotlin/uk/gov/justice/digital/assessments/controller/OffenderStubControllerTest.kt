package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.utils.offenderStubResource.OffenderAndOffenceStubDto
import java.time.LocalDate

@ActiveProfiles
class OffenderStubControllerTest : IntegrationTest() {

  @Test
  fun `create new offender stub`() {
    assessmentApiMockServer.stubOffenderStubs()
    communityApiMockServer.stubGetPrimaryIds()
    assessmentUpdateMockServer.stubOffenderStub()
    val offenderDto = webTestClient.get().uri("/offender/stub/")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<OffenderAndOffenceStubDto>()
      .returnResult()
      .responseBody

    assertThat(offenderDto).isEqualTo(
      OffenderAndOffenceStubDto(
        crn = "DX12340A",
        pnc = "A/1234560BA",
        familyName = "Smith",
        forename1 = "John",
        gender = "Male",
        dateOfBirth = LocalDate.of(1979, 8, 18),
        areaCode = "WWS",
        offenceCode = "046",
        codeDescription = "Stealing from shops and stalls (shoplifting)",
        offenceSubCode = "00",
        subCodeDescription = "Stealing from shops and stalls (shoplifting)",
        sentenceDate = LocalDate.of(2014, 8, 25)
      )
    )
  }

  @Test
  fun `create offender stub from crn`() {
    assessmentApiMockServer.stubOffenderStubs()
    assessmentUpdateMockServer.stubOffenderStub()
    val crn = "DX12340A"
    val offenderDto = webTestClient.get().uri("/offender/stub/crn/$crn")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<OffenderAndOffenceStubDto>()
      .returnResult()
      .responseBody

    assertThat(offenderDto).isEqualTo(
      OffenderAndOffenceStubDto(
        crn = "DX12340A",
        pnc = "A/1234560BA",
        familyName = "Smith",
        forename1 = "John",
        gender = "Male",
        dateOfBirth = LocalDate.of(1979, 8, 18),
        areaCode = "WWS",
        offenceCode = "046",
        codeDescription = "Stealing from shops and stalls (shoplifting)",
        offenceSubCode = "00",
        subCodeDescription = "Stealing from shops and stalls (shoplifting)",
        sentenceDate = LocalDate.of(2014, 8, 25)
      )
    )
  }
}
