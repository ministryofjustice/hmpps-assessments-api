package uk.gov.justice.digital.assessments.api

import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import java.time.LocalDateTime
import java.util.*


data class AnswerSchemaDto (

        val answerSchemaId: Long,
        val answerSchemaUuid: UUID,
        val answerSchemaCode: String? = null,
        val questionSchema: QuestionSchemaDto? = null,
        val answerStart: LocalDateTime? = null,
        val answerEnd: LocalDateTime? = null,
        val value: String? = null,
        val text: String? = null

) {
    companion object{

        fun from(answerSchemaEntities: Collection<AnswerSchemaEntity>?): Collection<AnswerSchemaDto>{
            if (answerSchemaEntities.isNullOrEmpty()) return emptyList()
            return answerSchemaEntities.map { from(it) }.toList()
        }

        fun from(answerSchemaEntity: AnswerSchemaEntity): AnswerSchemaDto{
            return AnswerSchemaDto(
                    answerSchemaEntity.answerSchemaId,
                    answerSchemaEntity.answerSchemaUuid,
                    answerSchemaEntity.answerSchemaCode,
                    QuestionSchemaDto.from(answerSchemaEntity.questionSchema),
                    answerSchemaEntity.answerStart,
                    answerSchemaEntity.answerEnd,
                    answerSchemaEntity.value,
                    answerSchemaEntity.text
            )
        }
    }
}