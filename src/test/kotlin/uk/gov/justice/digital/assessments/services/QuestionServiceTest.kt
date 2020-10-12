package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.GroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.*

@ExtendWith(MockKExtension::class)
@DisplayName("Question Schema Service Tests")
class QuestionServiceTest {

    private val questionSchemaRepository: QuestionSchemaRepository = mockk()
    private val groupRepository: GroupRepository = mockk()
    private val questionService = QuestionService(questionSchemaRepository, groupRepository)

    private val questionId = 1L
    private val questionUuid = UUID.randomUUID()

    @Test
    fun `get Question Schema by ID`() {
        every { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) } returns QuestionSchemaEntity(
                questionSchemaId = questionId, questionSchemaUuid = questionUuid, answerSchemaEntities = emptyList())

        val questionSchemaDto = questionService.getQuestionSchema(questionUuid)

        verify(exactly = 1) { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) }
        assertThat(questionSchemaDto).isEqualTo(QuestionSchemaDto(questionSchemaId = questionId, questionSchemaUuid = questionUuid, answerSchemas = emptyList()))
    }

    @Test
    fun `throw exception when Question Schema for ID`() {
        every { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) } returns null

        assertThrows<EntityNotFoundException> { questionService.getQuestionSchema(questionUuid) }
        verify(exactly = 1) { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) }
    }

    @Test
    fun `get group contents`() {
        val groupUuid = UUID.randomUUID()
        val contents = mutableListOf<QuestionGroupEntity>()
        val group = GroupEntity(
                groupId = 1,
                groupUuid = groupUuid,
                groupCode = "Test Group",
                contents = contents
        )
        val question = QuestionSchemaEntity(
                questionSchemaId = questionId,
                questionSchemaUuid = questionUuid,
                answerSchemaEntities = emptyList()
        )
        val questionGroup = QuestionGroupEntity(
                questionGroupId = 99,
                group = group,
                contentUuid = questionUuid,
                contentType = "question",
                displayOrder = "1",
                question = question,
                nestedGroup = null
        )
        contents.add(questionGroup)

        every { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) } returns question
        every { groupRepository.findByGroupUuid(groupUuid) } returns group

        val groupQuestions = questionService.getQuestionGroup(groupUuid)

        assertThat(groupQuestions?.groupId).isEqualTo(groupUuid)

        val groupContents = groupQuestions?.contents
        assertThat(groupContents).hasSize(1)
        val questionRef = groupContents?.get(0) as GroupQuestionDto
        assertThat(questionRef.questionId).isEqualTo(questionUuid)
    }
}
