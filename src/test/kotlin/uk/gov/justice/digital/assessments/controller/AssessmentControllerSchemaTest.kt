package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

@SqlGroup(
  Sql(
    scripts = ["classpath:referenceData/before-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
  ),
  Sql(
    scripts = ["classpath:referenceData/after-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
  )
)
@AutoConfigureWebTestClient
class AssessmentControllerSchemaTest : IntegrationTest() {
  private val assessmentGroupUuid = "e964d699-cf96-4abd-af0e-ddf1f6687a46"
  private val groupUuid = "e353f3df-113d-401c-a3c0-14239fc17cf9"
  private val subgroupUuid = "6afbe596-9956-4620-824b-c6c9000ace7c"
  private val questionSchemaUuid = "fd412ca8-d361-47ab-a189-7acb8ae0675b"
  private val answerSchemaUuid = "464e25da-f843-43b6-8223-4af415abda0c"
  private val subquestionUuid = "b9dd3680-c4d6-403e-8f27-8d65481cbf44"

  @Test
  fun `get all reference questions and answers for assessment schema code`() {
    val questionsGroup = webTestClient.get().uri("/assessments/schema/ROSH")
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

    val subGroup = questionRefs.last() as GroupWithContentsDto
    assertThat(subGroup.groupId).isEqualTo(UUID.fromString(subgroupUuid))
    assertThat(subGroup.contents.size).isEqualTo(1)
    val subQuestion = subGroup.contents.first() as GroupQuestionDto
    assertThat(subQuestion.questionId).isEqualTo(UUID.fromString(subquestionUuid))
  }

  @Test
  fun `section for top-level group for an assessment by assessment schema code`() {
    val assessmentGroup = webTestClient.get().uri("/assessments/schema/RSR_ONLY/summary")
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
