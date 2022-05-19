package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.Answers
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.services.dto.AssessmentEpisodeUpdateErrors
import uk.gov.justice.digital.assessments.services.dto.OasysAnswers

@Service
class OasysAssessmentUpdateService(
  private val questionService: QuestionService,
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient,
  private val assessmentReferenceDataService: AssessmentReferenceDataService
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun updateOASysAssessment(
    episode: AssessmentEpisodeEntity,
  ): AssessmentEpisodeUpdateErrors {
    return updateOASysAssessment(episode, emptyMap())
  }

  fun updateOASysAssessment(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: AnswersDto,
  ): AssessmentEpisodeUpdateErrors {
    val offenderPk = episode.assessment.subject?.oasysOffenderPk
    if (episode.oasysSetPk == null || offenderPk == null) {
      val errorMessage =
        "Unable to update OASys Assessment with keys type: ${episode.assessmentType} oasysSet: ${episode.oasysSetPk} offenderPk: $offenderPk, values cant be null"
      log.error(errorMessage)
      return AssessmentEpisodeUpdateErrors(errorsInAssessment = mutableListOf(errorMessage))
    }

    val oasysAnswers = OasysAnswers.from(
      episode,
      object : OasysAnswers.Companion.MappingProvider {
        override fun getAllQuestions(): QuestionSchemaEntities =
          questionService.getAllSectionQuestionsForQuestions(updatedEpisodeAnswers.keys.toList())

        override fun getTableQuestions(tableCode: String): QuestionSchemaEntities =
          questionService.getAllGroupQuestionsByGroupCode(tableCode)
      }
    )

    val oasysAssessmentType = assessmentReferenceDataService.toOasysAssessmentType(episode.assessmentType)

    val oasysUpdateResult = assessmentUpdateRestClient.updateAssessment(
      offenderPk,
      oasysAssessmentType,
      episode.oasysSetPk!!,
      oasysAnswers
    )
    log.info("Updated OASys assessment oasysSet ${episode.oasysSetPk} ${if (oasysUpdateResult?.validationErrorDtos?.isNotEmpty() == true) "with errors" else "successfully"}")
    oasysAnswers.forEach {
      log.info("Answer ${it.sectionCode}.${it.logicalPage}.${it.questionCode}: ${it.answer}")
    }
    oasysUpdateResult?.validationErrorDtos?.forEach {
      log.info("Error ${it.sectionCode}.${it.logicalPage}.${it.questionCode}: ${it.message}")
    }

    return AssessmentEpisodeUpdateErrors.mapOasysErrors(episode, questionService.getAllQuestions(), oasysUpdateResult)
  }

  fun completeOASysAssessment(
    episode: AssessmentEpisodeEntity,
    offenderPk: Long?,
  ): AssessmentEpisodeUpdateErrors {
    log.debug("Entered completeOASysAssessment")
    if (episode.oasysSetPk == null || offenderPk == null) {
      val errorMessage =
        "Unable to complete OASys Assessment with keys type: ${episode.assessmentType} oasysSet: ${episode.oasysSetPk} offenderPk: $offenderPk, values cant be null"
      log.error(errorMessage)
      return AssessmentEpisodeUpdateErrors(errorsInAssessment = mutableListOf(errorMessage))
    }
    val oasysAssessmentType = assessmentReferenceDataService.toOasysAssessmentType(episode.assessmentType)
    val oasysUpdateResult =
      assessmentUpdateRestClient.completeAssessment(offenderPk, oasysAssessmentType, episode.oasysSetPk!!)
    if (oasysUpdateResult?.validationErrorDtos?.isNotEmpty() == true) {
      log.info("Could not complete OASys assessment oasysSet ${episode.oasysSetPk} with errors")
    } else log.info("Completed OASys assessment oasysSet $episode.oasysSetPk successfully")

    oasysUpdateResult?.validationErrorDtos?.forEach {
      log.info("Error ${it.sectionCode}.${it.logicalPage}.${it.questionCode}: ${it.message}")
    }
    return AssessmentEpisodeUpdateErrors.mapOasysErrors(episode, null, oasysUpdateResult)
  }

  fun createOffenderAndOasysAssessment(
    crn: String?,
    deliusEventId: Long? = null,
    assessmentType: AssessmentType
  ): Pair<Long?, Long?> {
    val oasysOffenderPk =
      crn?.let { assessmentUpdateRestClient.createOasysOffender(crn = crn, deliusEvent = deliusEventId) }
    val oasysAssessmentType = assessmentReferenceDataService.toOasysAssessmentType(assessmentType)
    val oasysSetPK = oasysOffenderPk?.let { assessmentUpdateRestClient.createAssessment(it, oasysAssessmentType) }
    return Pair(oasysOffenderPk, oasysSetPK)
  }
}
