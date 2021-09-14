package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionGroupRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

@SqlGroup(
  Sql(scripts = ["classpath:referenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
  Sql(scripts = ["classpath:referenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
class QuestionGroupRepositoryTest(@Autowired val questionGroupRepository: QuestionGroupRepository) : IntegrationTest() {
  val groupUuid: UUID = UUID.fromString("e353f3df-113d-401c-a3c0-14239fc17cf9")
  val questionSchemaUuid: UUID = UUID.fromString("fd412ca8-d361-47ab-a189-7acb8ae0675b")

  @Test
  fun `fetch group contents`() {
    val questionGroupEntities = questionGroupRepository.findByGroupGroupUuid(groupUuid)
    assertThat(questionGroupEntities).hasSize(3)

    val questionGroupEntity = questionGroupEntities!!.first()
    assertThat(questionGroupEntity.contentType).isEqualTo("question")
    assertThat(questionGroupEntity.contentUuid).isEqualTo(questionSchemaUuid)
  }

  @Test
  fun `list group summaries`() {
    val groupSummaries = questionGroupRepository.listGroups()
    assertThat(groupSummaries).hasSize(37)

    val groupInfo = groupSummaries.find { it.groupCode == "Group code" }

    assertThat(groupInfo?.groupUuid).isEqualTo(groupUuid.toString())
    assertThat(groupInfo?.heading).isEqualTo("Heading 1")
    assertThat(groupInfo?.contentCount).isEqualTo(3)
    assertThat(groupInfo?.groupCount).isEqualTo(0)
    assertThat(groupInfo?.questionCount).isEqualTo(2)
  }
}
