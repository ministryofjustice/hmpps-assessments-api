package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionGroupRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

class QuestionGroupRepositoryTest(@Autowired val questionGroupRepository: QuestionGroupRepository) : IntegrationTest() {
  val groupUuid: UUID = UUID.fromString("667e9967-275f-4d23-bd02-7b5e3f3e1647")
  val questionSchemaUuid: UUID = UUID.fromString("6b04e69d-4ffd-46c6-869a-1f4005979848")

  @Test
  fun `fetch group contents`() {
    val questionGroupEntities = questionGroupRepository.findByGroupGroupUuid(groupUuid)
    assertThat(questionGroupEntities).hasSize(26)

    val questionGroupEntity = questionGroupEntities!!.first()
    assertThat(questionGroupEntity.contentType).isEqualTo("question")
    assertThat(questionGroupEntity.contentUuid).isEqualTo(questionSchemaUuid)
  }

  @Test
  fun `list group summaries`() {
    val groupSummaries = questionGroupRepository.listGroups()
    assertThat(groupSummaries).hasSize(28)

    val groupInfo = groupSummaries.find { it.groupCode == "placement_gender_preferences" }

    assertThat(groupInfo?.groupUuid).isEqualTo("b9114d94-2500-456e-8d2e-777703dfd6bc")
    assertThat(groupInfo?.heading).isEqualTo("Placement preferences based on gender identity")
    assertThat(groupInfo?.contentCount).isEqualTo(2)
    assertThat(groupInfo?.groupCount).isEqualTo(0)
    assertThat(groupInfo?.questionCount).isEqualTo(2)
  }
}
