package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.GroupContentQuestionDto
import uk.gov.justice.digital.assessments.api.QuestionGroupDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.repositories.GroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.*

@Service
class QuestionService(private val questionSchemaRepository: QuestionSchemaRepository,
                      private val groupRepository: GroupRepository) {

    fun getQuestionSchema(questionSchemaId: UUID): QuestionSchemaDto {
        val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaId)
                ?: throw EntityNotFoundException("Question Schema not found for id: $questionSchemaId")
        return QuestionSchemaDto.from(questionSchemaEntity)
    }

    fun getQuestionGroups(groupUuid:UUID): QuestionGroupDto {
        println(groupUuid.toString())

        return getQuestionGroups(groupUuid, null)
    }

    private fun getQuestionGroups(groupUuid:UUID, parentGroup: QuestionGroupEntity?): QuestionGroupDto {
        val group = groupRepository.findByGroupUuid(groupUuid) ?: throw EntityNotFoundException("Group not found: $groupUuid")
        val groupContents = group.contents
        if (groupContents.isEmpty()) throw EntityNotFoundException("Questions not found for Group: $groupUuid")

        val contents = groupContents.map {
            when(it.contentType) {
                "question" -> GroupContentQuestionDto.from(
                        questionSchemaRepository.findByQuestionSchemaUuid(it.contentUuid)!!,
                        it
                )
                "group" -> getQuestionGroups(it.contentUuid, it)
                else -> throw EntityNotFoundException("Bad group content type")
            }
        }

        return QuestionGroupDto.from(group, contents, parentGroup)
    }
}