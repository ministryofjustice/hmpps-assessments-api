package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.GroupContentDto
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.GroupSummaryDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.api.TableQuestionDto
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AnswerSchemaRepository
import uk.gov.justice.digital.assessments.jpa.repositories.GroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionGroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.UUID

@Service
class QuestionService(
  private val questionSchemaRepository: QuestionSchemaRepository,
  private val questionGroupRepository: QuestionGroupRepository,
  private val groupRepository: GroupRepository,
  private val answerSchemaRepository: AnswerSchemaRepository,
  private val questionDependencyService: QuestionDependencyService
) {
  fun getQuestionSchema(questionSchemaId: UUID): QuestionSchemaDto {
    val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaId)
      ?: throw EntityNotFoundException("Question Schema not found for id: $questionSchemaId")
    return QuestionSchemaDto.from(questionSchemaEntity)
  }

  fun listGroups(): Collection<GroupSummaryDto> {
    return questionGroupRepository.listGroups().map { GroupSummaryDto.from(it) }
  }

  fun getGroupContents(groupCode: String): GroupWithContentsDto {
    return getQuestionGroupContents(findByGroupCode(groupCode))
  }

  fun getGroupContents(groupUuid: UUID): GroupWithContentsDto {
    return getQuestionGroupContents(findByGroupUuid(groupUuid))
  }

  fun getGroupSections(groupCode: String): GroupSectionsDto {
    return fetchGroupSections(findByGroupCode(groupCode))
  }

  private fun getQuestionGroupContents(group: GroupEntity) =
    getQuestionGroupContents(
      group,
      null,
      questionDependencyService.dependencies()
    )

  private fun getQuestionGroupContents(
    group: GroupEntity,
    parentGroup: QuestionGroupEntity?,
    dependencies: QuestionDependencies
  ): GroupWithContentsDto {
    return expandGroupContents(group, parentGroup, dependencies, GroupWithContentsDto::from) as GroupWithContentsDto
  }

  private fun getTableGroupContents(
    group: GroupEntity,
    parentGroup: QuestionGroupEntity?,
    dependencies: QuestionDependencies
  ): TableQuestionDto {
    return expandGroupContents(group, parentGroup, dependencies, TableQuestionDto::from) as TableQuestionDto
  }

  private fun expandGroupContents(
    group: GroupEntity,
    parentGroup: QuestionGroupEntity?,
    dependencies: QuestionDependencies,
    toDto: (GroupEntity, List<GroupContentDto>, QuestionGroupEntity?) -> GroupContentDto
  ): GroupContentDto {
    val groupContents = group.contents.sortedBy { it.displayOrder }
    if (groupContents.isEmpty()) throw EntityNotFoundException("Questions not found for Group: ${group.groupUuid}")

    val contents = groupContents
      .map {
        when (it.contentType) {
          "question" -> getGroupQuestion(it, dependencies)
          "group" -> getQuestionGroupContents(findByGroupUuid(it.contentUuid), it, dependencies)
          else -> throw EntityNotFoundException("Bad group content type")
        }
      }
    return toDto(group, contents, parentGroup)
  }

  private fun getGroupQuestion(
    question: QuestionGroupEntity,
    dependencies: QuestionDependencies
  ): GroupContentDto {
    val questionEntity = questionSchemaRepository.findByQuestionSchemaUuid(question.contentUuid)
      ?: throw EntityNotFoundException("Could not get question ${question.contentUuid}")
    if (questionEntity.answerType?.startsWith("table:") == true)
      return tableGroupQuestion(questionEntity, dependencies)
    return GroupQuestionDto.from(
      questionEntity,
      question,
      dependencies
    )
  }

  private fun tableGroupQuestion(
    questionEntity: QuestionSchemaEntity,
    dependencies: QuestionDependencies
  ): TableQuestionDto {
    val tableName = questionEntity.answerType?.split(":")?.get(1)
      ?: throw EntityNotFoundException("Could not get table name for question ${questionEntity.questionCode}")
    val tableGroup = groupRepository.findByGroupCode(tableName)
      ?: throw EntityNotFoundException("Could not find group $tableName for question ${questionEntity.questionCode}")
    return getTableGroupContents(tableGroup, null, dependencies)
  }

  private fun fetchGroupSections(
    group: GroupEntity,
    depth: Int = 0
  ): GroupSectionsDto {
    val groupContents = if (depth != 2) group.contents.sortedBy { it.displayOrder } else null

    val contents = groupContents
      ?.filter { it.contentType == "group" }
      ?.map { fetchGroupSections(findByGroupUuid(it.contentUuid), depth + 1) }

    return GroupSectionsDto.from(group, contents)
  }

  fun getAllQuestions(): QuestionSchemaEntities {
    return QuestionSchemaEntities(questionSchemaRepository.findAll())
  }

  fun getAllAnswers(): List<AnswerSchemaEntity> {
    return answerSchemaRepository.findAll()
  }

  private fun findByGroupCode(groupCode: String): GroupEntity {
    return groupRepository.findByGroupCode(groupCode)
      ?: findByGroupUuid(groupCode)
  }
  private fun findByGroupUuid(uuidStr: String): GroupEntity {
    val groupUuid = codeToUuid(uuidStr)
    return findByGroupUuid(groupUuid)
  }
  private fun findByGroupUuid(uuid: UUID): GroupEntity {
    return groupRepository.findByGroupUuid(uuid)
      ?: throw EntityNotFoundException("Group not found: $uuid")
  }
  private fun codeToUuid(code: String): UUID {
    try { return UUID.fromString(code) } catch (e: IllegalArgumentException) {
      throw EntityNotFoundException("Group not found: $code")
    }
  }
}

class QuestionSchemaEntities(
  questionsList: List<QuestionSchemaEntity>
) {
  private val questions = questionsList.map { it.questionSchemaUuid to it }.toMap()
  private val oasysMapping = mapByOasysCoords(questionsList)

  operator fun get(questionSchemaUuid: UUID) = questions[questionSchemaUuid]

  fun withExternalSource(): List<QuestionSchemaEntity> {
    return questions.values.filter { it.externalSource != null }
  }

  fun forOasysMapping(
    sectionCode: String?,
    logicalPage: Long?,
    questionCode: String?,
  ): Collection<QuestionSchemaEntity> {
    val questions = oasysMapping.section(sectionCode)?.logicalPage(logicalPage)?.questionCode(questionCode)
    return questions ?: emptyList()
  }

  private class OasysMappingTree {
    private val sections: MutableMap<String?, LogicalPage> = mutableMapOf()

    fun section(sectionCode: String?) = sections[sectionCode]

    fun addSection(sectionCode: String?): LogicalPage {
      if (!sections.containsKey(sectionCode))
        sections[sectionCode] = LogicalPage()
      return sections[sectionCode]!!
    }
  }
  private class LogicalPage {
    private val logicalPages: MutableMap<Long?, QuestionCode> = mutableMapOf()

    fun logicalPage(logicalPage: Long?) = logicalPages[logicalPage]

    fun addLogicalPage(logicalPage: Long?): QuestionCode {
      if (!logicalPages.containsKey(logicalPage))
        logicalPages[logicalPage] = QuestionCode()
      return logicalPages[logicalPage]!!
    }
  }
  private class QuestionCode {
    private val questionCodes: MutableMap<String?, Questions> = mutableMapOf()

    fun questionCode(questionCode: String?) = questionCodes[questionCode]

    fun addQuestionCode(questionCode: String?): Questions {
      if (!questionCodes.containsKey(questionCode))
        questionCodes[questionCode] = Questions()
      return questionCodes[questionCode]!!
    }
  }
  private class Questions(
    private val questions: MutableList<QuestionSchemaEntity> = mutableListOf()
  ) : List<QuestionSchemaEntity> by questions {
    fun addQuestion(question: QuestionSchemaEntity) {
      questions.add(question)
    }
  }

  companion object {
    private fun mapByOasysCoords(questionsList: List<QuestionSchemaEntity>): OasysMappingTree {
      val mapping = OasysMappingTree()
      questionsList.forEach { question ->
        question.oasysMappings.forEach {
          mapping.addSection(it.sectionCode)
            .addLogicalPage(it.logicalPage)
            .addQuestionCode(it.questionCode)
            .addQuestion(question)
        }
      }
      return mapping
    }
  }
}
