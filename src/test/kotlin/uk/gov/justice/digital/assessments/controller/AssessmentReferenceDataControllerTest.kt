package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.groups.GroupContentDto
import uk.gov.justice.digital.assessments.api.groups.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.groups.GroupWithContentsDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000000")
class AssessmentReferenceDataControllerTest : IntegrationTest() {
  private val assessmentGroupUuid = "ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4"
  private val groupUuid = "2bd35476-ac9b-4f15-ac7d-ea6943ccc120"
  private val subgroupUuid1 = "667e9967-275f-4d23-bd02-7b5e3f3e1647"
  private val subgroupUuid2 = "d633d6e1-e252-4c09-a21c-c8cc558bce12"
  private val subgroupUuid3 = "b9114d94-2500-456e-8d2e-777703dfd6bc"

  private val upwGroupUuid = "ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4"
  private val upwSubGroupUuid1 = "2bd35476-ac9b-4f15-ac7d-ea6943ccc120"
  private val upwSubGroupUuid2 = "95000412-07cb-49aa-8821-6712880e3097"
  private val upwSubGroupUuid3 = "76a2b2a9-69c9-42c1-8d79-46294790b212"
  private val upwSubGroupUuid4 = "9cbfc1ad-a054-44ab-9827-fd1b429ef31d"
  private val upwSubGroupUuid5 = "b0238dcb-e12a-4d07-9986-7214139942d1"
  private val upwSubGroupUuid6 = "d674dfba-4e09-4673-9049-b5e1ee13285f"

  @Test
  fun `get all reference questions and answers for assessment type`() {
    val groups = webTestClient.get().uri("/assessments/UPW")
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupWithContentsDto>()
      .returnResult()
      .responseBody

    assertThat(groups).isNotNull
    assertThat(groups.groupId).isEqualTo(UUID.fromString(upwGroupUuid))
    assertThat(groups.groupCode).isEqualTo("assessment")
    assertThat(groups.title).isEqualTo("Unpaid Work Assessment")

    val subgroups = groups.contents
    assertThat(subgroups.size).isEqualTo(6)

    val subgroup1 = subgroups[0] as GroupWithContentsDto
    assertThat(subgroup1.groupId).isEqualTo(UUID.fromString(upwSubGroupUuid1))
    assertThat(subgroup1.groupCode).isEqualTo("diversity_section")
    assertThat(subgroup1.title).isEqualTo("Diversity section")

    val subgroup2 = subgroups[1] as GroupWithContentsDto
    assertThat(subgroup2.groupId).isEqualTo(UUID.fromString(upwSubGroupUuid2))
    assertThat(subgroup2.groupCode).isEqualTo("risk_section")
    assertThat(subgroup2.title).isEqualTo("Risk section")

    val subgroup3 = subgroups[2] as GroupWithContentsDto
    assertThat(subgroup3.groupId).isEqualTo(UUID.fromString(upwSubGroupUuid3))
    assertThat(subgroup3.groupCode).isEqualTo("placement_restrictions_section")
    assertThat(subgroup3.title).isEqualTo("Placement restrictions section")

    val subgroup4 = subgroups[3] as GroupWithContentsDto
    assertThat(subgroup4.groupId).isEqualTo(UUID.fromString(upwSubGroupUuid4))
    assertThat(subgroup4.groupCode).isEqualTo("employment_education_skills_section")
    assertThat(subgroup4.title).isEqualTo("Employment, education and skills section")

    val subgroup5 = subgroups[4] as GroupWithContentsDto
    assertThat(subgroup5.groupId).isEqualTo(UUID.fromString(upwSubGroupUuid5))
    assertThat(subgroup5.groupCode).isEqualTo("placement_details_section")
    assertThat(subgroup5.title).isEqualTo("Placement details section")

    val subgroup6 = subgroups[5] as GroupWithContentsDto
    assertThat(subgroup6.groupId).isEqualTo(UUID.fromString(upwSubGroupUuid6))
    assertThat(subgroup6.groupCode).isEqualTo("declaration_section")
    assertThat(subgroup6.title).isEqualTo("Declaration section")
  }

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
    assertThat(assessmentGroup.groupId).isEqualTo(UUID.fromString(assessmentGroupUuid))
    assertThat(assessmentGroup.groupCode).isEqualTo("assessment")
    assertThat(assessmentGroup.title).isEqualTo("Unpaid Work Assessment")

    val sections = assessmentGroup?.contents!!
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

    assertThat(assessmentGroup).hasSize(167)
  }
}
