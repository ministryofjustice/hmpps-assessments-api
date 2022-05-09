package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.GroupContentDto
import uk.gov.justice.digital.assessments.api.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.config.CacheConstants.ASSESSMENT_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.ASSESSMENT_SUMMARY_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTIONS_FOR_ASSESSMENT_TYPE_CACHE_KEY
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.refdata.PredictorEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.AssessmentRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.OasysAssessmentTypeMappingMissing
import java.util.UUID

@Service
class AssessmentReferenceDataService(
  private val assessmentRepository: AssessmentRepository,
  private val questionService: QuestionService
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun getPredictorsForAssessment(assessmentType: AssessmentType): List<PredictorEntity> {
    return assessmentRepository.findByAssessmentType(assessmentType)?.predictorEntities.orEmpty().toList()
  }

  @Cacheable(ASSESSMENT_CACHE_KEY)
  fun getAssessmentForAssessmentType(assessmentType: AssessmentType): GroupWithContentsDto {
    return questionService.getGroupContents(getAssessmentGroupUuid(assessmentType))
  }

  @Cacheable(QUESTIONS_FOR_ASSESSMENT_TYPE_CACHE_KEY)
  fun getQuestionsForAssessmentType(assessmentType: AssessmentType): List<GroupContentDto> {
    return questionService.getFlatQuestionsForGroup(getAssessmentGroupUuid(assessmentType))
  }

  @Cacheable(ASSESSMENT_SUMMARY_CACHE_KEY)
  fun getAssessmentSummary(assessmentType: AssessmentType): GroupSectionsDto {
    val assessmentSchemaGroupCode =
      assessmentRepository.findByAssessmentType(assessmentType)?.assessmentGroup?.group?.groupCode
        ?: throw EntityNotFoundException("Assessment not found for assessment type: $assessmentType")

    return questionService.getGroupSections(assessmentSchemaGroupCode)
  }

  fun toOasysAssessmentType(assessmentType: AssessmentType): OasysAssessmentType {
    return assessmentRepository.findByAssessmentType(assessmentType)?.oasysAssessmentType
      ?: throw OasysAssessmentTypeMappingMissing("Corresponding Oasys assessment type mapping not found for :$assessmentType")
  }

  private fun getAssessmentGroupUuid(assessmentType: AssessmentType): UUID {
    log.debug("Entered getAssessmentGroupUuid({})", assessmentType)
    return assessmentRepository.findByAssessmentType(assessmentType)?.assessmentGroup?.group?.groupUuid
      ?: throw EntityNotFoundException("Assessment not found for assessment type: $assessmentType")
  }
}
