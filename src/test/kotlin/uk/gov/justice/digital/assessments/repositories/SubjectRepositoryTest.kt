package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

@SqlGroup(
  Sql(scripts = ["classpath:subject/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
  Sql(scripts = ["classpath:subject/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)

class SubjectRepositoryTest(@Autowired val subjectRepository: SubjectRepository) : IntegrationTest() {
  val crn = "dummy-crn-1"
  val subject = "COURT"

  @Test
  fun `return Court Cases by CRN`() {
    val court = subjectRepository.findByCrn(crn)
    assertThat(court?.name).isEqualTo("John Smith")
  }
}
