package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import java.time.LocalDateTime
import java.util.UUID

@DisplayName("Assessment DTO Tests")
class QuestionSchemaDtoTest {

  @Test
  fun `builds valid Question Schema DTO`() {

    val questionSchemaEntity = QuestionSchemaEntity(
      1L,
      UUID.randomUUID(),
      "SupervisionId",
      "RSR_25",
      null,
      LocalDateTime.of(2019, 8, 1, 8, 0),
      LocalDateTime.of(2020, 8, 1, 8, 0),
      "Freetext",
      "Question text",
      "Question help text"
    )

    val questionSchemaDto = QuestionSchemaDto.from(questionSchemaEntity)

    assertThat(questionSchemaDto.questionSchemaId).isEqualTo(questionSchemaEntity.questionSchemaId)
    assertThat(questionSchemaDto.questionSchemaUuid).isEqualTo(questionSchemaEntity.questionSchemaUuid)
    assertThat(questionSchemaDto.questionCode).isEqualTo(questionSchemaEntity.questionCode)
    assertThat(questionSchemaDto.oasysQuestionCode).isEqualTo(questionSchemaEntity.oasysQuestionCode)
    assertThat(questionSchemaDto.questionStart).isEqualTo(questionSchemaEntity.questionStartDate)
    assertThat(questionSchemaDto.questionEnd).isEqualTo(questionSchemaEntity.questionEndDate)
    assertThat(questionSchemaDto.answerType).isEqualTo(questionSchemaEntity.answerType)
    assertThat(questionSchemaDto.questionText).isEqualTo(questionSchemaEntity.questionText)
    assertThat(questionSchemaDto.questionHelpText).isEqualTo(questionSchemaEntity.questionHelpText)
  }
}
