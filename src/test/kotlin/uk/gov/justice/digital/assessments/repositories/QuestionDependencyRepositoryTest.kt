package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionDependencyRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

@SqlGroup(
  Sql(scripts = ["classpath:referenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
  Sql(scripts = ["classpath:referenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
class QuestionDependencyRepositoryTest(
  @Autowired
  val questionDependencyRepository: QuestionDependencyRepository
) : IntegrationTest() {
  @Test
  fun `fetch all dependencies`() {
    val dependencies = questionDependencyRepository.findAll()

    assertThat(dependencies.size).isEqualTo(6)
  }
}
