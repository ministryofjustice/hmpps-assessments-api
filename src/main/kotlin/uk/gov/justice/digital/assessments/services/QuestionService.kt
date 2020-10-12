package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.GroupSummaryDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.repositories.GroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionGroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.*

@Service
class QuestionService(private val questionSchemaRepository: QuestionSchemaRepository,
                      private val questionGroupRepository: QuestionGroupRepository,
                      private val groupRepository: GroupRepository) {

    fun getQuestionSchema(questionSchemaId: UUID): QuestionSchemaDto {
        val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaId)
                ?: throw EntityNotFoundException("Question Schema not found for id: $questionSchemaId")
        return QuestionSchemaDto.from(questionSchemaEntity)
    }

    fun getQuestionGroup(groupUuid:UUID): GroupWithContentsDto {
        return getQuestionGroup(groupUuid, null)
    }

    fun listGroups(): Collection<GroupSummaryDto> {
        return questionGroupRepository.listGroups().map { GroupSummaryDto.from(it) }
    }

    private fun getQuestionGroup(groupUuid:UUID, parentGroup: QuestionGroupEntity?): GroupWithContentsDto {
        val group = groupRepository.findByGroupUuid(groupUuid) ?: throw EntityNotFoundException("Group not found: $groupUuid")
        val groupContents = group.contents
        if (groupContents.isEmpty()) throw EntityNotFoundException("Questions not found for Group: $groupUuid")

        val contents = groupContents.map {
            when(it.contentType) {
                "question" -> GroupQuestionDto.from(
                        questionSchemaRepository.findByQuestionSchemaUuid(it.contentUuid)!!,
                        it
                )
                "group" -> getQuestionGroup(it.contentUuid, it)
                else -> throw EntityNotFoundException("Bad group content type")
            }
        }

        return GroupWithContentsDto.from(group, contents, parentGroup)
    }
}