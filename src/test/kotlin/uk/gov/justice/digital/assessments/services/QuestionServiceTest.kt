package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.CheckboxGroupDto
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.api.QuestionDto
import uk.gov.justice.digital.assessments.api.TableQuestionDto
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupSummaryEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OASysMappingEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionDependencyEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.AnswerRepository
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.GroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.OASysMappingRepository
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionGroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Question Schema Service Tests")
class QuestionServiceTest {
  private val questionRepository: QuestionRepository = mockk()
  private val answerRepository: AnswerRepository = mockk()
  private val questionGroupRepository: QuestionGroupRepository = mockk()
  private val groupRepository: GroupRepository = mockk()
  private val oasysMappingRepository: OASysMappingRepository = mockk()
  private val dependencyService: QuestionDependencyService = mockk()
  private val questionService = QuestionService(
    questionRepository,
    questionGroupRepository,
    groupRepository,
    oasysMappingRepository,
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

  private val groupWithTableUuid = UUID.randomUUID()
  private val groupWithTableContents = mutableListOf<QuestionGroupEntity>()
  private val groupWithTable = GroupEntity(
    groupId = 9,
    groupUuid = groupWithTableUuid,
    groupCode = "group with table",
    contents = groupWithTableContents
  )

  private val tableQuestionUuid = UUID.randomUUID()
  private val tableQuestion = QuestionEntity(
    questionId = 2L,
    questionUuid = tableQuestionUuid,
    answerType = "table:children",
    questionCode = "table_children"
  )

  private val tableGroupContents = mutableListOf<QuestionGroupEntity>()
  private val tableGroup = GroupEntity(
    groupId = 2,
    groupUuid = UUID.randomUUID(),
    groupCode = "children",
    contents = tableGroupContents
  )
  private val tableSubGroupContents = mutableListOf<QuestionGroupEntity>()
  private val tableSubGroup = GroupEntity(
    groupId = 4,
    groupUuid = UUID.randomUUID(),
    groupCode = "children-sub-group",
    contents = tableSubGroupContents
  )
  private val tableSubQuestion1Id = UUID.randomUUID()
  private val tableSubQuestion2Id = UUID.randomUUID()
  private val tableSubQuestion3Id = UUID.randomUUID()
  private val tableSubQuestion4Id = UUID.randomUUID()
  private val tableSubQuestion1 = QuestionEntity(
    questionId = 12,
    questionUuid = tableSubQuestion1Id,
    questionCode = "table_sub_question_one"
  )
  private val tableSubQuestion2 = QuestionEntity(
    questionId = 14,
    questionUuid = tableSubQuestion2Id,
    questionCode = "table_sub_question_two"
  )
  private val tableSubQuestion3 = QuestionEntity(
    questionId = 16,
    questionUuid = tableSubQuestion3Id,
    questionCode = "table_sub_question_three"
  )
  private val tableSubQuestion4 = QuestionEntity(
    questionId = 18,
    questionUuid = tableSubQuestion4Id,
    questionCode = "table_sub_question_four"
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

    groupWithTableContents.add(
      QuestionGroupEntity(
        questionGroupId = 99,
        group = groupWithTable,
        contentUuid = questionUuid,
        contentType = "question",
        displayOrder = 1,
        question = question,
        nestedGroup = null,
        readOnly = false
      )
    )
    groupWithTableContents.add(
      QuestionGroupEntity(
        questionGroupId = 99,
        group = groupWithTable,
        contentUuid = tableQuestionUuid,
        contentType = "question",
        displayOrder = 2,
        question = tableQuestion,
        nestedGroup = null,
        readOnly = false
      )
    )

    tableGroupContents.add(
      QuestionGroupEntity(
        questionGroupId = 100,
        group = tableGroup,
        contentUuid = tableSubQuestion1Id,
        contentType = "question",
        displayOrder = 1,
        question = tableSubQuestion1,
        nestedGroup = null
      )
    )
    tableGroupContents.add(
      QuestionGroupEntity(
        questionGroupId = 100,
        group = tableGroup,
        contentUuid = tableSubQuestion2Id,
        contentType = "question",
        displayOrder = 2,
        question = tableSubQuestion2,
        nestedGroup = null
      )
    )

    tableSubGroupContents.add(
      QuestionGroupEntity(
        questionGroupId = 101,
        group = tableSubGroup,
        contentUuid = tableSubQuestion3Id,
        contentType = "question",
        displayOrder = 1,
        question = tableSubQuestion3,
        nestedGroup = null
      )
    )
    tableSubGroupContents.add(
      QuestionGroupEntity(
        questionGroupId = 102,
        group = tableSubGroup,
        contentUuid = tableSubQuestion4Id,
        contentType = "question",
        displayOrder = 2,
        question = tableSubQuestion4,
        nestedGroup = null
      )
    )

    tableGroupContents.add(
      QuestionGroupEntity(
        questionGroupId = 103,
        group = tableSubGroup,
        contentUuid = tableSubGroup.groupUuid,
        contentType = "group",
        displayOrder = 3,
        question = null,
        nestedGroup = tableSubGroup
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
  fun `get group contents with table`() {
    every { groupRepository.findByGroupUuid(groupWithTableUuid) } returns groupWithTable
    every { groupRepository.findByGroupUuid(tableSubGroup.groupUuid) } returns tableSubGroup
    every { groupRepository.findByGroupCode("children") } returns tableGroup
    every { questionRepository.findByQuestionUuid(questionUuid) } returns question
    every { questionRepository.findByQuestionUuid(tableQuestionUuid) } returns tableQuestion
    every { questionRepository.findByQuestionUuid(tableSubQuestion1Id) } returns tableSubQuestion1
    every { questionRepository.findByQuestionUuid(tableSubQuestion2Id) } returns tableSubQuestion2
    every { questionRepository.findByQuestionUuid(tableSubQuestion3Id) } returns tableSubQuestion3
    every { questionRepository.findByQuestionUuid(tableSubQuestion4Id) } returns tableSubQuestion4
    every { dependencyService.dependencies() } returns QuestionDependencies(emptyList())

    val groupQuestions = questionService.getGroupContents(groupWithTableUuid)

    assertThat(groupQuestions.groupId).isEqualTo(groupWithTableUuid)

    val groupContents = groupQuestions.contents
    assertThat(groupContents).hasSize(2)

    val questionRef = groupContents[0] as GroupQuestionDto
    assertThat(questionRef.questionId).isEqualTo(questionUuid)

    val tableRef = groupContents[1] as TableQuestionDto
    assertThat(tableRef.tableId).isEqualTo(tableGroup.groupUuid)
    assertThat(tableRef.contents).hasSize(3)
    val tableQuestionIds = tableRef.contents.subList(0, 2).map { (it as GroupQuestionDto).questionId }
    assertThat(tableQuestionIds).contains(tableSubQuestion1Id, tableSubQuestion2Id)

    val subGroupRef = tableRef.contents[2] as GroupWithContentsDto
    assertThat(subGroupRef.contents).hasSize(2)
    val subgroupQuestionIds = subGroupRef.contents.map { (it as GroupQuestionDto).questionId }
    assertThat(subgroupQuestionIds).contains(tableSubQuestion3Id, tableSubQuestion4Id)
  }

  @Disabled("Removed as inline checkboxes no longer used")
  @Test
  fun `get group contents with inline-checkboxes`() {
    every { groupRepository.findByGroupUuid(groupWithCheckboxUuid) } returns groupWithCheckbox
    every { groupRepository.findByGroupCode("previous_offences") } returns checkboxGroup
    every { questionRepository.findByQuestionUuid(checkboxQuestionUuid) } returns checkboxQuestion
    every { questionRepository.findByQuestionUuid(checkboxSubQuestion1Id) } returns checkboxSubQuestion1
    every { dependencyService.dependencies() } returns QuestionDependencies(emptyList())

    val groupQuestions = questionService.getGroupContents(groupWithCheckboxUuid)

    assertThat(groupQuestions.groupId).isEqualTo(groupWithCheckboxUuid)
    val groupContents = groupQuestions.contents
    assertThat(groupContents).hasSize(1)

    val checkboxRef = groupContents[0] as CheckboxGroupDto
    assertThat(checkboxRef.checkboxGroupId).isEqualTo(checkboxGroupUuid1)
    assertThat(checkboxRef.contents).hasSize(1)

    val questionIds = checkboxRef.contents.map { (it as GroupQuestionDto).questionId }
    assertThat(questionIds).containsOnly(checkboxSubQuestion1Id)
  }

  @Test
  fun `all questions in a group`() {
    every { groupRepository.findByGroupCode("children") } returns tableGroup
    every { groupRepository.findByGroupCode("childrenSubGroup") } returns tableSubGroup
    every { groupRepository.findByGroupUuid(tableSubGroup.groupUuid) } returns tableSubGroup
    every { groupRepository.findByGroupCode(tableSubGroup.groupUuid.toString()) } returns null
    every { questionRepository.findByQuestionUuid(questionUuid) } returns question
    every { questionRepository.findByQuestionUuid(tableQuestionUuid) } returns tableQuestion
    every { questionRepository.findByQuestionUuid(tableSubQuestion1Id) } returns tableSubQuestion1
    every { questionRepository.findByQuestionUuid(tableSubQuestion2Id) } returns tableSubQuestion2
    every { questionRepository.findByQuestionUuid(tableSubQuestion3Id) } returns tableSubQuestion3
    every { questionRepository.findByQuestionUuid(tableSubQuestion4Id) } returns tableSubQuestion4
    every { dependencyService.dependencies() } returns QuestionDependencies(emptyList())

    val subTableQuestions = questionService.getAllGroupQuestionsByGroupCode("childrenSubGroup")
    assertThat(subTableQuestions).hasSize(2)
    val subTableQuestionIds = subTableQuestions.map { it.questionUuid }
    assertThat(subTableQuestionIds).contains(tableSubQuestion3Id, tableSubQuestion4Id)

    val tableQuestions = questionService.getAllGroupQuestionsByGroupCode("children")
    assertThat(tableQuestions).hasSize(4)
    val tableQuestionIds = tableQuestions.map { it.questionUuid }
    assertThat(tableQuestionIds).contains(
      tableSubQuestion1Id,
      tableSubQuestion2Id,
      tableSubQuestion3Id,
      tableSubQuestion4Id
    )
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
  fun `get all questions for a section that the given question belongs to`() {
    val questionUuid1 = UUID.randomUUID()
    val questionUuid2 = UUID.randomUUID()
    every { oasysMappingRepository.findAllByQuestion_QuestionCodeIn(listOf("question_code_1")) } returns
      listOf(
        OASysMappingEntity(
          mappingId = 1,
          sectionCode = "section",
          questionCode = "code",
          question = QuestionEntity(
            questionId = 1,
            questionUuid = questionUuid1,
            questionCode = "question_code_1"
          )
        )
      )

    every { oasysMappingRepository.findAllBySectionCodeIn(listOf("section")) } returns
      listOf(
        OASysMappingEntity(
          mappingId = 1,
          sectionCode = "section",
          questionCode = "code",
          question = QuestionEntity(
            questionId = 1,
            questionUuid = questionUuid1,
            questionCode = "question_code_1"
          )
        ),
        OASysMappingEntity(
          mappingId = 1,
          sectionCode = "section",
          questionCode = "code",
          question = QuestionEntity(
            questionId = 2,
            questionUuid = questionUuid2,
            questionCode = "question_code_2"
          )
        )
      )

    val result = questionService.getAllSectionQuestionsForQuestions(listOf("question_code_1"))

    verify(exactly = 1) { oasysMappingRepository.findAllByQuestion_QuestionCodeIn(listOf("question_code_1")) }
    assertThat(result).hasSize(2)
    assertThat(result.map { it.questionUuid }).contains(questionUuid1, questionUuid2)
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
          it.answers?.first()?.conditionals?.first()?.conditional
        ).isEqualTo(thirdQuestion.questionCode)
      }
  }
}
