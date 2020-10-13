package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.GroupSummaryDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import java.util.*

@SqlGroup(
        Sql(scripts = ["classpath:referenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
        Sql(scripts = ["classpath:referenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD))
@AutoConfigureWebTestClient
class QuestionControllerTest : IntegrationTest() {

    private val groupUuid = "e353f3df-113d-401c-a3c0-14239fc17cf9"
    private val questionSchemaUuid = "fd412ca8-d361-47ab-a189-7acb8ae0675b"
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
                .expectBody<GroupWithContentsDto>()
                .returnResult()
                .responseBody

        assertThat(questionsGroup?.groupId).isEqualTo(UUID.fromString(groupUuid))

        val questionRefs = questionsGroup?.contents
        val questionRef = questionRefs?.get(0) as GroupQuestionDto
        assertThat(questionRef.questionId).isEqualTo(UUID.fromString(questionSchemaUuid))

        val answerRefs = questionRef.answerSchemas
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

    @Test
    fun `list groups`() {
        val groupSummaries = webTestClient.get().uri("/questions/list")
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<List<GroupSummaryDto>>()
                .returnResult()
                .responseBody

        assertThat(groupSummaries).hasSize(1)

        val groupInfo = groupSummaries.first()

        assertThat(groupInfo.groupId).isEqualTo(groupUuid.toString())
        assertThat(groupInfo.title).isEqualTo("Heading 1")
        assertThat(groupInfo.contentCount).isEqualTo(1)
        assertThat(groupInfo.groupCount).isEqualTo(0)
        assertThat(groupInfo.questionCount).isEqualTo(1)

    }
}
