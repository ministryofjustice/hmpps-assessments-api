package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.DeliusEventType
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.time.LocalDate

@AutoConfigureWebTestClient
internal class OffenderControllerTest : IntegrationTest() {

  @Test
  fun `should handle request when EventType is absent`() {

    val crn = "DX5678A"
    val eventId = 1
    val path = "/offender/crn/$crn/eventId/$eventId"

    val offenderDto = webTestClient.get().uri(path)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<OffenderDto>()
      .returnResult()
      .responseBody

    Assertions.assertThat(offenderDto).isEqualTo(
      OffenderDto(
        offenderId = 101,
        firstName = "John",
        surname = "Smith",
        dateOfBirth = LocalDate.of(1979, 8, 18),
        dateOfBirthAliases = listOf("1979-09-18", "1979-08-18"),
        gender = "Male",
        crn = crn,
        pncNumber = "A/1234560BA",
        firstNameAliases = listOf("John", "Jonny"),
        surnameAliases = listOf("Smithy"),
        offence = OffenceDto(
          convictionId = 2500000223,
          convictionIndex = 1,
          offenceCode = "046",
          codeDescription = "Stealing from shops and stalls (shoplifting)",
          offenceSubCode = "00",
          subCodeDescription = "Stealing from shops and stalls (shoplifting)",
          sentenceDate = LocalDate.of(2014, 8, 25)
        )
      )
    )
  }

  @ParameterizedTest(name = "Delius Event type = {0}")
  @MethodSource("getConvictionData")
  fun `should return offence details Dto given provided parameters`(
    eventType: DeliusEventType,
    expectedConvictionId: Long,
    eventId: Long
  ) {

    val crn = "DX5678A"
    val path = "/offender/crn/$crn/eventType/$eventType/eventId/$eventId"

    val offenderDto = webTestClient.get().uri(path)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<OffenderDto>()
      .returnResult()
      .responseBody

    Assertions.assertThat(offenderDto).isEqualTo(
      OffenderDto(
        offenderId = 101,
        firstName = "John",
        surname = "Smith",
        dateOfBirth = LocalDate.of(1979, 8, 18),
        dateOfBirthAliases = listOf("1979-09-18", "1979-08-18"),
        gender = "Male",
        crn = crn,
        pncNumber = "A/1234560BA",
        firstNameAliases = listOf("John", "Jonny"),
        surnameAliases = listOf("Smithy"),
        offence = OffenceDto(
          convictionId = expectedConvictionId,
          convictionIndex = 1,
          offenceCode = "046",
          codeDescription = "Stealing from shops and stalls (shoplifting)",
          offenceSubCode = "00",
          subCodeDescription = "Stealing from shops and stalls (shoplifting)",
          sentenceDate = LocalDate.of(2014, 8, 25)
        )
      )
    )
  }

  companion object {
    @JvmStatic
    fun getConvictionData(): List<Arguments> {

      return listOf(
        Arguments.of(DeliusEventType.EVENT_INDEX, 2500000223, 1),
        Arguments.of(DeliusEventType.EVENT_ID, 123456, 123456),
      )
    }
  }
}
