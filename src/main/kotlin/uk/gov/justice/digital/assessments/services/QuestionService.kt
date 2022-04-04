package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.api.GroupContentDto
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.GroupSummaryDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.api.TableQuestionDto
import uk.gov.justice.digital.assessments.config.CacheConstants.GROUP_CONTENTS_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.GROUP_SECTIONS_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.LIST_GROUP_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTION_SCHEMA_CACHE_KEY
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.GroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.OASysMappingRepository
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionGroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.services.dto.ExternalSourceQuestionSchemaDto
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.UUID

@Service
@Transactional("refDataTransactionManager")
class QuestionService(
  private val questionSchemaRepository: QuestionSchemaRepository,
  private val questionGroupRepository: QuestionGroupRepository,
  private val groupRepository: GroupRepository,
  private val oasysMappingRepository: OASysMappingRepository,
  private val questionDependencyService: QuestionDependencyService
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Cacheable(QUESTION_SCHEMA_CACHE_KEY)
  fun getQuestionSchema(questionSchemaId: UUID): QuestionSchemaDto {
    val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaId)
      ?: throw EntityNotFoundException("Question Schema not found for id: $questionSchemaId")
    return QuestionSchemaDto.from(questionSchemaEntity)
  }

  @Cacheable(LIST_GROUP_CACHE_KEY)
  fun listGroups(): Collection<GroupSummaryDto> {
    return questionGroupRepository.listGroups().map { GroupSummaryDto.from(it) }
  }

  @Cacheable(GROUP_CONTENTS_CACHE_KEY)
  fun getGroupContents(groupCode: String): GroupWithContentsDto {
    return getQuestionGroupContents(findByGroupCode(groupCode))
  }

  fun getGroupContents(groupUuid: UUID): GroupWithContentsDto {
    return getQuestionGroupContents(findByGroupUuid(groupUuid))
  }

  @Cacheable(GROUP_SECTIONS_CACHE_KEY)
  fun getGroupSections(groupCode: String): GroupSectionsDto {
    return fetchGroupSections(findByGroupCode(groupCode))
  }

  fun flattenQuestionsForGroup(groupUuid: UUID, dependencies: QuestionDependencies): List<GroupContentDto> {
    val group = findByGroupUuid(groupUuid)

    return group.contents.flatMap {
      when (it.contentType) {
        "question" -> {
          val questionSchema = getGroupQuestion(it, dependencies)
          listOf(questionSchema)
        }
        "group" -> {
          flattenQuestionsForGroup(it.contentUuid, dependencies)
        }
        else -> emptyList()
      }
    }
  }

  fun getFlatQuestionsForGroup(groupUuid: UUID): List<GroupContentDto> {
    val dependencies = questionDependencyService.dependencies()
    return flattenQuestionsForGroup(groupUuid, dependencies)
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

  private fun expandGroupContents(
    group: GroupEntity,
    parentGroup: QuestionGroupEntity?,
    dependencies: QuestionDependencies,
    toDto: (GroupEntity, List<GroupContentDto>, QuestionGroupEntity?) -> GroupContentDto
  ): GroupContentDto {
    log.debug("expandGroupContents {}", group.groupUuid)
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
    log.debug("getGroupQuestion {}", question.contentUuid)
    val questionEntity = questionSchemaRepository.findByQuestionSchemaUuid(question.contentUuid)
      ?: throw EntityNotFoundException("Could not get question ${question.contentUuid}")

    return when (questionEntity.answerType?.split(":")?.get(0)) {
      "table" -> tableGroupQuestion(questionEntity, dependencies)
      else -> GroupQuestionDto.from(
        questionEntity,
        question,
        dependencies
      )
    }
  }

  private fun tableGroupQuestion(
    questionEntity: QuestionSchemaEntity,
    dependencies: QuestionDependencies
  ): TableQuestionDto {
    val group = findGroup(questionEntity)
    return expandGroupContents(group, null, dependencies, TableQuestionDto::from) as TableQuestionDto
  }

  private fun findGroup(
    questionEntity: QuestionSchemaEntity
  ): GroupEntity {
    val groupName = questionEntity.answerType?.split(":")?.get(1)
      ?: throw EntityNotFoundException("Could not get group name for question ${questionEntity.questionCode}")
    return groupRepository.findByGroupCode(groupName)
      ?: throw EntityNotFoundException("Could not find group $groupName for question ${questionEntity.questionCode}")
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

  fun getAllGroupQuestionsByGroupCode(groupCode: String): QuestionSchemaEntities {
    return getAllGroupQuestions(findByGroupCode(groupCode))
  }

  private fun getAllGroupQuestions(group: GroupEntity): QuestionSchemaEntities {
    val allQuestions = mutableListOf<QuestionSchemaEntity>()

    group.contents.forEach {
      if (it.contentType == "question")
        allQuestions.add(questionSchemaRepository.findByQuestionSchemaUuid(it.contentUuid)!!)
      if (it.contentType == "group")
        allQuestions.addAll(
          getAllGroupQuestions(findByGroupUuid(it.contentUuid))
        )
    }

    return QuestionSchemaEntities(allQuestions)
  }

  private fun findByGroupCode(groupCode: String): GroupEntity {
    return groupRepository.findByGroupCode(groupCode) ?: throw EntityNotFoundException("Group not found: $groupCode")
  }

  private fun findByGroupUuid(uuid: UUID): GroupEntity {
    log.debug("findByGroupUuid {}", uuid)
    return groupRepository.findByGroupUuid(uuid)
      ?: throw EntityNotFoundException("Group not found: $uuid")
  }

  fun getAllSectionQuestionsForQuestions(questionCodes: List<String>): QuestionSchemaEntities {
    val mappings = oasysMappingRepository.findAllByQuestionSchema_QuestionCodeIn(questionCodes)
    val sections = mappings?.map { it.sectionCode }?.distinct() ?: emptyList()
    return QuestionSchemaEntities(
      oasysMappingRepository.findAllBySectionCodeIn(sections)
        .map { it.questionSchema }.distinct()
    )
  }
}

class QuestionSchemaEntities(
  questionsList: List<QuestionSchemaEntity>
) : List<QuestionSchemaEntity> by questionsList {
  private val questions = questionsList.associateBy { it.questionCode }
  private val oasysMapping = mapByOasysCoords(questionsList)

  operator fun get(questionCode: String) = questions[questionCode]

  fun withExternalSource(assessmentSchemaCode: AssessmentSchemaCode): List<ExternalSourceQuestionSchemaDto> {
    return questions.values
      .filter { !it.externalSources.isEmpty() }
      .filter { it.externalSources.any { source -> source.assessmentSchemaCode == assessmentSchemaCode } }
      .map { it.toQuestionSchemaDto(assessmentSchemaCode) }
      .flatten()
  }

  private fun QuestionSchemaEntity.toQuestionSchemaDto(assessmentSchemaCode: AssessmentSchemaCode): List<ExternalSourceQuestionSchemaDto> {
    val source = this.externalSources.filter { it.assessmentSchemaCode == assessmentSchemaCode }
    return source.map {
      ExternalSourceQuestionSchemaDto(
        it.questionSchema.questionCode,
        it.externalSource,
        it.jsonPathField,
        it.fieldType,
        it.externalSourceEndpoint,
        it.mappedValue,
        it.ifEmpty
      )
    }
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
