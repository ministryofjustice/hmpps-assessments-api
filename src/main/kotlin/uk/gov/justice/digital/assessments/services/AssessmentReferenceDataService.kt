package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.groups.GroupContentDto
import uk.gov.justice.digital.assessments.api.groups.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.groups.GroupWithContentsDto
import uk.gov.justice.digital.assessments.config.CacheConstants.ASSESSMENT_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.ASSESSMENT_SUMMARY_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTIONS_FOR_ASSESSMENT_TYPE_CACHE_KEY
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.AssessmentRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.UUID

@Service
class AssessmentReferenceDataService(
  private val assessmentRepository: AssessmentRepository,
  private val questionService: QuestionService,
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Cacheable(ASSESSMENT_CACHE_KEY)
  fun getAssessmentForAssessmentType(assessmentType: AssessmentType): GroupWithContentsDto = questionService.getGroupContents(getAssessmentGroupUuid(assessmentType))

  @Cacheable(QUESTIONS_FOR_ASSESSMENT_TYPE_CACHE_KEY)
  fun getQuestionsForAssessmentType(assessmentType: AssessmentType): List<GroupContentDto> = questionService.getFlatQuestionsForGroup(getAssessmentGroupUuid(assessmentType))

  @Cacheable(ASSESSMENT_SUMMARY_CACHE_KEY)
  fun getAssessmentSummary(assessmentType: AssessmentType): GroupSectionsDto {
    val assessmentSchemaGroupCode =
      assessmentRepository.findByAssessmentType(assessmentType)?.assessmentGroup?.group?.groupCode
        ?: throw EntityNotFoundException("Assessment not found for assessment type: $assessmentType")

    return questionService.getGroupSections(assessmentSchemaGroupCode)
  }

  private fun getAssessmentGroupUuid(assessmentType: AssessmentType): UUID {
    log.debug("Entered getAssessmentGroupUuid({})", assessmentType)
    return assessmentRepository.findByAssessmentType(assessmentType)?.assessmentGroup?.group?.groupUuid
      ?: throw EntityNotFoundException("Assessment not found for assessment type: $assessmentType")
  }
}
