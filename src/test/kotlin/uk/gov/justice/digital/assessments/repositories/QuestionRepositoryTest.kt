package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID

class QuestionRepositoryTest(@Autowired val questionRepository: QuestionRepository) : IntegrationTest() {

  @Test
  fun `return Question by UUID`() {
    val questionSchemaUuid = UUID.fromString("2c88800c-1566-4019-9be1-1c1dfc67d4fb")
    val questionSchemaEntity = questionRepository.findByQuestionUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity).isNotNull
    assertThat(questionSchemaEntity?.questionUuid).isEqualTo(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionCode).isEqualTo("gender_identity")
    assertThat(questionSchemaEntity?.answerGroup).isNotNull
    assertThat(questionSchemaEntity?.answerEntities?.size).isEqualTo(5)
  }
}
