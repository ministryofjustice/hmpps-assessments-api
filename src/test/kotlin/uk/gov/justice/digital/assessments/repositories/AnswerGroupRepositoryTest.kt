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
    val answerSchemaGroupUuid = UUID.fromString("d03940ce-5f84-4ec1-af45-ab2957d09402")
    val answerSchemaGroup = answerGroupRepository.findByAnswerGroupUuid(answerSchemaGroupUuid)
    assertThat(answerSchemaGroup?.answerGroupUuid).isEqualTo(answerSchemaGroupUuid)
    assertThat(answerSchemaGroup?.answerSchemaGroupCode).isEqualTo("noproblems-someproblems-significantproblems")
    assertThat(answerSchemaGroup?.answerEntities?.size).isEqualTo(3)
  }
}
