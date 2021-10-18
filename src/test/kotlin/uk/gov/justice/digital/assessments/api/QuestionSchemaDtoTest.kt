package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.ReferenceDataTargetMappingEntity
import java.time.LocalDateTime
import java.util.UUID

class QuestionSchemaDtoTest {

  @Test
  fun `builds valid Question Schema DTO`() {

    val referenceDataTargetMappingEntity = ReferenceDataTargetMappingEntity(
      id = 2L,
      questionSchema = QuestionSchemaEntity(questionSchemaId = 1L, questionCode = "question_code_one"),
      parentQuestionSchema = QuestionSchemaEntity(questionSchemaId = 2L, questionCode = "question_code_two"),
      isRequired = false
    )

    val questionSchemaEntity = QuestionSchemaEntity(
      1L,
      UUID.randomUUID(),
      "SupervisionId",
      emptyList(),
      LocalDateTime.of(2019, 8, 1, 8, 0),
      LocalDateTime.of(2020, 8, 1, 8, 0),
      "Freetext",
      "Question text",
      "Question help text",
      "TEST_REF_DATA_CAT",
      listOf(referenceDataTargetMappingEntity),
    )

    val questionSchemaDto = QuestionSchemaDto.from(questionSchemaEntity)

    assertThat(questionSchemaDto.questionSchemaId).isEqualTo(questionSchemaEntity.questionSchemaId)
    assertThat(questionSchemaDto.questionSchemaUuid).isEqualTo(questionSchemaEntity.questionSchemaUuid)
    assertThat(questionSchemaDto.questionCode).isEqualTo(questionSchemaEntity.questionCode)
    assertThat(questionSchemaDto.questionStart).isEqualTo(questionSchemaEntity.questionStartDate)
    assertThat(questionSchemaDto.questionEnd).isEqualTo(questionSchemaEntity.questionEndDate)
    assertThat(questionSchemaDto.answerType).isEqualTo(questionSchemaEntity.answerType)
    assertThat(questionSchemaDto.questionText).isEqualTo(questionSchemaEntity.questionText)
    assertThat(questionSchemaDto.questionHelpText).isEqualTo(questionSchemaEntity.questionHelpText)
    assertThat(questionSchemaDto.referenceDataCategory).isEqualTo(questionSchemaEntity.referenceDataCategory)
    assertThat(questionSchemaDto.referenceDataTargets.first().questionSchemaUuid).isEqualTo(
      referenceDataTargetMappingEntity.parentQuestionSchema.questionSchemaUuid
    )
  }
}
