package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionDependencyRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

class QuestionDependencyRepositoryTest(
  @Autowired
  val questionDependencyRepository: QuestionDependencyRepository,
) : IntegrationTest() {
  @Test
  fun `fetch all dependencies`() {
    val dependencies = questionDependencyRepository.findAll()

    assertThat(dependencies.size).isEqualTo(11)
  }
}
