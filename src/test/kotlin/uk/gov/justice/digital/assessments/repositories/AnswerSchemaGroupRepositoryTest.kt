package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.AnswerSchemaGroupRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

class AnswerSchemaGroupRepositoryTest(@Autowired val answerSchemaGroupRepository: AnswerSchemaGroupRepository) : IntegrationTest() {

  @Test
  fun `return Answer Schema Group by UUID`() {
    val answerSchemaGroupUuid = UUID.fromString("d03940ce-5f84-4ec1-af45-ab2957d09402")
    val answerSchemaGroup = answerSchemaGroupRepository.findByAnswerSchemaGroupUuid(answerSchemaGroupUuid)
    assertThat(answerSchemaGroup?.answerSchemaGroupUuid).isEqualTo(answerSchemaGroupUuid)
    assertThat(answerSchemaGroup?.answerSchemaGroupCode).isEqualTo("noproblems-someproblems-significantproblems")
    assertThat(answerSchemaGroup?.answerSchemaEntities?.size).isEqualTo(3)
  }
}
