package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.QuestionGroupDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import java.util.*

@SqlGroup(
        Sql(scripts = ["classpath:referenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
        Sql(scripts = ["classpath:referenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD))
@AutoConfigureWebTestClient
class QuestionControllerTest : IntegrationTest() {

    private val groupUuid = "22222222-2222-2222-2222-222222222222"
    private val questionSchemaUuid = "11111111-1111-1111-1111-111111111111"
    private val answerSchemaUuid = "464e25da-f843-43b6-8223-4af415abda0c"

    @Test
    fun `access forbidden when no authority`() {
        webTestClient.get().uri("/questions/id/$questionSchemaUuid")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun `get reference question and answers`() {
        val questionSchema = webTestClient.get().uri("/questions/id/$questionSchemaUuid")
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<QuestionSchemaDto>()
                .returnResult()
                .responseBody

        assertThat(questionSchema?.questionSchemaUuid).isEqualTo(UUID.fromString(questionSchemaUuid))
    }


    @Test
    fun `get all reference questions and answers for group`() {
        val questionsGroup = webTestClient.get().uri("/questions/$groupUuid")
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<QuestionGroupDto>()
                .returnResult()
                .responseBody

        assertThat(questionsGroup?.group?.groupUuid).isEqualTo(UUID.fromString(groupUuid))

        val questionRefs = questionsGroup?.questionRefs
        assertThat(questionRefs?.get(0)?.questionSchemaUuid).isEqualTo(UUID.fromString(questionSchemaUuid))

        val answerRefs = questionRefs?.get(0)?.answerSchemas
        assertThat(answerRefs?.get(0)?.answerSchemaUuid).isEqualTo(UUID.fromString(answerSchemaUuid))
    }

    @Test
    fun `get questions returns not found when group does not exist`() {
        val invalidGroupUuid = UUID.randomUUID()
        webTestClient.get().uri("/questions/$invalidGroupUuid")
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isNotFound
    }
}
