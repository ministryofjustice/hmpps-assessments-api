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
    val questionSchemaUuid = UUID.fromString("574618c3-27f4-4dd2-94bb-6de74126ff22")
    val questionSchemaEntity = questionRepository.findByQuestionUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity).isNotNull
    assertThat(questionSchemaEntity?.questionUuid).isEqualTo(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionCode).isEqualTo("binge_drinking")
    assertThat(questionSchemaEntity?.answerGroup).isNotNull
    assertThat(questionSchemaEntity?.answerEntities?.size).isEqualTo(3)
  }
}
