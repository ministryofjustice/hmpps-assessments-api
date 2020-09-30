package uk.gov.justice.digital.assessments.api

import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import java.time.LocalDateTime
import java.util.*

data class QuestionGroupDto (

    val questionGroupId : Long,
    val questionGroupUuid: UUID,
    val questionRefs : List<GetQuestionsForGroupDto>,
    val group : GroupDto,
    val groupName : String? = null,
    val parentGroupUUID: UUID? = null,
    val groupStart : LocalDateTime? = null,
    val groupEnd : LocalDateTime? = null

) {
    companion object {

        fun from(questionGroupEntities: Collection<QuestionGroupEntity>): QuestionGroupDto {
            val questionGroup = questionGroupEntities.elementAt(0)
            val questionRefs = questionGroupEntities.map {
                        GetQuestionsForGroupDto.from(it.questionSchema, questionGroup )}
            return QuestionGroupDto(
                    questionGroupId = questionGroup.questionGroupId,
                    questionGroupUuid = questionGroup.uuid,
                    questionRefs = questionRefs,
                    group = GroupDto.from(questionGroup.group),
                    groupName = questionGroup.groupName,
                    parentGroupUUID = questionGroup.parentGroupId,
            )
        }
    }

    data class GetQuestionsForGroupDto(
            val questionSchemaId: Long?,
            val questionSchemaUuid: UUID?,
            val questionCode: String? = null,
            val oasysQuestionCode: String? = null,
            val questionStart: LocalDateTime? = null,
            val questionEnd: LocalDateTime? = null,
            val answerSchemas: Collection<AnswerSchemaDto>? = null,
            val answerType: String? = null,
            val questionText: String? = null,
            val questionHelpText: String? = null,
            val displayOrder : String? = null,
            val mandatory : String? = null,
            val validation : String? = null,
    ){ 
        companion object{
        
        fun from(questionSchemaEntity: QuestionSchemaEntity, questionGroupEntity: QuestionGroupEntity): GetQuestionsForGroupDto{
            return GetQuestionsForGroupDto(
                questionSchemaId = questionSchemaEntity.questionSchemaId,
                questionSchemaUuid = questionSchemaEntity.questionSchemaUuid,
                questionCode = questionSchemaEntity.questionCode,
                oasysQuestionCode = questionSchemaEntity.oasysQuestionCode,
                questionStart = questionSchemaEntity.questionStartDate,
                questionEnd = questionSchemaEntity.questionEndDate,
                answerSchemas = AnswerSchemaDto.from(questionSchemaEntity.answerSchemaEntities),
                answerType = questionSchemaEntity.answerType,
                questionText = questionSchemaEntity.questionText,
                questionHelpText = questionSchemaEntity.questionHelpText,
                displayOrder = questionGroupEntity.displayOrder,
                mandatory = questionGroupEntity.mandatory,
                validation = questionGroupEntity.validation
            ) }
        }
    }
}