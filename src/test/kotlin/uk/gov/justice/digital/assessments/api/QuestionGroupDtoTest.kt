package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.Test
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import java.time.LocalDateTime
import java.util.*

@DisplayName("Question Group DTO Tests")
class QuestionGroupDtoTest {
    val question = QuestionSchemaEntity(
            1L,
            UUID.randomUUID(),
            "SupervisionId",
            "RSR_25",
            LocalDateTime.of(2019, 8, 1, 8, 0),
            null,
            "Freetext",
            "Question text",
            "Question help text",
            emptyList()
    )
    val additionalQuestion = QuestionSchemaEntity(
            1L,
            UUID.randomUUID(),
            "AssessmentId",
            "RSR_40",
            LocalDateTime.of(2019, 8, 1, 8, 0),
            null,
            "Freetext",
            "Question text",
            "Question help text",
            emptyList()
    )

    val groupWithOneQuestion = GroupEntity(
            1L,
            UUID.randomUUID(),
            "simple-group",
            "Simple Group",
            "subheading",
            "help!",
            LocalDateTime.of(2019, 8, 1, 8, 0),
            null
    )

    val groupWithOneQuestionQuestion = QuestionGroupEntity(
            1L,
            UUID.randomUUID(),
            groupWithOneQuestion,
            question.questionSchemaUuid,
            "question",
            "1",
            "mandatory",
            "none",
            question
    )

    val groupWithTwoQuestions = GroupEntity(
            1L,
            UUID.randomUUID(),
            "two-question-group",
            "Two Question Group",
            "subheading",
            "help!",
            LocalDateTime.of(2019, 8, 1, 8, 0),
            null
    )

    val groupWithTwoQuestionsFirstQuestion = QuestionGroupEntity(
            1L,
            UUID.randomUUID(),
            groupWithTwoQuestions,
            question.questionSchemaUuid,
            "question",
            "1",
            "mandatory",
            "none",
            question
    )
    val groupWithTwoQuestionsSecondQuestion = QuestionGroupEntity(
            1L,
            UUID.randomUUID(),
            groupWithTwoQuestions,
            additionalQuestion.questionSchemaUuid,
            "question",
            "2",
            "no",
            "lots",
            additionalQuestion
    )

    @Test
    fun `dto for group with one question`() {
        val entities = listOf(
                groupWithOneQuestionQuestion
        )

        val dto = QuestionGroupDto.from(entities)

        assertGroupDetails(dto, groupWithOneQuestion)

        assertThat(dto.contents.size).isEqualTo(1)

        val contentQuestion = dto.contents[0]
        assertThat(contentQuestion.questionId).isEqualTo(question.questionSchemaUuid)
        assertThat(contentQuestion.questionCode).isEqualTo(question.questionCode)
        assertThat(contentQuestion.questionText).isEqualTo(question.questionText)
        assertThat(contentQuestion.helpText).isEqualTo(question.questionHelpText)
        assertThat(contentQuestion.answerType).isEqualTo(question.answerType)

        assertThat(contentQuestion.displayOrder).isEqualTo(groupWithOneQuestionQuestion.displayOrder)
        assertThat(contentQuestion.mandatory).isEqualTo(groupWithOneQuestionQuestion.mandatory)
        assertThat(contentQuestion.validation).isEqualTo(groupWithOneQuestionQuestion.validation)
    }

    @Test
    fun `dto for two question group`() {
        val entities = listOf(
                groupWithTwoQuestionsFirstQuestion,
                groupWithTwoQuestionsSecondQuestion
        )

        val dto = QuestionGroupDto.from(entities)

        assertGroupDetails(dto, groupWithTwoQuestions)

        assertThat(dto.contents.size).isEqualTo(2)

        val firstQuestion = dto.contents[0]
        assertThat(firstQuestion.questionId).isEqualTo(question.questionSchemaUuid)
        assertThat(firstQuestion.questionCode).isEqualTo(question.questionCode)
        assertThat(firstQuestion.questionText).isEqualTo(question.questionText)
        assertThat(firstQuestion.helpText).isEqualTo(question.questionHelpText)
        assertThat(firstQuestion.answerType).isEqualTo(question.answerType)

        assertThat(firstQuestion.displayOrder).isEqualTo(groupWithTwoQuestionsFirstQuestion.displayOrder)
        assertThat(firstQuestion.mandatory).isEqualTo(groupWithTwoQuestionsFirstQuestion.mandatory)
        assertThat(firstQuestion.validation).isEqualTo(groupWithTwoQuestionsFirstQuestion.validation)

        val secondQuestion = dto.contents[1]
        assertThat(secondQuestion.questionId).isEqualTo(additionalQuestion.questionSchemaUuid)
        assertThat(secondQuestion.questionCode).isEqualTo(additionalQuestion.questionCode)
        assertThat(secondQuestion.questionText).isEqualTo(additionalQuestion.questionText)
        assertThat(secondQuestion.helpText).isEqualTo(additionalQuestion.questionHelpText)
        assertThat(secondQuestion.answerType).isEqualTo(additionalQuestion.answerType)

        assertThat(secondQuestion.displayOrder).isEqualTo(groupWithTwoQuestionsSecondQuestion.displayOrder)
        assertThat(secondQuestion.mandatory).isEqualTo(groupWithTwoQuestionsSecondQuestion.mandatory)
        assertThat(secondQuestion.validation).isEqualTo(groupWithTwoQuestionsSecondQuestion.validation)
    }

    companion object {
        fun assertGroupDetails(dto: QuestionGroupDto, group: GroupEntity) {
            assertThat(dto.groupId).isEqualTo(group.groupUuid)
            assertThat(dto.groupCode).isEqualTo(group.groupCode)
            assertThat(dto.title).isEqualTo(group.heading)
            assertThat(dto.subheading).isEqualTo(group.subheading)
            assertThat(dto.helpText).isEqualTo(group.helpText)
        }
    }
}