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
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.api.TableQuestionDto
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.GroupSummaryEntity
import uk.gov.justice.digital.assessments.jpa.entities.OASysMappingEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AnswerSchemaRepository
import uk.gov.justice.digital.assessments.jpa.repositories.GroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.OASysMappingRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionGroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Question Schema Service Tests")
class QuestionServiceTest {
  private val questionSchemaRepository: QuestionSchemaRepository = mockk()
  private val answerSchemaRepository: AnswerSchemaRepository = mockk()
  private val questionGroupRepository: QuestionGroupRepository = mockk()
  private val groupRepository: GroupRepository = mockk()
  private val oasysMappingRepository: OASysMappingRepository = mockk()
  private val dependencyService: QuestionDependencyService = mockk()
  private val questionService = QuestionService(
    questionSchemaRepository,
    questionGroupRepository,
    groupRepository,
    answerSchemaRepository,
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
  private val question = QuestionSchemaEntity(
    questionSchemaId = questionId,
    questionSchemaUuid = questionUuid
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
  private val tableQuestion = QuestionSchemaEntity(
    questionSchemaId = 2L,
    questionSchemaUuid = tableQuestionUuid,
    answerType = "table:children"
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
  private val tableSubQuestion1 = QuestionSchemaEntity(
    questionSchemaId = 12,
    questionSchemaUuid = tableSubQuestion1Id
  )
  private val tableSubQuestion2 = QuestionSchemaEntity(
    questionSchemaId = 14,
    questionSchemaUuid = tableSubQuestion2Id
  )
  private val tableSubQuestion3 = QuestionSchemaEntity(
    questionSchemaId = 16,
    questionSchemaUuid = tableSubQuestion3Id
  )
  private val tableSubQuestion4 = QuestionSchemaEntity(
    questionSchemaId = 18,
    questionSchemaUuid = tableSubQuestion4Id
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
  }

  @Test
  fun `get Question Schema by ID`() {
    every { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) } returns QuestionSchemaEntity(
      questionSchemaId = questionId,
      questionSchemaUuid = questionUuid
    )

    val questionSchemaDto = questionService.getQuestionSchema(questionUuid)

    verify(exactly = 1) { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) }
    assertThat(questionSchemaDto).isEqualTo(QuestionSchemaDto(questionSchemaId = questionId, questionSchemaUuid = questionUuid, answerSchemas = emptySet()))
  }

  @Test
  fun `throw exception when Question Schema for ID`() {
    every { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) } returns null
    Assertions.assertThatThrownBy { questionService.getQuestionSchema(questionUuid) }
      .isInstanceOf(EntityNotFoundException::class.java)
    verify(exactly = 1) { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) }
  }

  @Test
  fun `get group contents`() {
    every { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) } returns question
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
    every { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) } returns question
    every { questionSchemaRepository.findByQuestionSchemaUuid(tableQuestionUuid) } returns tableQuestion
    every { questionSchemaRepository.findByQuestionSchemaUuid(tableSubQuestion1Id) } returns tableSubQuestion1
    every { questionSchemaRepository.findByQuestionSchemaUuid(tableSubQuestion2Id) } returns tableSubQuestion2
    every { questionSchemaRepository.findByQuestionSchemaUuid(tableSubQuestion3Id) } returns tableSubQuestion3
    every { questionSchemaRepository.findByQuestionSchemaUuid(tableSubQuestion4Id) } returns tableSubQuestion4
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
    val tableQuestionIds = tableRef.contents.subList(0,2).map { (it as GroupQuestionDto).questionId }
    assertThat(tableQuestionIds).contains(tableSubQuestion1Id, tableSubQuestion2Id)

    val subGroupRef = tableRef.contents[2] as GroupWithContentsDto
    assertThat(subGroupRef.contents).hasSize(2)
    val subgroupQuestionIds = subGroupRef.contents.map { (it as GroupQuestionDto).questionId }
    assertThat(subgroupQuestionIds).contains(tableSubQuestion3Id, tableSubQuestion4Id)
  }

  @Test
  fun `all questions in a group`() {
    every { groupRepository.findByGroupCode("children") } returns tableGroup
    every { groupRepository.findByGroupCode("childrenSubGroup") } returns tableSubGroup
    every { groupRepository.findByGroupUuid(tableSubGroup.groupUuid) } returns tableSubGroup
    every { groupRepository.findByGroupCode(tableSubGroup.groupUuid.toString()) } returns null
    every { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) } returns question
    every { questionSchemaRepository.findByQuestionSchemaUuid(tableQuestionUuid) } returns tableQuestion
    every { questionSchemaRepository.findByQuestionSchemaUuid(tableSubQuestion1Id) } returns tableSubQuestion1
    every { questionSchemaRepository.findByQuestionSchemaUuid(tableSubQuestion2Id) } returns tableSubQuestion2
    every { questionSchemaRepository.findByQuestionSchemaUuid(tableSubQuestion3Id) } returns tableSubQuestion3
    every { questionSchemaRepository.findByQuestionSchemaUuid(tableSubQuestion4Id) } returns tableSubQuestion4
    every { dependencyService.dependencies() } returns QuestionDependencies(emptyList())

    val subTableQuestions = questionService.getAllGroupQuestions("childrenSubGroup")
    assertThat(subTableQuestions).hasSize(2)
    val subTableQuestionIds = subTableQuestions.map { it.questionSchemaUuid }
    assertThat(subTableQuestionIds).contains(tableSubQuestion3Id, tableSubQuestion4Id)

    val tableQuestions = questionService.getAllGroupQuestions("children")
    assertThat(tableQuestions).hasSize(4)
    val tableQuestionIds = tableQuestions.map { it.questionSchemaUuid }
    assertThat(tableQuestionIds).contains(tableSubQuestion1Id, tableSubQuestion2Id, tableSubQuestion3Id, tableSubQuestion4Id)
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
    every { oasysMappingRepository.findAllByQuestionSchema_QuestionSchemaUuidIn(any()) } returns
      listOf(
        OASysMappingEntity(
          mappingId = 1,
          sectionCode = "section",
          questionCode = "code",
          questionSchema = QuestionSchemaEntity(
            questionSchemaId = 1,
            questionSchemaUuid = questionUuid1
          )
        )
      )

    every { oasysMappingRepository.findAllBySectionCodeIn(listOf("section")) } returns
      listOf(
        OASysMappingEntity(
          mappingId = 1,
          sectionCode = "section",
          questionCode = "code",
          questionSchema = QuestionSchemaEntity(
            questionSchemaId = 1,
            questionSchemaUuid = questionUuid1
          )
        ),
        OASysMappingEntity(
          mappingId = 1,
          sectionCode = "section",
          questionCode = "code",
          questionSchema = QuestionSchemaEntity(
            questionSchemaId = 2,
            questionSchemaUuid = questionUuid2
          )
        )
      )

    val result = questionService.getAllSectionQuestionsForQuestions(listOf(questionUuid1))

    verify(exactly = 1) { oasysMappingRepository.findAllByQuestionSchema_QuestionSchemaUuidIn(any()) }
    assertThat(result).hasSize(2)
    assertThat(result.map { it.questionSchemaUuid }).contains(questionUuid1, questionUuid2)
  }
}
