package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.GroupSummaryDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.api.QuestionDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "60000000")
class QuestionControllerTest : IntegrationTest() {

  private val questionSchemaUuid = "2c88800c-1566-4019-9be1-1c1dfc67d4fb"

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
      .expectBody<QuestionDto>()
      .returnResult()
      .responseBody

    assertThat(questionSchema?.questionUuid).isEqualTo(UUID.fromString(questionSchemaUuid))
  }

  @Test
  fun `get all reference questions and answers for group by group code`() {
    val questionsGroup = webTestClient.get().uri("/questions/placement_gender_preferences")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupWithContentsDto>()
      .returnResult()
      .responseBody

    assertThat(questionsGroup?.groupId).isEqualTo(UUID.fromString("b9114d94-2500-456e-8d2e-777703dfd6bc"))

    val questionRefs = questionsGroup?.contents
    val questionRef = questionRefs?.first() as GroupQuestionDto
    assertThat(questionRef.questionId).isEqualTo(UUID.fromString("b2af0358-56fb-4e45-be76-b661ce829138"))
    val questionRef2 = questionRefs?.get(1) as GroupQuestionDto
    assertThat(questionRef2.questionId).isEqualTo(UUID.fromString("980f7936-682c-4174-91b6-3dcfc684c494"))

    val answerSchemaUuids = questionRef2.answerDtos?.map { it.answerUuid }
    assertThat(answerSchemaUuids).contains(
      UUID.fromString("51c9db72-9c69-4377-818c-b107572eab33"),
      UUID.fromString("70ca6d84-730d-45b2-b531-1d8d13fefadc")
    )
  }

  @Test
  fun `verify dependency conditionals`() {
    val questions = webTestClient.get().uri("/questions/cultural_info")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupWithContentsDto>()
      .returnResult()
      .responseBody
      ?.contents

    val subjectQuestion =
      questions?.find { (it as GroupQuestionDto).questionId.toString() == "e7f8205b-1f2a-4578-943c-154d2a6ee11e" } as GroupQuestionDto
    assertThat(subjectQuestion.conditional).isTrue
    assertThat(subjectQuestion.questionText).isEqualTo("Give details")

    val triggerQuestion =
      questions.find { (it as GroupQuestionDto).questionId.toString() == "5cefd241-cc51-4128-a343-cb7c438a9048" } as GroupQuestionDto
    assertThat(triggerQuestion.conditional).isFalse
    assertThat(triggerQuestion.questionText).isEqualTo("Are adjustments required for cultural or religious reasons?")
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

    assertThat(groupSummaries).hasSize(24)

    val groupInfo = groupSummaries?.find { it.groupCode == "placement_gender_preferences" }

    assertThat(groupInfo?.title).isEqualTo("Placement preferences based on gender identity")
    assertThat(groupInfo?.groupId).isEqualTo(UUID.fromString("b9114d94-2500-456e-8d2e-777703dfd6bc"))
    assertThat(groupInfo?.contentCount).isEqualTo(2)
    assertThat(groupInfo?.groupCount).isEqualTo(0)
    assertThat(groupInfo?.questionCount).isEqualTo(2)
  }

  @Test
  fun `section for top-level group by group code`() {
    val assessmentGroup = webTestClient.get().uri("/questions/assessment/summary")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupSectionsDto>()
      .returnResult()
      .responseBody

    assertThat(assessmentGroup?.groupId).isEqualTo(UUID.fromString("ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4"))

    val sections = assessmentGroup?.contents!!
    assertThat(sections.size).isEqualTo(6)

    val section = sections.first()
    assertThat(section.groupId).isEqualTo(UUID.fromString("2bd35476-ac9b-4f15-ac7d-ea6943ccc120"))

    val subsections = section.contents!!
    assertThat(subsections.size).isEqualTo(3)

    val subsection = subsections.first()
    assertThat(subsection.groupId).isEqualTo(UUID.fromString("667e9967-275f-4d23-bd02-7b5e3f3e1647"))
    assertThat(subsection.contents).isNull()
  }
}
