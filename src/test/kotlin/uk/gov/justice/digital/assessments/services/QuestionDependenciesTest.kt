package uk.gov.justice.digital.assessments.services

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionDependencyRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.*

@SqlGroup(
        Sql(scripts = ["classpath:referenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
        Sql(scripts = ["classpath:referenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD))
class QuestionDependenciesTest (
        @Autowired
        private val questionDependencyService: QuestionDependencyService
) : IntegrationTest() {
    val subjectUuid = UUID.fromString("11111111-1111-1111-1111-111111111113")
    val triggerUuid = UUID.fromString("11111111-1111-1111-1111-111111111112")
    lateinit var dependencies: QuestionDependencies

    @BeforeEach
    fun setup() {
        dependencies = questionDependencyService.dependencies()
    }

    @Test
    fun `question has a dependency`() {
        assertThat(dependencies.hasDependency(subjectUuid)).isTrue()
    }

    @Test
    fun `question has no dependency`() {
        assertThat(dependencies.hasDependency(triggerUuid)).isFalse()
    }

    @Test
    fun `question triggers a dependency`() {
        assertThat(dependencies.triggersDependency(triggerUuid, "Y")).isEqualTo(subjectUuid)
    }

    @Test
    fun `question triggers that has no trigger`() {
        assertThat(dependencies.triggersDependency(triggerUuid, "N")).isEqualTo(null)
        assertThat(dependencies.triggersDependency(subjectUuid, "Y")).isEqualTo(null)
    }
}