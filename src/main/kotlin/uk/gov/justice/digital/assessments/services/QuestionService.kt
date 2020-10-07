package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.QuestionGroupDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionGroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.GroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.*

@Service
class QuestionService(private val questionSchemaRepository: QuestionSchemaRepository,
                      private val groupRepository: GroupRepository,
                      private val questionGroupRepository: QuestionGroupRepository, ) {

    fun getQuestionSchema(questionSchemaId: UUID): QuestionSchemaDto {
        val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaId)
                ?: throw EntityNotFoundException("Question Schema not found for id: $questionSchemaId")
        return QuestionSchemaDto.from(questionSchemaEntity)
    }

    fun getQuestionGroups(groupUuid:UUID): QuestionGroupDto {
        println(groupUuid.toString())

        val group = groupRepository.findByGroupUuid(groupUuid) ?: throw EntityNotFoundException("Group not found: $groupUuid")
        val groupContents = questionGroupRepository.findByGroupGroupUuid(groupUuid)
                ?: throw EntityNotFoundException("Group not found: $groupUuid")
        val gc = group.contents
        if (gc.isEmpty()) throw EntityNotFoundException("Questions not found for Group: $groupUuid")
        gc.forEach {
            when(it.contentType) {
                "question" -> it.question = questionSchemaRepository.findByQuestionSchemaUuid(it.contentUuid)
                "group" -> it.nestedGroup = groupRepository.findByGroupUuid(it.contentUuid)
            }
        }

        return QuestionGroupDto.from(group, groupContents)
    }
}