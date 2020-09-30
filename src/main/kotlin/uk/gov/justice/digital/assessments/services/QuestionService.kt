package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.QuestionGroupDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionGroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.*

@Service
class QuestionService(private val questionSchemaRepository: QuestionSchemaRepository,
                        private val questionGroupRepository: QuestionGroupRepository) {

    fun getQuestionSchema(questionSchemaId: UUID): QuestionSchemaDto {
        val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaId)
                ?: throw EntityNotFoundException("Question Schema not found for id: $questionSchemaId")
        return QuestionSchemaDto.from(questionSchemaEntity)
    }

    fun getQuestionGroups(groupUuid:UUID): QuestionGroupDto {
        println(groupUuid.toString())
        val questionGroup = questionGroupRepository.findByGroupGroupUuid(groupUuid)
                ?: throw EntityNotFoundException("Group not found: $groupUuid")

        if (questionGroup.isEmpty()) throw EntityNotFoundException("Questions not found for Group: $groupUuid")
        return QuestionGroupDto.from(questionGroup)
    }
}