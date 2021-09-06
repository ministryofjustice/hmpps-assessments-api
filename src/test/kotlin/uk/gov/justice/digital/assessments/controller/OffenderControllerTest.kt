package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

@SqlGroup(
  Sql(scripts = ["classpath:subject/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
  Sql(scripts = ["classpath:subject/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
class OffenderControllerTest : IntegrationTest() {

  val crn = "DX12340A"
  val eventId = 1
  val oasysUserId = 101L
  @Test
  fun `access forbidden when no authority`() {
    webTestClient.get().uri("/offender/crn/$crn")
      .header("Content-Type", "application/json")
      .exchange()
      .expectStatus().isUnauthorized
  }

  @Test
  fun `get offender returns offender and offence for crn and conviction ID`() {
    val offenderDto = webTestClient.get().uri("/offender/crn/$crn/eventId/$eventId")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<OffenderDto>()
      .returnResult()
      .responseBody

    assertThat(offenderDto?.offenderId).isEqualTo(oasysUserId)
    assertThat(offenderDto?.offence?.mainOffenceId).isEqualTo("M2500000001")
    assertThat(offenderDto?.offence?.offenceCode).isEqualTo("11600")
  }

  @Test
  fun `returns offender with address`() {
    val offenderDto = webTestClient.get().uri("/offender/crn/$crn/eventId/$eventId")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<OffenderDto>()
      .returnResult()
      .responseBody

    assertThat(offenderDto?.offenderId).isEqualTo(oasysUserId)
    assertThat(offenderDto?.address?.address1).isEqualTo("38")
    assertThat(offenderDto?.address?.postcode).isEqualTo("ad21 5dr")
  }

  @Test
  fun `returns not found for invalid crn`() {
    val invalidCrn = "invalidNotFound"
    webTestClient.get().uri("/offender/crn/$invalidCrn/eventId/$eventId")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `returns offender with aliases`() {
    val offenderDto = webTestClient.get().uri("/offender/crn/$crn/eventId/$eventId")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<OffenderDto>()
      .returnResult()
      .responseBody

    assertThat(offenderDto?.offenderId).isEqualTo(oasysUserId)
    assertThat(offenderDto?.firstNameAliases).containsOnly("John", "Jonny")
    assertThat(offenderDto?.surnameAliases).containsOnly("Smithy")
  }

  @Test
  fun `returns offence with category codes and code descriptions`() {
    val offenderDto = webTestClient.get().uri("/offender/crn/$crn/eventId/$eventId")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<OffenderDto>()
      .returnResult()
      .responseBody

    assertThat(offenderDto?.offenderId).isEqualTo(oasysUserId)
    assertThat(offenderDto?.offence?.offenceCode).isEqualTo("11600")
    assertThat(offenderDto?.offence?.offenceDescription).isEqualTo("Fishery Laws - 11600")
    assertThat(offenderDto?.offence?.categoryCode).isEqualTo("116")
    assertThat(offenderDto?.offence?.categoryDescription).isEqualTo("Fishery Laws")
    assertThat(offenderDto?.offence?.subCategoryCode).isEqualTo("00")
    assertThat(offenderDto?.offence?.subCategoryDescription).isEqualTo("Fishery Laws")
  }
}
