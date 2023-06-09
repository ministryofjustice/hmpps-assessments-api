package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.groups.GroupContentDto
import uk.gov.justice.digital.assessments.api.groups.GroupSectionsDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000000")
class AssessmentReferenceDataControllerTest : IntegrationTest() {
  private val assessmentGroupUuid = "ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4"
  private val groupUuid = "2bd35476-ac9b-4f15-ac7d-ea6943ccc120"
  private val subgroupUuid1 = "667e9967-275f-4d23-bd02-7b5e3f3e1647"
  private val subgroupUuid2 = "d633d6e1-e252-4c09-a21c-c8cc558bce12"
  private val subgroupUuid3 = "b9114d94-2500-456e-8d2e-777703dfd6bc"

  @Test
  fun `section for top-level group for an assessment by assessment type`() {
    val assessmentGroup = webTestClient.get().uri("/assessments/UPW/summary")
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupSectionsDto>()
      .returnResult()
      .responseBody

    assertThat(assessmentGroup).isNotNull
    assertThat(assessmentGroup?.groupId).isEqualTo(UUID.fromString(assessmentGroupUuid))
    assertThat(assessmentGroup?.groupCode).isEqualTo("assessment")
    assertThat(assessmentGroup?.title).isEqualTo("Unpaid Work Assessment")

    val sections = assessmentGroup?.contents.orEmpty()
    assertThat(sections.size).isEqualTo(6)

    val section = sections.first()
    assertThat(section.groupId).isEqualTo(UUID.fromString(groupUuid))
    assertThat(section.groupCode).isEqualTo("diversity_section")
    assertThat(section.title).isEqualTo("Diversity section")

    val subsections = section.contents!!
    assertThat(subsections.size).isEqualTo(3)

    val subsection1 = subsections[0]
    assertThat(subsection1.groupId).isEqualTo(UUID.fromString(subgroupUuid1))
    assertThat(subsection1.groupCode).isEqualTo("cultural_info")
    assertThat(subsection1.title).isEqualTo("Cultural or Religious adjustments")

    val subsection2 = subsections[1]
    assertThat(subsection2.groupId).isEqualTo(UUID.fromString(subgroupUuid2))
    assertThat(subsection2.groupCode).isEqualTo("placement_preferences")
    assertThat(subsection2.title).isEqualTo("Placement preferences")

    val subsection3 = subsections[2]
    assertThat(subsection3.groupId).isEqualTo(UUID.fromString(subgroupUuid3))
    assertThat(subsection3.groupCode).isEqualTo("placement_gender_preferences")
    assertThat(subsection3.title).isEqualTo("Placement preferences based on gender identity")
  }

  @Test
  fun `get flattened questions for assessment type`() {
    val assessmentGroup = webTestClient.get().uri("/assessments/UPW/questions")
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<List<GroupContentDto>>()
      .returnResult()
      .responseBody

    assertThat(assessmentGroup).hasSize(169)
  }
}
