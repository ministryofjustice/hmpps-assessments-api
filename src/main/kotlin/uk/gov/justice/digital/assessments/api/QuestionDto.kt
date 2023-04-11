package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.api.answers.AnswerDto
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionEntity
import java.time.LocalDateTime
import java.util.UUID

data class QuestionDto(

  @Schema(description = "Question primary key", example = "1234")
  val questionId: Long?,

  @Schema(description = "Question UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val questionUuid: UUID?,

  @Schema(description = "Question Code", example = "RSR_23")
  val questionCode: String? = null,

  @Schema(description = "Question Start Date", example = "2020-01-02T16:00:00")
  val questionStart: LocalDateTime? = null,

  @Schema(description = "Question End Date", example = "2020-01-02T16:00:00")
  val questionEnd: LocalDateTime? = null,

  @Schema(description = "Answer Type", example = "Free Text")
  val answerType: String? = null,

  @Schema(description = "Question Text", example = "Some question text")
  val questionText: String? = null,

  @Schema(description = "Question Help Text", example = "Some question help text")
  val questionHelpText: String? = null,

  @Schema(description = "Reference Data Category")
  val referenceDataCategory: String? = null,

  @Schema(description = "List of Reference Data Answers")
  val answerDtos: Collection<AnswerDto>,

) {

  companion object {

    fun from(questions: Collection<QuestionEntity>?): List<QuestionDto> {
      return questions?.map { from(it) }?.toList().orEmpty()
    }

    fun from(questionEntity: QuestionEntity?): QuestionDto {
      return QuestionDto(
        questionEntity?.questionId,
        questionEntity?.questionUuid,
        questionEntity?.questionCode,
        questionEntity?.questionStartDate,
        questionEntity?.questionEndDate,
        questionEntity?.answerType,
        questionEntity?.questionText,
        questionEntity?.questionHelpText,
        questionEntity?.referenceDataCategory,
        AnswerDto.from(
          questionEntity?.answerEntities,
        ),
      )
    }
  }
}
