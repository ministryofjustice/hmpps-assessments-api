package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.api.QuestionDto
import uk.gov.justice.digital.assessments.api.groups.GroupContentDto
import uk.gov.justice.digital.assessments.api.groups.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.groups.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.groups.GroupSummaryDto
import uk.gov.justice.digital.assessments.api.groups.GroupWithContentsDto
import uk.gov.justice.digital.assessments.config.CacheConstants.LIST_QUESTION_GROUPS_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTION_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTION_GROUP_CONTENTS_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTION_GROUP_SECTIONS_CACHE_KEY
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.GroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionGroupRepository
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.UUID

@Service
@Transactional("refDataTransactionManager")
class QuestionService(
  private val questionRepository: QuestionRepository,
  private val questionGroupRepository: QuestionGroupRepository,
  private val groupRepository: GroupRepository,
  private val questionDependencyService: QuestionDependencyService,
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Cacheable(QUESTION_CACHE_KEY)
  fun getQuestion(questionUuid: UUID): QuestionDto {
    val questionEntity = questionRepository.findByQuestionUuid(questionUuid)
      ?: throw EntityNotFoundException("Question not found for id: $questionUuid")
    return QuestionDto.from(questionEntity)
  }

  @Cacheable(LIST_QUESTION_GROUPS_CACHE_KEY)
  fun listGroups(): Collection<GroupSummaryDto> {
    return questionGroupRepository.listGroups().map { GroupSummaryDto.from(it) }
  }

  @Cacheable(QUESTION_GROUP_CONTENTS_CACHE_KEY)
  fun getGroupContents(groupCode: String): GroupWithContentsDto {
    return getQuestionGroupContents(findByGroupCode(groupCode))
  }

  fun getGroupContents(groupUuid: UUID): GroupWithContentsDto {
    return getQuestionGroupContents(findByGroupUuid(groupUuid))
  }

  @Cacheable(QUESTION_GROUP_SECTIONS_CACHE_KEY)
  fun getGroupSections(groupCode: String): GroupSectionsDto {
    return fetchGroupSections(findByGroupCode(groupCode))
  }

  fun flattenQuestionsForGroup(groupUuid: UUID, dependencies: QuestionDependencies): List<GroupContentDto> {
    val group = findByGroupUuid(groupUuid)

    return group.contents.flatMap {
      when (it.contentType) {
        "question" -> {
          val question = getGroupQuestion(it, dependencies)
          listOf(question)
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
      questionDependencyService.dependencies(),
    )

  private fun getQuestionGroupContents(
    group: GroupEntity,
    dependencies: QuestionDependencies,
  ): GroupWithContentsDto {
    return expandGroupContents(group, dependencies, GroupWithContentsDto::from) as GroupWithContentsDto
  }

  private fun expandGroupContents(
    group: GroupEntity,
    dependencies: QuestionDependencies,
    toDto: (GroupEntity, List<GroupContentDto>) -> GroupContentDto,
  ): GroupContentDto {
    log.debug("expandGroupContents {}", group.groupUuid)
    val groupContents = group.contents.sortedBy { it.displayOrder }
    if (groupContents.isEmpty()) throw EntityNotFoundException("Questions not found for Group: ${group.groupUuid}")

    val contents = groupContents
      .map {
        when (it.contentType) {
          "question" -> getGroupQuestion(it, dependencies)
          "group" -> getQuestionGroupContents(findByGroupUuid(it.contentUuid), dependencies)
          else -> throw EntityNotFoundException("Bad group content type")
        }
      }
    return toDto(group, contents)
  }

  private fun getGroupQuestion(
    question: QuestionGroupEntity,
    dependencies: QuestionDependencies,
  ): GroupContentDto {
    log.debug("getGroupQuestion {}", question.contentUuid)
    val questionEntity = questionRepository.findByQuestionUuid(question.contentUuid)
      ?: throw EntityNotFoundException("Could not get question ${question.contentUuid}")

    return GroupQuestionDto.from(
      questionEntity,
      question,
      dependencies,
    )
  }

  private fun fetchGroupSections(
    group: GroupEntity,
    depth: Int = 0,
  ): GroupSectionsDto {
    val groupContents = if (depth != 2) group.contents.sortedBy { it.displayOrder } else null

    val contents = groupContents
      ?.filter { it.contentType == "group" }
      ?.map { fetchGroupSections(findByGroupUuid(it.contentUuid), depth + 1) }

    return GroupSectionsDto.from(group, contents)
  }

  fun getAllQuestions(): QuestionSchemaEntities {
    return QuestionSchemaEntities(questionRepository.findAll())
  }

  private fun findByGroupCode(groupCode: String): GroupEntity {
    return groupRepository.findByGroupCode(groupCode) ?: throw EntityNotFoundException("Group not found: $groupCode")
  }

  private fun findByGroupUuid(uuid: UUID): GroupEntity {
    log.debug("findByGroupUuid {}", uuid)
    return groupRepository.findByGroupUuid(uuid)
      ?: throw EntityNotFoundException("Group not found: $uuid")
  }
}

class QuestionSchemaEntities(
  questionsList: List<QuestionEntity>,
) : List<QuestionEntity> by questionsList {
  private val questions = questionsList.associateBy { it.questionCode }

  operator fun get(questionCode: String) = questions[questionCode]
}
