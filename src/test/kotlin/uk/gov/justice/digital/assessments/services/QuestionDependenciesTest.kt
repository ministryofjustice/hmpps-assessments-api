package uk.gov.justice.digital.assessments.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.api.ConditionalsSchemaDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

class QuestionDependenciesTest(
  @Autowired
  private val questionDependencyService: QuestionDependencyService,
) : IntegrationTest() {
  val subjectUuid: UUID = UUID.fromString("e7f8205b-1f2a-4578-943c-154d2a6ee11e")
  val subjectQuestionCode: String = "cultural_religious_adjustment_details"

  val triggerUuid: UUID = UUID.fromString("5cefd241-cc51-4128-a343-cb7c438a9048")
  val questionWithNoDependenciesUuid: UUID = UUID.fromString("7877141f-19f6-4dab-a466-2f3c438e3ba6")
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
    assertThat(dependencies.hasDependency(questionWithNoDependenciesUuid)).isFalse
  }

  @Test
  fun `question triggers a dependency`() {
    assertThat(dependencies.triggersDependency(triggerUuid, "YES")).first()
      .isEqualTo(ConditionalsSchemaDto(subjectQuestionCode, true))
  }

  @Test
  fun `question triggers that has no trigger`() {
    assertThat(dependencies.triggersDependency(questionWithNoDependenciesUuid, "NO")).isEqualTo(null)
    assertThat(dependencies.triggersDependency(questionWithNoDependenciesUuid, "YES")).isEqualTo(null)
  }
}
