package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.time.LocalDate

@AutoConfigureWebTestClient
internal class OffenderControllerTest : IntegrationTest() {

  @Test
  fun `should handle request when EventType is absent`() {
    val crn = "DX5678A"
    val eventId = 123456
    val path = "/offender/crn/$crn/eventId/$eventId"

    val offenderDto = webTestClient.get().uri(path)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<OffenderDto>()
      .returnResult()
      .responseBody

    assertThat(offenderDto).isEqualTo(
      OffenderDto(
        firstName = "John",
        surname = "Smith",
        dateOfBirth = LocalDate.of(1979, 8, 18),
        dateOfBirthAliases = listOf("1979-09-18", "1979-08-17"),
        gender = "Male",
        crn = crn,
        pncNumber = "A/1234560BA",
        firstNameAliases = listOf("John", "Jonny"),
        surnameAliases = listOf("Smithy", "Smith"),
        offence = OffenceDto(
          eventId = 123456,
          offenceCode = "150",
          codeDescription = "Merchant Shipping Acts",
          offenceSubCode = "00",
          subCodeDescription = "Merchant Shipping Acts",
          sentenceDate = LocalDate.of(2023, 1, 26),
        ),
      ),
    )
  }

  @Test
  fun `should return offence details Dto`() {
    val crn = "DX5678A"
    val eventId = 123456L
    val expectedEventId = 123456L
    val path = "/offender/crn/$crn/eventId/$eventId"

    val offenderDto = webTestClient.get().uri(path)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<OffenderDto>()
      .returnResult()
      .responseBody

    assertThat(offenderDto).isEqualTo(
      OffenderDto(
        firstName = "John",
        surname = "Smith",
        dateOfBirth = LocalDate.of(1979, 8, 18),
        dateOfBirthAliases = listOf("1979-09-18", "1979-08-17"),
        gender = "Male",
        crn = crn,
        pncNumber = "A/1234560BA",
        firstNameAliases = listOf("John", "Jonny"),
        surnameAliases = listOf("Smithy", "Smith"),
        offence = OffenceDto(
          eventId = expectedEventId,
          offenceCode = "150",
          codeDescription = "Merchant Shipping Acts",
          offenceSubCode = "00",
          subCodeDescription = "Merchant Shipping Acts",
          sentenceDate = LocalDate.of(2023, 1, 26),
        ),
      ),
    )
  }
}
