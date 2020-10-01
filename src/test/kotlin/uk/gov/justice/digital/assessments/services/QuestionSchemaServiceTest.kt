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
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException

@ExtendWith(MockKExtension::class)
@DisplayName("Question Schema Service Tests")
class QuestionSchemaServiceTest {

    private val questionSchemaRepository: QuestionSchemaRepository = mockk()
    private val questionSchemasService = QuestionSchemaService(questionSchemaRepository)

    private val questionSchemaId = 1234L

    @Test
    fun `should return Question Schema by ID`() {
        every { questionSchemaRepository.findByQuestionSchemaId(questionSchemaId) } returns QuestionSchemaEntity(questionSchemaId)

        val questionSchemaDto = questionSchemasService.getQuestionSchema(questionSchemaId)

        verify(exactly = 1) { questionSchemaRepository.findByQuestionSchemaId(questionSchemaId) }
        assertThat(questionSchemaDto).isEqualTo(QuestionSchemaDto(questionSchemaId))
    }

    @Test
    fun `should throw exception when Question Schema does not exist with ID`() {
        every { questionSchemaRepository.findByQuestionSchemaId(questionSchemaId) } returns null

        assertThrows<EntityNotFoundException> { questionSchemasService.getQuestionSchema(questionSchemaId) }
        verify(exactly = 1) { questionSchemaRepository.findByQuestionSchemaId(questionSchemaId) }
    }
}
