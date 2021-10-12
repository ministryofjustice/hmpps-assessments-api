package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

@AutoConfigureWebTestClient
class AssessmentControllerSchemaTest : IntegrationTest() {
  private val assessmentGroupUuid = "b89429c8-9e3e-4989-b886-9caed4ed0a30"
  private val groupUuid = "5d37254e-d956-488e-89be-1eaec8758ef7"
  private val subgroupUuid = "5606da47-8f27-49a0-a943-0f2696f66186"
  private val subgroupUuid2 = "eb7b7324-f2a6-4902-91ef-709a8fab1f82"
  private val subgroupUuid3 = "6d3a4377-2177-429e-a7fa-6aa2444d14dd"

  private val roshGroupUuid = "65a3924c-4130-4140-b7f4-cc39a52603bb"
  private val roshSubGroupUuid1 = "5d77fc6b-0001-4955-ad54-7f417becc7c8"
  private val roshSubGroupUuid2 = "d9d1df65-9878-4a76-bbae-9e7685ea4efa"
  private val roshSubGroupUuid3 = "61feebd9-afe5-43ef-94a5-27c400453eba"
  private val roshSubGroupUuid4 = "1e1159e1-6092-4e22-9e29-5734654baeda"
  private val roshSubGroupUuid5 = "7773691f-7244-4415-b410-17c495bf9a59"

  @Test
  fun `get all reference questions and answers for assessment schema code`() {
    val groups = webTestClient.get().uri("/assessments/schema/ROSH")
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupWithContentsDto>()
      .returnResult()
      .responseBody

    assertThat(groups).isNotNull
    assertThat(groups.groupId).isEqualTo(UUID.fromString(roshGroupUuid))
    assertThat(groups.groupCode).isEqualTo("pre_sentence_assessment")
    assertThat(groups.title).isEqualTo("Pre-Sentence Assessment")

    val subgroups = groups.contents
    assertThat(subgroups.size).isEqualTo(5)

    val subgroup1 = subgroups[0] as GroupWithContentsDto
    assertThat(subgroup1.groupId).isEqualTo(UUID.fromString(roshSubGroupUuid1))
    assertThat(subgroup1.groupCode).isEqualTo("individual_and_case_details")
    assertThat(subgroup1.title).isEqualTo("Individual and case details")

    val subgroup2 = subgroups[1] as GroupWithContentsDto
    assertThat(subgroup2.groupId).isEqualTo(UUID.fromString(roshSubGroupUuid2))
    assertThat(subgroup2.groupCode).isEqualTo("sources_of_information")
    assertThat(subgroup2.title).isEqualTo("Sources of information")

    val subgroup3 = subgroups[2] as GroupWithContentsDto
    assertThat(subgroup3.groupId).isEqualTo(UUID.fromString(roshSubGroupUuid3))
    assertThat(subgroup3.groupCode).isEqualTo("offences_convictions_and_needs")
    assertThat(subgroup3.title).isEqualTo("Offences, convictions and needs")

    val subgroup4 = subgroups[3] as GroupWithContentsDto
    assertThat(subgroup4.groupId).isEqualTo(UUID.fromString(roshSubGroupUuid4))
    assertThat(subgroup4.groupCode).isEqualTo("rosh_screening")
    assertThat(subgroup4.title).isEqualTo("ROSH screening")

    val subgroup5 = subgroups[4] as GroupWithContentsDto
    assertThat(subgroup5.groupId).isEqualTo(UUID.fromString(roshSubGroupUuid5))
    assertThat(subgroup5.groupCode).isEqualTo("rosh_full_analysis")
    assertThat(subgroup5.title).isEqualTo("ROSH full analysis")
  }

  @Test
  fun `section for top-level group for an assessment by assessment schema code`() {
    val assessmentGroup = webTestClient.get().uri("/assessments/schema/RSR/summary")
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<GroupSectionsDto>()
      .returnResult()
      .responseBody

    assertThat(assessmentGroup).isNotNull
    assertThat(assessmentGroup.groupId).isEqualTo(UUID.fromString(assessmentGroupUuid))
    assertThat(assessmentGroup.groupCode).isEqualTo("rsr_only")
    assertThat(assessmentGroup.title).isEqualTo("RSR Only")

    val sections = assessmentGroup?.contents!!
    assertThat(sections.size).isEqualTo(1)

    val section = sections.first()
    assertThat(section.groupId).isEqualTo(UUID.fromString(groupUuid))
    assertThat(section.groupCode).isEqualTo("risk_of_serious_recidivism_rsr_assessment")
    assertThat(section.title).isEqualTo("Risk of Serious Recidivism (RSR) assessment")

    val subsections = section.contents!!
    assertThat(subsections.size).isEqualTo(3)

    val subsection1 = subsections[0]
    assertThat(subsection1.groupId).isEqualTo(UUID.fromString(subgroupUuid))
    assertThat(subsection1.groupCode).isEqualTo("risk_of_serious_recidivism_rsr_assessment_landing")
    assertThat(subsection1.title).isEqualTo("Risk of Serious Recidivism (RSR) assessment")

    val subsection2 = subsections[1]
    assertThat(subsection2.groupId).isEqualTo(UUID.fromString(subgroupUuid2))
    assertThat(subsection2.groupCode).isEqualTo("offences_and_convictions")
    assertThat(subsection2.title).isEqualTo("Offences and convictions")

    val subsection3 = subsections[2]
    assertThat(subsection3.groupId).isEqualTo(UUID.fromString(subgroupUuid3))
    assertThat(subsection3.groupCode).isEqualTo("rsr_needs")
    assertThat(subsection3.title).isEqualTo("Needs")
  }
}
