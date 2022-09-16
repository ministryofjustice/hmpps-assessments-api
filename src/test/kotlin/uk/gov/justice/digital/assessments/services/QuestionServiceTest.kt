package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.QuestionDto
import uk.gov.justice.digital.assessments.api.groups.GroupQuestionDto
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupSummaryEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionDependencyEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.GroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionGroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Question Schema Service Tests")
class QuestionServiceTest {
  private val questionRepository: QuestionRepository = mockk()
  private val questionGroupRepository: QuestionGroupRepository = mockk()
  private val groupRepository: GroupRepository = mockk()
  private val dependencyService: QuestionDependencyService = mockk()
  private val questionService = QuestionService(
    questionRepository,
    questionGroupRepository,
    groupRepository,
    dependencyService
  )

  private val questionId = 1L
  private val questionUuid = UUID.randomUUID()

  private val groupUuid = UUID.randomUUID()
  private val contents = mutableListOf<QuestionGroupEntity>()
  private val group = GroupEntity(
    groupId = 1,
    groupUuid = groupUuid,
    groupCode = "Test Group",
    contents = contents
  )
  private val question = QuestionEntity(
    questionId = questionId,
    questionUuid = questionUuid,
    questionCode = "question_code"
  )

  private val groupWithCheckboxUuid = UUID.randomUUID()
  private val groupWithCheckboxContents = mutableListOf<QuestionGroupEntity>()
  private val groupWithCheckbox = GroupEntity(
    groupId = 10,
    groupUuid = groupWithCheckboxUuid,
    groupCode = "group with checkboxes",
    contents = groupWithCheckboxContents
  )
  private val checkboxQuestionUuid = UUID.randomUUID()
  private val checkboxQuestion = QuestionEntity(
    questionId = 2L,
    questionUuid = checkboxQuestionUuid,
    answerType = "inline-checkboxes:previous_offences",
    questionCode = "question_code_previous_offences"
  )
  private val checkboxGroupContents = mutableListOf<QuestionGroupEntity>()
  val checkboxGroupUuid1 = UUID.randomUUID()
  private val checkboxGroup = GroupEntity(
    groupId = 11,
    groupUuid = checkboxGroupUuid1,
    groupCode = "previous_offences",
    contents = checkboxGroupContents
  )
  private val checkboxSubQuestion1Id = UUID.randomUUID()
  private val checkboxSubQuestion1 = QuestionEntity(
    questionId = 17,
    questionUuid = checkboxSubQuestion1Id,
    questionCode = "question_code"
  )

  @BeforeEach
  fun setup() {
    contents.add(
      QuestionGroupEntity(
        questionGroupId = 99,
        group = group,
        contentUuid = questionUuid,
        contentType = "question",
        displayOrder = 1,
        question = question,
        nestedGroup = null,
        readOnly = false
      )
    )

    groupWithCheckboxContents.add(
      QuestionGroupEntity(
        questionGroupId = 104,
        group = groupWithCheckbox,
        contentUuid = checkboxQuestionUuid,
        contentType = "question",
        displayOrder = 1,
        question = checkboxQuestion,
        nestedGroup = null,
        readOnly = false
      )
    )
    checkboxGroupContents.add(
      QuestionGroupEntity(
        questionGroupId = 105,
        group = checkboxGroup,
        contentUuid = checkboxSubQuestion1Id,
        contentType = "question",
        displayOrder = 1,
        question = checkboxSubQuestion1,
        nestedGroup = null
      )
    )
  }

  @Test
  fun `get Question Schema by ID`() {
    every { questionRepository.findByQuestionUuid(questionUuid) } returns QuestionEntity(
      questionId = questionId,
      questionUuid = questionUuid,
      questionCode = "question_code"
    )

    val questionSchemaDto = questionService.getQuestion(questionUuid)

    verify(exactly = 1) { questionRepository.findByQuestionUuid(questionUuid) }
    assertThat(questionSchemaDto).isEqualTo(
      QuestionDto(
        questionId = questionId,
        questionUuid = questionUuid,
        questionCode = "question_code",
        answerDtos = emptySet()
      )
    )
  }

  @Test
  fun `throw exception when Question Schema for ID`() {
    every { questionRepository.findByQuestionUuid(questionUuid) } returns null
    Assertions.assertThatThrownBy { questionService.getQuestion(questionUuid) }
      .isInstanceOf(EntityNotFoundException::class.java)
    verify(exactly = 1) { questionRepository.findByQuestionUuid(questionUuid) }
  }

  @Test
  fun `get group contents`() {
    every { questionRepository.findByQuestionUuid(questionUuid) } returns question
    every { groupRepository.findByGroupUuid(groupUuid) } returns group
    every { dependencyService.dependencies() } returns QuestionDependencies(emptyList())

    val groupQuestions = questionService.getGroupContents(groupUuid)

    assertThat(groupQuestions.groupId).isEqualTo(groupUuid)

    val groupContents = groupQuestions.contents
    assertThat(groupContents).hasSize(1)
    val questionRef = groupContents[0] as GroupQuestionDto
    assertThat(questionRef.questionId).isEqualTo(questionUuid)
  }

  @Test
  fun `get group summary`() {
    val groupUuidStr = groupUuid.toString()
    every { questionGroupRepository.listGroups() } returns listOf(
      object : GroupSummaryEntity {
        override val groupUuid = groupUuidStr
        override val groupCode = "Code"
        override val heading = "Heading"
        override val contentCount = 5L
        override val groupCount = 2L
        override val questionCount = 3L
      }
    )

    val summaries = questionService.listGroups()

    assertThat(summaries).hasSize(1)

    val summary = summaries.first()
    assertThat(summary.groupId).isEqualTo(groupUuid)
    assertThat(summary.title).isEqualTo("Heading")
    assertThat(summary.contentCount).isEqualTo(5L)
    assertThat(summary.groupCount).isEqualTo(2L)
    assertThat(summary.questionCount).isEqualTo(3L)
  }

  @Test
  fun `get a flat array of questions for an assessment type`() {
    val questionGroupContents = mutableListOf<QuestionGroupEntity>()
    val questionGroup = GroupEntity(
      groupId = 1,
      groupUuid = UUID.randomUUID(),
      groupCode = "parent_group",
      contents = questionGroupContents,
    )

    val childQuestionGroupContents = mutableListOf<QuestionGroupEntity>()
    val childQuestionGroup = GroupEntity(
      groupId = 2,
      groupUuid = UUID.randomUUID(),
      groupCode = "child_group",
      contents = childQuestionGroupContents,
    )

    val firstQuestion = QuestionEntity(
      questionId = 1,
      questionUuid = UUID.randomUUID(),
      questionCode = "first_question",
    )

    val answerSchemaEntities = mutableListOf<AnswerEntity>()
    val answerSchemaGroup = AnswerGroupEntity(
      answerGroupId = 1,
      answerGroupUuid = UUID.randomUUID(),
      answerEntities = answerSchemaEntities,
    )
    val answer = AnswerEntity(
      answerId = 1,
      answerUuid = UUID.randomUUID(),
      answerCode = "yes",
      value = "YES",
      answerGroup = answerSchemaGroup,
    )
    answerSchemaEntities.add(answer)

    val secondQuestion = QuestionEntity(
      questionId = 2,
      questionUuid = UUID.randomUUID(),
      questionCode = "second_question",
      answerGroup = answerSchemaGroup
    )

    val thirdQuestion = QuestionEntity(
      questionId = 3,
      questionUuid = UUID.randomUUID(),
      questionCode = "third_question",
    )

    questionGroupContents.add(
      QuestionGroupEntity(
        questionGroupId = 1,
        contentUuid = childQuestionGroup.groupUuid,
        contentType = "group",
        group = questionGroup,
        question = null,
        nestedGroup = null,
      )
    )

    childQuestionGroupContents.add(
      QuestionGroupEntity(
        questionGroupId = 2,
        contentUuid = firstQuestion.questionUuid,
        contentType = "question",
        group = childQuestionGroup,
        question = firstQuestion,
        nestedGroup = null,
      )
    )

    questionGroupContents.add(
      QuestionGroupEntity(
        questionGroupId = 3,
        contentUuid = secondQuestion.questionUuid,
        contentType = "question",
        group = questionGroup,
        question = secondQuestion,
        nestedGroup = null,
      )
    )

    questionGroupContents.add(
      QuestionGroupEntity(
        questionGroupId = 4,
        contentUuid = thirdQuestion.questionUuid,
        contentType = "question",
        group = questionGroup,
        question = thirdQuestion,
        nestedGroup = null,
      )
    )

    val questionDependencies = QuestionDependencies(
      questionDeps = listOf(
        QuestionDependencyEntity(
          dependencyId = 1,
          triggerQuestionUuid = secondQuestion.questionUuid,
          triggerAnswerValue = "YES",
          subjectQuestionSchema = thirdQuestion,
          displayInline = true,
        )
      )
    )

    every { dependencyService.dependencies() } returns questionDependencies
    every { groupRepository.findByGroupUuid(questionGroup.groupUuid) } returns questionGroup
    every { groupRepository.findByGroupUuid(childQuestionGroup.groupUuid) } returns childQuestionGroup

    every {
      questionRepository.findByQuestionUuid(firstQuestion.questionUuid)
    } returns firstQuestion
    every {
      questionRepository.findByQuestionUuid(secondQuestion.questionUuid)
    } returns secondQuestion
    every {
      questionRepository.findByQuestionUuid(thirdQuestion.questionUuid)
    } returns thirdQuestion

    val result = questionService.getFlatQuestionsForGroup(questionGroup.groupUuid)

    assertThat(result.size).isEqualTo(3)
    assertThat(
      result
        .map { it as GroupQuestionDto }
        .map { it.questionId }
    ).contains(
      firstQuestion.questionUuid,
      secondQuestion.questionUuid,
      thirdQuestion.questionUuid,
    )
    result
      .map { it as GroupQuestionDto }
      .filter { it.questionId == secondQuestion.questionUuid }
      .forEach {
        assertThat(
          it.answerDtos?.first()?.conditionals?.first()?.conditional
        ).isEqualTo(thirdQuestion.questionCode)
      }
  }
}
