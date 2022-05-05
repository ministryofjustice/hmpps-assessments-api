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

@AutoConfigureWebTestClient
class QuestionControllerTest : IntegrationTest() {

  private val questionSchemaUuid = "ed495c57-21f3-4388-87e6-57017a6999b1"

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
    val questionsGroup = webTestClient.get().uri("/questions/risk_to_self")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupWithContentsDto>()
      .returnResult()
      .responseBody

    assertThat(questionsGroup?.groupId).isEqualTo(UUID.fromString("991117ce-8e1a-4c1c-9cd4-962c7979fce2"))

    val questionRefs = questionsGroup?.contents
    val questionRef = questionRefs?.first() as GroupQuestionDto
    assertThat(questionRef.questionId).isEqualTo(UUID.fromString("f2de0ecb-b004-47c9-98c4-71ad81f84d70"))
    val questionRef2 = questionRefs?.get(1) as GroupQuestionDto
    assertThat(questionRef2.questionId).isEqualTo(UUID.fromString("b7d3e56e-4df0-4152-b92d-edb51e2e95a9"))

    val answerSchemaUuids = questionRef2.answers?.map { it.answerUuid }
    assertThat(answerSchemaUuids).contains(
      UUID.fromString("9541bbcd-dc4f-4d38-8a7c-0ddda92e82d4"),
      UUID.fromString("2b45f6bc-0fa0-4713-a25b-a14e96e8007b"),
      UUID.fromString("199de7bd-6191-41b5-bb3c-5e1f888b3755")
    )
  }

  @Test
  fun `verify dependency conditionals`() {
    val questions = webTestClient.get().uri("/questions/offences_and_convictions")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupWithContentsDto>()
      .returnResult()
      .responseBody
      ?.contents

    val subjectQuestion =
      questions?.find { (it as GroupQuestionDto).questionId.toString() == "3662710d-ce3e-4e45-bce3-caa4155872aa" } as GroupQuestionDto
    assertThat(subjectQuestion.conditional).isTrue
    assertThat(subjectQuestion.questionText).isEqualTo("Does the current offence have a sexual motivation?")

    val triggerQuestion =
      questions.find { (it as GroupQuestionDto).questionId.toString() == "58d3efd1-65a1-439b-952f-b2826ffa5e71" } as GroupQuestionDto
    assertThat(triggerQuestion.conditional).isFalse
    assertThat(triggerQuestion.questionText).isEqualTo("Have they ever committed a sexual or sexually motivated offence?")

    val yesAnswer = triggerQuestion.answers?.find { it.value == "YES" }
    assertThat(yesAnswer?.conditionals?.size).isEqualTo(6)
    assertThat(yesAnswer?.conditionals?.map { it.conditional }).contains(
      "current_sexual_offence", "most_recent_sexual_offence_date", "total_sexual_offences_adult",
      "total_sexual_offences_child", "total_sexual_offences_child_image", "total_non_contact_sexual_offences"
    )
    val noAnswer = triggerQuestion.answers?.find { it.value == "NO" }
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

    assertThat(groupSummaries).hasSize(54)

    val groupInfo = groupSummaries?.find { it.groupCode == "risk_to_others" }

    assertThat(groupInfo?.title).isEqualTo("Risk to others")
    assertThat(groupInfo?.groupId).isEqualTo(UUID.fromString("946091d2-4038-4e2b-9283-83cc4876f6ed"))
    assertThat(groupInfo?.contentCount).isEqualTo(32)
    assertThat(groupInfo?.groupCount).isEqualTo(0)
    assertThat(groupInfo?.questionCount).isEqualTo(32)
  }

  @Test
  fun `section for top-level group by group code`() {
    val assessmentGroup = webTestClient.get().uri("/questions/pre_sentence_assessment/summary")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupSectionsDto>()
      .returnResult()
      .responseBody

    assertThat(assessmentGroup?.groupId).isEqualTo(UUID.fromString("65a3924c-4130-4140-b7f4-cc39a52603bb"))

    val sections = assessmentGroup?.contents!!
    assertThat(sections.size).isEqualTo(5)

    val section = sections.first()
    assertThat(section.groupId).isEqualTo(UUID.fromString("5d77fc6b-0001-4955-ad54-7f417becc7c8"))

    val subsections = section.contents!!
    assertThat(subsections.size).isEqualTo(2)

    val subsection = subsections.first()
    assertThat(subsection.groupId).isEqualTo(UUID.fromString("f7dd78a8-7f04-4fde-8a4f-ac2cf0c56ff5"))
    assertThat(subsection.contents).isNull()
  }
}
