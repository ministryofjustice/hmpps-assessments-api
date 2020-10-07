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
            question,
            null
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
            question,
            null
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
            additionalQuestion,
            null
    )

    val groupWithNestedGroup = GroupEntity(
            1L,
            UUID.randomUUID(),
            "question-group-question",
            "Complex Group",
            "subheading",
            "help!",
            LocalDateTime.of(2019, 8, 1, 8, 0),
            null
    )

    val groupWithNestedGroupFirstQuestion = QuestionGroupEntity(
            1L,
            UUID.randomUUID(),
            groupWithNestedGroup,
            question.questionSchemaUuid,
            "question",
            "1",
            "mandatory",
            "none",
            question,
            null
    )
    val groupWithNestedGroupGroup = QuestionGroupEntity(
            2L,
            UUID.randomUUID(),
            groupWithNestedGroup,
            groupWithTwoQuestions.groupUuid,
            "nested group",
            "2",
            null,
            null,
            null,
            groupWithTwoQuestions
    )
    val groupWithNestedGroupSecondQuestion = QuestionGroupEntity(
            3L,
            UUID.randomUUID(),
            groupWithNestedGroup,
            additionalQuestion.questionSchemaUuid,
            "question",
            "3",
            "no",
            "lots",
            additionalQuestion,
            null
    )

    @Test
    fun `dto for group with one question`() {
        val dto = QuestionGroupDto.from(
                groupWithOneQuestion,
                listOf(
                    groupWithOneQuestionQuestion
                )
        )

        assertGroupDetails(dto, groupWithOneQuestion)

        assertQuestionContentsDetails(dto.contents[0], groupWithOneQuestionQuestion)
    }

    @Test
    fun `dto for two question group`() {
        val dto = QuestionGroupDto.from(
                groupWithTwoQuestions,
                listOf(
                        groupWithTwoQuestionsFirstQuestion,
                        groupWithTwoQuestionsSecondQuestion
                )
        )

        assertGroupDetails(dto, groupWithTwoQuestions)

        assertThat(dto.contents.size).isEqualTo(2)

        assertQuestionContentsDetails(dto.contents[0], groupWithTwoQuestionsFirstQuestion)
        assertQuestionContentsDetails(dto.contents[1], groupWithTwoQuestionsSecondQuestion)
    }

    @Test
    fun `dto for nested group`() {
        val dto = QuestionGroupDto.from(
                groupWithNestedGroup,
                listOf(
                        groupWithNestedGroupFirstQuestion,
                        groupWithNestedGroupGroup,
                        groupWithNestedGroupSecondQuestion
                )
        )

        assertGroupDetails(dto, groupWithNestedGroup)

        assertThat(dto.contents.size).isEqualTo(3)

        assertQuestionContentsDetails(dto.contents[0], groupWithNestedGroupFirstQuestion)
        assertGroupContentsDetails(dto.contents[1], groupWithNestedGroupGroup)
        assertQuestionContentsDetails(dto.contents[2], groupWithNestedGroupSecondQuestion)
    }

    companion object {
        fun assertGroupDetails(dto: QuestionGroupDto, group: GroupEntity) {
            assertThat(dto.groupId).isEqualTo(group.groupUuid)
            assertThat(dto.groupCode).isEqualTo(group.groupCode)
            assertThat(dto.title).isEqualTo(group.heading)
            assertThat(dto.subheading).isEqualTo(group.subheading)
            assertThat(dto.helpText).isEqualTo(group.helpText)
        }

        fun assertQuestionContentsDetails(content: GroupContentDto, entity: QuestionGroupEntity) {
            val qc = content as GroupContentQuestionDto

            assertThat(qc.displayOrder).isEqualTo(entity.displayOrder)
            assertThat(qc.mandatory).isEqualTo(entity.mandatory)
            assertThat(qc.validation).isEqualTo(entity.validation)

            val question = entity.question
            assertThat(question).isNotNull()

            assertThat(qc.questionId).isEqualTo(question?.questionSchemaUuid)
            assertThat(qc.questionCode).isEqualTo(question?.questionCode)
            assertThat(qc.questionText).isEqualTo(question?.questionText)
            assertThat(qc.helpText).isEqualTo(question?.questionHelpText)
            assertThat(qc.answerType).isEqualTo(question?.answerType)
        }

        fun assertGroupContentsDetails(content: GroupContentDto, entity: QuestionGroupEntity) {
            val gc = content as QuestionGroupDto

            assertThat(gc.displayOrder).isEqualTo(entity.displayOrder)
            assertThat(gc.mandatory).isEqualTo(entity.mandatory)
            assertThat(gc.validation).isEqualTo(entity.validation)

            val group = entity.nestedGroup
            assertThat(group).isNotNull()

            assertThat(gc.groupId).isEqualTo(group?.groupUuid)
            assertThat(gc.groupCode).isEqualTo(group?.groupCode)
            assertThat(gc.title).isEqualTo(group?.heading)
            assertThat(gc.subheading).isEqualTo(group?.subheading)
            assertThat(gc.helpText).isEqualTo(group?.helpText)
        }
    }
}