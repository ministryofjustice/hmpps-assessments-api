package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import uk.gov.justice.digital.assessments.controller.IntegrationTest
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionGroupRepository
import java.util.*

@SqlGroup(
        Sql(scripts = ["classpath:referenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
        Sql(scripts = ["classpath:referenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD))
class QuestionGroupRepositoryTest(@Autowired val questionGroupRepository: QuestionGroupRepository) : IntegrationTest() {

    @Test
    fun `fetch group contents`() {
        val groupUuid = UUID.fromString("e353f3df-113d-401c-a3c0-14239fc17cf9")
        val questionSchemaUuid = UUID.fromString("fd412ca8-d361-47ab-a189-7acb8ae0675b")

        val questionGroupEntities = questionGroupRepository.findByGroupGroupUuid(groupUuid)
        assertThat(questionGroupEntities).hasSize(1)

        val questionGroupEntity = questionGroupEntities!!.first()
        assertThat(questionGroupEntity.contentType).isEqualTo("question")
        assertThat(questionGroupEntity.contentUuid).isEqualTo(questionSchemaUuid)
    }

    @Test
    fun `list group summaries`() {
        val groupSummaries = questionGroupRepository.listGroups()
        assertThat(groupSummaries).hasSize(1)

        val groupInfo = groupSummaries.first()
        assertThat(groupInfo.heading).isEqualTo("Heading 1")
        assertThat(groupInfo.groupCount).isEqualTo(0)
        assertThat(groupInfo.questionCount).isEqualTo(1)
    }
}