package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.OASysMappingRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentApiRestClient
import uk.gov.justice.digital.assessments.restclient.assessmentapi.RefElementDto
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.UUID

@Service
class ReferenceDataService(
  private val assessmentClient: AssessmentApiRestClient,
  private val assessmentRepository: AssessmentRepository,
  private val oaSysMappingRepository: OASysMappingRepository,
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun getFilteredReferenceData(assessmentUuid: UUID, episodeUuid: UUID, questionUuid: UUID, parentFields: Map<UUID, String>?): Map<String, Collection<RefElementDto>> {
    val assessment = assessmentRepository.findByAssessmentUuid(assessmentUuid)
    val episode = assessment?.episodes?.find { e -> e.episodeUuid == episodeUuid }
      ?: throw EntityNotFoundException("Failed to find episode $episodeUuid for assessment $assessmentUuid")

    val questionUuids = mutableListOf(questionUuid)
    parentFields?.mapNotNull { parentField -> parentField.key }?.toCollection(questionUuids)

    val oasysMappings = oaSysMappingRepository.findAllByQuestionSchema_QuestionSchemaUuidIn(questionUuids)
      ?.associate { m -> m.questionSchema.questionSchemaUuid to m }

    val questionSchema = oasysMappings?.get(questionUuid)
      ?: throw EntityNotFoundException("Failed to find OASys mappings for question schema $questionUuid")

    val mappedParentFields: Map<String, String>? = parentFields
      ?.map { (key, value) ->
        oasysMappings.getOrElse(key) { throw EntityNotFoundException("Failed to find OASys mappings for parent field $key") }.questionCode to value
      }?.toMap()

    return episode.oasysSetPk.let {
      assessmentClient.getFilteredReferenceData(
        oasysSetPk = episode.oasysSetPk!!,
        offenderPk = assessment.subject?.oasysOffenderPk,
        assessmentType = episode.assessmentType.toString(),
        sectionCode = questionSchema.sectionCode,
        fieldName = questionSchema.questionCode,
        parentList = mappedParentFields
      )
    }.also { log.info("Returning ${it?.size ?: 0} reference data items for field ${questionSchema.questionCode}") }
      ?: emptyMap()
  }
}
