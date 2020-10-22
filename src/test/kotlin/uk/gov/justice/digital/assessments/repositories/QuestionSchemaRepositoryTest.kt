package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import uk.gov.justice.digital.assessments.controller.IntegrationTest
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionSchemaRepository
import java.util.*

@SqlGroup(
        Sql(scripts = ["classpath:referenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
        Sql(scripts = ["classpath:referenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD))
class QuestionSchemaRepositoryTest(@Autowired val questionSchemaRepository: QuestionSchemaRepository) : IntegrationTest() {

    @Test
    fun `return Question by UUID`() {
        val questionSchemaUuid = UUID.fromString("fd412ca8-d361-47ab-a189-7acb8ae0675b")
        val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaUuid)
        assertThat(questionSchemaEntity?.questionSchemaUuid).isEqualTo(questionSchemaUuid)
        assertThat(questionSchemaEntity?.answerSchemaGroup).isNotNull
        assertThat(questionSchemaEntity?.answerSchemaEntities?.size).isEqualTo(2)

    }
}