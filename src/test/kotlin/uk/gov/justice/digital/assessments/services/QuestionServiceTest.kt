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
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.api.TableQuestionDto
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.GroupSummaryEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AnswerSchemaRepository
import uk.gov.justice.digital.assessments.jpa.repositories.GroupRepository
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
  private val dependencyService: QuestionDependencyService = mockk()
  private val questionService = QuestionService(
    questionSchemaRepository,
    questionGroupRepository,
    groupRepository,
    answerSchemaRepository,
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

  private val tableSubQuestion1Id = UUID.randomUUID()
  private val tableSubQuestion2Id = UUID.randomUUID()

  @BeforeEach
  fun setup() {
    contents.add(QuestionGroupEntity(
      questionGroupId = 99,
      group = group,
      contentUuid = questionUuid,
      contentType = "question",
      displayOrder = 1,
      question = question,
      nestedGroup = null,
      readOnly = false
    ))

    groupWithTableContents.add(QuestionGroupEntity(
      questionGroupId = 99,
      group = groupWithTable,
      contentUuid = questionUuid,
      contentType = "question",
      displayOrder = 1,
      question = question,
      nestedGroup = null,
      readOnly = false
    ))
    groupWithTableContents.add(QuestionGroupEntity(
      questionGroupId = 99,
      group = groupWithTable,
      contentUuid = tableQuestionUuid,
      contentType = "question",
      displayOrder = 2,
      question = tableQuestion,
      nestedGroup = null,
      readOnly = false
    ))


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
    every { questionSchemaRepository.findByQuestionSchemaUuid(questionUuid) } returns question
    every { questionSchemaRepository.findByQuestionSchemaUuid(tableQuestionUuid) } returns tableQuestion
    every { dependencyService.dependencies() } returns QuestionDependencies(emptyList())

    val groupQuestions = questionService.getGroupContents(groupWithTableUuid)

    assertThat(groupQuestions.groupId).isEqualTo(groupWithTableUuid)

    val groupContents = groupQuestions.contents
    assertThat(groupContents).hasSize(2)

    val questionRef = groupContents[0] as GroupQuestionDto
    assertThat(questionRef.questionId).isEqualTo(questionUuid)

    val tableRef = groupContents[1] as TableQuestionDto
    assertThat(tableRef.tableId).isEqualTo(tableQuestionUuid)
    assertThat(tableRef.contents).hasSize(2)
    val tableQuestionIds = tableRef.contents.map { (it as GroupQuestionDto).questionId }
    assertThat(tableQuestionIds).contains(tableSubQuestion1Id, tableSubQuestion2Id)
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
}
