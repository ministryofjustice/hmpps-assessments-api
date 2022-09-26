package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionEntity
import java.time.LocalDateTime
import java.util.UUID

class QuestionDtoTest {

  @Test
  fun `builds valid Question Schema DTO`() {

    val questionEntity = QuestionEntity(
      1L,
      UUID.randomUUID(),
      "SupervisionId",
      LocalDateTime.of(2019, 8, 1, 8, 0),
      LocalDateTime.of(2020, 8, 1, 8, 0),
      "Freetext",
      "Question text",
      "Question help text",
      "TEST_REF_DATA_CAT",
    )

    val questionDto = QuestionDto.from(questionEntity)

    assertThat(questionDto.questionId).isEqualTo(questionEntity.questionId)
    assertThat(questionDto.questionUuid).isEqualTo(questionEntity.questionUuid)
    assertThat(questionDto.questionCode).isEqualTo(questionEntity.questionCode)
    assertThat(questionDto.questionStart).isEqualTo(questionEntity.questionStartDate)
    assertThat(questionDto.questionEnd).isEqualTo(questionEntity.questionEndDate)
    assertThat(questionDto.answerType).isEqualTo(questionEntity.answerType)
    assertThat(questionDto.questionText).isEqualTo(questionEntity.questionText)
    assertThat(questionDto.questionHelpText).isEqualTo(questionEntity.questionHelpText)
    assertThat(questionDto.referenceDataCategory).isEqualTo(questionEntity.referenceDataCategory)
  }
}
