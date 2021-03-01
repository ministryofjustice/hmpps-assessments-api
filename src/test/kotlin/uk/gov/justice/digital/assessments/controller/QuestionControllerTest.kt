package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.*
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

@SqlGroup(
  Sql(scripts = ["classpath:referenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
  Sql(scripts = ["classpath:referenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
@AutoConfigureWebTestClient
class QuestionControllerTest : IntegrationTest() {

  private val assessmentGroupUuid = "e964d699-cf96-4abd-af0e-ddf1f6687a46"
  private val groupUuid = "e353f3df-113d-401c-a3c0-14239fc17cf9"
  private val subgroupUuid = "6afbe596-9956-4620-824b-c6c9000ace7c"
  private val questionSchemaUuid = "fd412ca8-d361-47ab-a189-7acb8ae0675b"
  private val subjectQuestionUuid = "1948af63-07f2-4a8c-9e4c-0ec347bd6ba8"
  private val answerSchemaUuid = "464e25da-f843-43b6-8223-4af415abda0c"
  private val subquestionUuid = "b9dd3680-c4d6-403e-8f27-8d65481cbf44"

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
  fun `get all reference questions and answers for group by uuid`() {
    val questionsGroup = webTestClient.get().uri("/questions/$groupUuid")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupWithContentsDto>()
      .returnResult()
      .responseBody

    assertThat(questionsGroup?.groupId).isEqualTo(UUID.fromString(groupUuid))

    val questionRefs = questionsGroup?.contents
    val questionRef = questionRefs?.first() as GroupQuestionDto
    assertThat(questionRef.questionId).isEqualTo(UUID.fromString(questionSchemaUuid))

    val answerSchemaUuids = questionRef.answerSchemas?.map { it.answerSchemaUuid }
    assertThat(answerSchemaUuids).contains(UUID.fromString(answerSchemaUuid))

    val subGroup = questionRefs?.last() as GroupWithContentsDto
    assertThat(subGroup.groupId).isEqualTo(UUID.fromString(subgroupUuid))
    assertThat(subGroup.contents.size).isEqualTo(1)
    val subQuestion = subGroup.contents.first() as GroupQuestionDto
    assertThat(subQuestion.questionId).isEqualTo(UUID.fromString(subquestionUuid))
  }

  @Test
  fun `verify dependency conditionals`() {
    val questions = webTestClient.get().uri("/questions/Group code")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupWithContentsDto>()
      .returnResult()
      .responseBody
      ?.contents

    val subjectQuestion = questions?.find { (it as GroupQuestionDto).questionId.toString() == subjectQuestionUuid } as GroupQuestionDto
    assertThat(subjectQuestion.conditional).isTrue()

    val triggerQuestion = questions.find { (it as GroupQuestionDto).questionId.toString() == questionSchemaUuid } as GroupQuestionDto
    assertThat(triggerQuestion.conditional).isFalse()
    val yesAnswer = triggerQuestion.answerSchemas?.find { it.value == "true" }
    assertThat(yesAnswer?.conditionals?.first()?.conditional.toString()).isEqualTo(subjectQuestionUuid)
    val noAnswer = triggerQuestion.answerSchemas?.find { it.value == "false" }
    assertThat(noAnswer?.conditionals?.first()?.conditional).isNull()
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

    assertThat(groupSummaries).hasSize(3)

    val groupInfo = groupSummaries?.first()

    assertThat(groupInfo?.groupId).isEqualTo(UUID.fromString(groupUuid))
    assertThat(groupInfo?.title).isEqualTo("Heading 1")
    assertThat(groupInfo?.contentCount).isEqualTo(3)
    assertThat(groupInfo?.groupCount).isEqualTo(0)
    assertThat(groupInfo?.questionCount).isEqualTo(2)
  }

  @Test
  fun `section for top-level group by uuid`() {
    val assessmentGroup = webTestClient.get().uri("/questions/$assessmentGroupUuid/summary")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupSectionsDto>()
      .returnResult()
      .responseBody

    assertThat(assessmentGroup.groupId).isEqualTo(UUID.fromString(assessmentGroupUuid))

    val sections = assessmentGroup.contents!!
    assertThat(sections.size).isEqualTo(1)

    val section = sections.first()
    assertThat(section.groupId).isEqualTo(UUID.fromString(groupUuid))

    val subsections = section.contents!!
    assertThat(subsections.size).isEqualTo(1)

    val subsection = subsections.first()
    assertThat(subsection.groupId).isEqualTo(UUID.fromString(subgroupUuid))
    assertThat(subsection.contents).isNull()
  }
}
