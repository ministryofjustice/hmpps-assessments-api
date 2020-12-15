package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.GroupSummaryDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AnswerSchemaRepository
import uk.gov.justice.digital.assessments.jpa.repositories.GroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionGroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.*

@Service
class QuestionService(private val questionSchemaRepository: QuestionSchemaRepository,
                      private val questionGroupRepository: QuestionGroupRepository,
                      private val groupRepository: GroupRepository,
                      private val answerSchemaRepository: AnswerSchemaRepository) {

    fun getQuestionSchema(questionSchemaId: UUID): QuestionSchemaDto {
        val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaId)
                ?: throw EntityNotFoundException("Question Schema not found for id: $questionSchemaId")
        return QuestionSchemaDto.from(questionSchemaEntity)
    }

    fun listGroups(): Collection<GroupSummaryDto> {
        return questionGroupRepository.listGroups().map { GroupSummaryDto.from(it) }
    }

    fun getQuestionGroup(groupCode: String): GroupWithContentsDto {
        return getQuestionGroupContents(findByGroupCode(groupCode), null)
    }

    fun getQuestionGroup(groupUuid: UUID): GroupWithContentsDto {
        return getQuestionGroupContents(findByGroupUuid(groupUuid), null)
    }

    private fun getQuestionGroupContents(group: GroupEntity, parentGroup: QuestionGroupEntity?): GroupWithContentsDto {
        val groupContents = group.contents.sortedBy { it.displayOrder }
        if (groupContents.isEmpty()) throw EntityNotFoundException("Questions not found for Group: ${group.groupUuid}")

        val contents = groupContents
                .map {
            when(it.contentType) {
                "question" -> GroupQuestionDto.from(
                        questionSchemaRepository.findByQuestionSchemaUuid(it.contentUuid)!!,
                        it
                )
                "group" -> getQuestionGroupContents(findByGroupUuid(it.contentUuid), it)
                else -> throw EntityNotFoundException("Bad group content type")
            }
        }

        return GroupWithContentsDto.from(group, contents, parentGroup)
    }

    fun getAllQuestions(): List<QuestionSchemaEntity> {
        return questionSchemaRepository.findAll()
    }

    fun getAllAnswers(): List<AnswerSchemaEntity> {
        return answerSchemaRepository.findAll()
    }

    private fun findByGroupCode(groupCode: String): GroupEntity {
        return groupRepository.findByGroupCode(groupCode)
                ?: findByGroupUuid(groupCode)
    }
    private fun findByGroupUuid(uuidStr: String): GroupEntity {
        val groupUuid = codeToUuid(uuidStr)
        return findByGroupUuid(groupUuid)
    }
    private fun findByGroupUuid(uuid: UUID): GroupEntity {
        return groupRepository.findByGroupUuid(uuid)
                ?: throw EntityNotFoundException("Group not found: $uuid")
    }
    private fun codeToUuid(code: String): UUID {
        try { return UUID.fromString(code) }
        catch(e: IllegalArgumentException) {
            throw EntityNotFoundException("Group not found: $code")
        }
    }
}