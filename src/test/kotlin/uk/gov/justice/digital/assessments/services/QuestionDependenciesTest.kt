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
  private val questionDependencyService: QuestionDependencyService
) : IntegrationTest() {
  val subjectUuid: UUID = UUID.fromString("3662710d-ce3e-4e45-bce3-caa4155872aa")
  val subjectQuestionCode: String = "current_sexual_offence"

  val triggerUuid: UUID = UUID.fromString("58d3efd1-65a1-439b-952f-b2826ffa5e71")
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
  fun `question triggers multiple dependencies`() {
    val multipleDependencies = mutableSetOf<ConditionalsSchemaDto>()
    multipleDependencies.add(ConditionalsSchemaDto("current_sexual_offence", true))
    multipleDependencies.add(ConditionalsSchemaDto("most_recent_sexual_offence_date", true))
    multipleDependencies.add(ConditionalsSchemaDto("total_sexual_offences_adult", true))
    multipleDependencies.add(ConditionalsSchemaDto("total_sexual_offences_child", true))
    multipleDependencies.add(ConditionalsSchemaDto("total_sexual_offences_child_image", true))
    multipleDependencies.add(ConditionalsSchemaDto("total_non_contact_sexual_offences", true))

    assertThat(dependencies.triggersDependency(triggerUuid, "YES")).isEqualTo(
      multipleDependencies
    )
  }

  @Test
  fun `question triggers that has no trigger`() {
    assertThat(dependencies.triggersDependency(questionWithNoDependenciesUuid, "NO")).isEqualTo(null)
    assertThat(dependencies.triggersDependency(questionWithNoDependenciesUuid, "YES")).isEqualTo(null)
  }
}
