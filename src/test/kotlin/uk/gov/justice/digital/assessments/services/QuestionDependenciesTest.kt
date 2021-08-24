package uk.gov.justice.digital.assessments.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import uk.gov.justice.digital.assessments.api.ConditionalsSchemaDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

@SqlGroup(
  Sql(scripts = ["classpath:referenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
  Sql(scripts = ["classpath:referenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
class QuestionDependenciesTest(
  @Autowired
  private val questionDependencyService: QuestionDependencyService
) : IntegrationTest() {
  val subjectUuid: UUID = UUID.fromString("11111111-1111-1111-1111-111111111113")
  val subjectQuestionCode: String = "RSR_05"

  val triggerUuid: UUID = UUID.fromString("11111111-1111-1111-1111-111111111112")
  lateinit var dependencies: QuestionDependencies

  @BeforeEach
  fun setup() {
    dependencies = questionDependencyService.dependencies()
  }

  @Test
  fun `question has a dependency`() {
    assertThat(dependencies.hasDependency(subjectUuid)).isTrue
  }

  @Test
  fun `question has no dependency`() {
    assertThat(dependencies.hasDependency(triggerUuid)).isFalse
  }

  @Test
  fun `question triggers a dependency`() {
    assertThat(dependencies.triggersDependency(triggerUuid, "Y")).first().isEqualTo(ConditionalsSchemaDto(subjectQuestionCode, true))
  }

  @Test
  fun `question triggers multiple dependencies`() {
    val multipleDependencies = mutableSetOf<ConditionalsSchemaDto>()
    multipleDependencies.add(ConditionalsSchemaDto("RSR_09", true))
    multipleDependencies.add(ConditionalsSchemaDto("RSR_11", false))

    assertThat(dependencies.triggersDependency(UUID.fromString("11111111-1111-1111-1111-111111111116"), "Y")).isEqualTo(multipleDependencies)
  }

  @Test
  fun `question triggers that has no trigger`() {
    assertThat(dependencies.triggersDependency(triggerUuid, "N")).isEqualTo(null)
    assertThat(dependencies.triggersDependency(subjectUuid, "Y")).isEqualTo(null)
  }
}
