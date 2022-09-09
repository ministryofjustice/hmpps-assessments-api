package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.AnswerGroupRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

class AnswerGroupRepositoryTest(@Autowired val answerGroupRepository: AnswerGroupRepository) : IntegrationTest() {

  @Test
  fun `return Answer Schema Group by UUID`() {
    val answerSchemaGroupUuid = UUID.fromString("8067ff6e-7400-4d1e-ae2a-87dee7e124ec")
    val answerSchemaGroup = answerGroupRepository.findByAnswerGroupUuid(answerSchemaGroupUuid)
    assertThat(answerSchemaGroup?.answerGroupUuid).isEqualTo(answerSchemaGroupUuid)
    assertThat(answerSchemaGroup?.answerGroupCode).isEqualTo("yes-noillcomebacklater")
    assertThat(answerSchemaGroup?.answerEntities?.size).isEqualTo(2)
  }
}
