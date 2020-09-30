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
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionGroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.*

@ExtendWith(MockKExtension::class)
@DisplayName("Question Schema Service Tests")
class QuestionServiceTest {

    private val questionSchemaRepository: QuestionSchemaRepository = mockk()
    private val questionGroupRepository: QuestionGroupRepository = mockk()
    private val questionService = QuestionService(questionSchemaRepository, questionGroupRepository)

    private val questionId = 1L
    private val uuid = UUID.randomUUID()

    @Test
    fun `should return Question Schema by ID`() {
        every { questionSchemaRepository.findByQuestionSchemaUuid(uuid) } returns QuestionSchemaEntity(
                questionSchemaId = questionId, questionSchemaUuid = uuid, answerSchemaEntities = emptyList())

        val questionSchemaDto = questionService.getQuestionSchema(uuid)

        verify(exactly = 1) { questionSchemaRepository.findByQuestionSchemaUuid(uuid) }
        assertThat(questionSchemaDto).isEqualTo(QuestionSchemaDto(questionSchemaId = questionId, questionSchemaUuid = uuid, answerSchemas = emptyList()))
    }

    @Test
    fun `should throw exception when Question Schema does not exist with ID`() {
        every { questionSchemaRepository.findByQuestionSchemaUuid(uuid) } returns null

        assertThrows<EntityNotFoundException> { questionService.getQuestionSchema(uuid) }
        verify(exactly = 1) { questionSchemaRepository.findByQuestionSchemaUuid(uuid) }
    }


//    @Test
//    fun `should return Questions for group`() {
//        val groupId = UUID.randomUUID()
//        val validQuestionGroup = QuestionGroupEntity(questionGroupId = UUID.randomUUID(), questionSchema = QuestionSchemaEntity(UUID.randomUUID()), )
//        every { questionGroupRepository.findByGroup_GroupId(groupId) } returns )
//
//        val questionSchemaDto = questionService.getQuestionSchema(questionSchemaId)
//
//        verify(exactly = 1) { questionSchemaRepository.findByQuestionSchemaId(questionSchemaId) }
//        assertThat(questionSchemaDto).isEqualTo(QuestionSchemaDto(questionSchemaId))
//    }

//    @Test
//    fun `should throw exception when Question Schema does not exist with ID`() {
//        every { questionSchemaRepository.findByQuestionSchemaId(questionSchemaId) } returns null
//
//        assertThrows<EntityNotFoundException> { questionService.getQuestionSchema(questionSchemaId) }
//        verify(exactly = 1) { questionSchemaRepository.findByQuestionSchemaId(questionSchemaId) }
//    }
}
