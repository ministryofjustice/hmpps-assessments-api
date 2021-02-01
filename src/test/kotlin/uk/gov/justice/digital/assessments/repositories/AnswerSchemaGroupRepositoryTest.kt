package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import uk.gov.justice.digital.assessments.jpa.repositories.AnswerSchemaGroupRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

@SqlGroup(
  Sql(scripts = ["classpath:referenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
  Sql(scripts = ["classpath:referenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
class AnswerSchemaGroupRepositoryTest(@Autowired val answerSchemaGroupRepository: AnswerSchemaGroupRepository) : IntegrationTest() {

  @Test
  fun `return Answer Schema Group by UUID`() {
    val answerSchemaGroupUuid = UUID.fromString("f756f79d-dfad-49f9-a1b9-964a41cf660d")
    val answerSchemaGroup = answerSchemaGroupRepository.findByAnswerSchemaGroupUuid(answerSchemaGroupUuid)
    assertThat(answerSchemaGroup?.answerSchemaGroupUuid).isEqualTo(answerSchemaGroupUuid)
    assertThat(answerSchemaGroup?.answerSchemaGroupCode).isEqualTo("TEST")
    assertThat(answerSchemaGroup?.answerSchemaEntities?.size).isEqualTo(2)
  }
}
