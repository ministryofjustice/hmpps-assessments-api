package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.GroupContentDto
import uk.gov.justice.digital.assessments.api.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.refdata.PredictorEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.AssessmentSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.OasysAssessmentTypeMappingMissing
import java.util.UUID

@Service
class AssessmentSchemaService(
  private val assessmentSchemaRepository: AssessmentSchemaRepository,
  private val questionService: QuestionService
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun getPredictorsForAssessment(assessmentSchemaCode: AssessmentSchemaCode): List<PredictorEntity> {
    return assessmentSchemaRepository.findByAssessmentSchemaCode(assessmentSchemaCode)?.predictorEntities.orEmpty().toList()
  }

  @Cacheable("AssessmentSchemaService:assessmentSchema")
  fun getAssessmentSchema(assessmentSchemaCode: AssessmentSchemaCode): GroupWithContentsDto {
    return questionService.getGroupContents(getAssessmentSchemaGroupUuid(assessmentSchemaCode))
  }

  @Cacheable("AssessmentSchemaService:questionsForSchemaCode")
  fun getQuestionsForSchemaCode(assessmentSchemaCode: AssessmentSchemaCode): List<GroupContentDto> {
    return questionService.getFlatQuestionsForGroup(getAssessmentSchemaGroupUuid(assessmentSchemaCode))
  }

  @Cacheable("AssessmentSchemaService:assessmentSchemaSummary")
  fun getAssessmentSchemaSummary(assessmentSchemaCode: AssessmentSchemaCode): GroupSectionsDto {
    val assessmentSchemaGroupCode =
      assessmentSchemaRepository.findByAssessmentSchemaCode(assessmentSchemaCode)?.assessmentSchemaGroup?.group?.groupCode
        ?: throw EntityNotFoundException("Assessment Schema not found for assessmentSchemaCode $assessmentSchemaCode")

    return questionService.getGroupSections(assessmentSchemaGroupCode)
  }

  fun toOasysAssessmentType(assessmentSchemaCode: AssessmentSchemaCode): OasysAssessmentType {
    return assessmentSchemaRepository.findByAssessmentSchemaCode(assessmentSchemaCode)?.oasysAssessmentType
      ?: throw OasysAssessmentTypeMappingMissing("Corresponding Oasys assessment type mapping not found for :$assessmentSchemaCode")
  }

  private fun getAssessmentSchemaGroupUuid(assessmentSchemaCode: AssessmentSchemaCode): UUID {
    log.debug("getAssessmentSchemaGroupUuid - begin {}", assessmentSchemaCode)
    return assessmentSchemaRepository.findByAssessmentSchemaCode(assessmentSchemaCode)?.assessmentSchemaGroup?.group?.groupUuid
      ?: throw EntityNotFoundException("Assessment Schema not found for assessmentSchemaCode $assessmentSchemaCode")
  }
}
