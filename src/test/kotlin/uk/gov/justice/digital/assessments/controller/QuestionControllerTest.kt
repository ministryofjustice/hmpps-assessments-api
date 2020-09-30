package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.QuestionGroupDto
import java.util.*

@SqlGroup(
        Sql(scripts = ["classpath:referenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
        Sql(scripts = ["classpath:referenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD))
@AutoConfigureWebTestClient
class QuestionControllerTest : IntegrationTest() {

    private val questionSchemaUuid = "11111111-1111-1111-1111-111111111111"

    @Test
    fun `access forbidden when no authority`() {
        webTestClient.get().uri("/questions/id/$questionSchemaUuid")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun `get all reference questions and answers for group`() {
        val groupUuid = "22222222-2222-2222-2222-222222222222"
        val questions = webTestClient.get().uri("/questions/$groupUuid")
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<QuestionGroupDto>()
                .returnResult()
                .responseBody

        assertThat(questions?.group?.groupUuid).isEqualTo(UUID.fromString(groupUuid))
        assertThat(questions?.questionRefs?.get(0)?.questionSchemaUuid).isEqualTo(UUID.fromString(questionSchemaUuid))

    }
}
