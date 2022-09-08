package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionGroupRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

class QuestionGroupRepositoryTest(@Autowired val questionGroupRepository: QuestionGroupRepository) : IntegrationTest() {
  val groupUuid: UUID = UUID.fromString("eb7b7324-f2a6-4902-91ef-709a8fab1f82")
  val questionSchemaUuid: UUID = UUID.fromString("5ca86a06-5472-4861-bd6a-a011780db49a")

  @Test
  fun `fetch group contents`() {
    val questionGroupEntities = questionGroupRepository.findByGroupGroupUuid(groupUuid)
    assertThat(questionGroupEntities).hasSize(14)

    val questionGroupEntity = questionGroupEntities!!.first()
    assertThat(questionGroupEntity.contentType).isEqualTo("question")
    assertThat(questionGroupEntity.contentUuid).isEqualTo(questionSchemaUuid)
  }

  @Test
  fun `list group summaries`() {
    val groupSummaries = questionGroupRepository.listGroups()
    assertThat(groupSummaries).hasSize(55)

    val groupInfo = groupSummaries.find { it.groupCode == "risk_to_others" }

    assertThat(groupInfo?.groupUuid).isEqualTo("946091d2-4038-4e2b-9283-83cc4876f6ed")
    assertThat(groupInfo?.heading).isEqualTo("Risk to others")
    assertThat(groupInfo?.contentCount).isEqualTo(32)
    assertThat(groupInfo?.groupCount).isEqualTo(0)
    assertThat(groupInfo?.questionCount).isEqualTo(32)
  }
}
