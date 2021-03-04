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
  val convictionId = 636401162L
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
    val offenderDto = webTestClient.get().uri("/offender/crn/$crn/conviction/$convictionId")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<OffenderDto>()
      .returnResult()
      .responseBody

    assertThat(offenderDto?.offenderId).isEqualTo(oasysUserId)
    assertThat(offenderDto?.offence?.convictionId).isEqualTo(convictionId)
    assertThat(offenderDto?.offence?.mainOffenceId).isEqualTo("offence1")
    assertThat(offenderDto?.offence?.offenceCode).isEqualTo("code1")
  }

  @Test
  fun `get offender returns offender with address for crn and conviction ID`() {
    val offenderDto = webTestClient.get().uri("/offender/crn/$crn/conviction/$convictionId")
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
  fun `get offender returns not found for invalid crn`() {
    val invalidCrn = "invalid"
    webTestClient.get().uri("/offender/crn/$invalidCrn/conviction/$convictionId")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isNotFound
  }
}
