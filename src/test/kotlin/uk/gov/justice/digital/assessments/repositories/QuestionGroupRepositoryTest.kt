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
    fun `return all Questions for Group`() {
        val questionGroupEntity = questionGroupRepository.findByGroupGroupUuid(UUID.fromString("22222222-2222-2222-2222-222222222222"))
        assertThat(questionGroupEntity).hasSize(1)
        assertThat(questionGroupEntity?.map{a -> a.questionSchema.questionSchemaUuid}).contains(UUID.fromString("11111111-1111-1111-1111-111111111111"))
    }
}