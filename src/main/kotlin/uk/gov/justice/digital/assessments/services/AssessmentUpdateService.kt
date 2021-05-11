package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.EpisodeRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.services.dto.AssessmentEpisodeUpdateErrors
import uk.gov.justice.digital.assessments.services.dto.OasysAnswers
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import java.util.UUID
import javax.transaction.Transactional

typealias TableAnswers = Map<UUID, Collection<String>>

@Service
class AssessmentUpdateService(
  private val assessmentRepository: AssessmentRepository,
  private val episodeRepository: EpisodeRepository,
  private val questionService: QuestionService,
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient,
  private val assessmentService: AssessmentService
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    const val courtSource = "COURT"
    const val deliusSource = "DELIUS"
  }

  @Transactional
  fun updateEpisode(
    assessmentUuid: UUID,
    episodeUuid: UUID,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    val episode = assessmentService.getEpisode(episodeUuid, assessmentUuid)
    return updateEpisode(episode, updatedEpisodeAnswers)
  }

  @Transactional
  fun updateCurrentEpisode(
    assessmentUuid: UUID,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    val episode = assessmentService.getCurrentEpisode(assessmentUuid)
    return updateEpisode(episode, updatedEpisodeAnswers)
  }

  private fun updateEpisode(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    if (episode.isClosed()) throw UpdateClosedEpisodeException("Cannot update closed Episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid}")

    updateEpisodeAnswers(episode, updatedEpisodeAnswers)
    log.info("Updated episode ${episode.episodeUuid} with ${updatedEpisodeAnswers.answers.size} answer(s) for assessment ${episode.assessment?.assessmentUuid}")

    val oasysResult = updateOASysAssessment(episode.assessment?.subject?.oasysOffenderPk, episode)

    assessmentRepository.save(episode.assessment)
    log.info("Saved episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid}")

    return AssessmentEpisodeDto.from(episode, oasysResult)
  }

  fun updateOASysAssessment(
    offenderPk: Long?,
    episode: AssessmentEpisodeEntity
  ): AssessmentEpisodeUpdateErrors? {
    if (episode.assessmentType == null || episode.oasysSetPk == null || offenderPk == null) {
      log.info("Unable to update OASys Assessment with keys type: ${episode.assessmentType} oasysSet: ${episode.oasysSetPk} offenderPk: $offenderPk")
      return null
    }

    val questions = questionService.getAllQuestions()
    val oasysAnswers = OasysAnswers.from(
      episode, object : OasysAnswers.Companion.MappingProvider {
      override fun getAllQuestions(): QuestionSchemaEntities = questionService.getAllQuestions()
      override fun getTableQuestions(tableCode: String): QuestionSchemaEntities =
        questionService.getAllGroupQuestions(tableCode)
    }
    )

    val oasysUpdateResult = assessmentUpdateRestClient.updateAssessment(
      offenderPk,
      episode.oasysSetPk!!,
      episode.assessmentType!!,
      oasysAnswers
    )
    log.info("Updated OASys assessment oasysSet ${episode.oasysSetPk} ${if (oasysUpdateResult?.validationErrorDtos?.isNotEmpty() == true) "with errors" else "successfully"}")
    oasysAnswers.forEach {
      log.info("Answer ${it.sectionCode}.${it.logicalPage}.${it.questionCode}: ${it.answer}")
    }
    oasysUpdateResult?.validationErrorDtos?.forEach {
      log.info("Error ${it.sectionCode}.${it.logicalPage}.${it.questionCode}: ${it.message}")
    }

    return AssessmentEpisodeUpdateErrors.mapOasysErrors(episode, questions, oasysUpdateResult)
  }

  private fun updateEpisodeAnswers(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ) {
    for (updatedAnswer in updatedEpisodeAnswers.answers) {
      val currentQuestionAnswer = episode.answers?.get(updatedAnswer.key)

      if (currentQuestionAnswer == null) {
        episode.answers?.put(
          updatedAnswer.key,
          AnswerEntity(updatedAnswer.value)
        )
      } else {
        currentQuestionAnswer.answers = updatedAnswer.value
      }
    }
  }

  @Transactional
  fun updateEpisodeTableRow(
    assessmentUuid: UUID,
    episodeUuid: UUID,
    tableName: String,
    index: Int,
    updatedTableRow: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return modifyEpisodeTableRow(
      assessmentUuid,
      episodeUuid,
      tableName
    ) { existingTable ->
      if ((index < 0) || (index >= existingTable.values.first().size))
        throw IllegalStateException("Bad index $index for table $tableName")

      updateTableAnswers(existingTable, index, updatedTableRow.answers)
    }
  }

  @Transactional
  fun addEpisodeTableRow(
    assessmentUuid: UUID,
    episodeUuid: UUID,
    tableName: String,
    newTableRow: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return modifyEpisodeTableRow(
      assessmentUuid,
      episodeUuid,
      tableName
    ) { existingTable ->
      extendTableAnswers(existingTable, newTableRow.answers)
    }
  }

  private fun modifyEpisodeTableRow(
    assessmentUuid: UUID,
    episodeUuid: UUID,
    tableName: String,
    modifyFn: (TableAnswers) -> TableAnswers
  ): AssessmentEpisodeDto {
    val tableQuestions = questionService.getAllGroupQuestions(tableName)
    if (tableQuestions.isEmpty())
      throw IllegalStateException("No questions found for table $tableName")

    val episode = assessmentService.getEpisode(episodeUuid, assessmentUuid)

    val existingTable = grabExistingTableAnswers(episode, tableQuestions)
    val updatedTable = modifyFn(existingTable)

    return updateEpisode(episode, UpdateAssessmentEpisodeDto(updatedTable))
  }

  private fun grabExistingTableAnswers(
    episode: AssessmentEpisodeEntity,
    tableQuestions: QuestionSchemaEntities
  ): TableAnswers {
    val existingTable = mutableMapOf<UUID, Collection<String>>()

    for (questionUuid in tableQuestions.map { it.questionSchemaUuid }) {
      val answer = episode.answers?.get(questionUuid) ?: AnswerEntity()
      existingTable[questionUuid] = answer.answers
    }

    return existingTable
  }

  private fun extendTableAnswers(
    existingTable: TableAnswers,
    newTableRow: TableAnswers
  ): TableAnswers {
    val updatedTable = mutableMapOf<UUID, Collection<String>>()

    for ((id, answers) in existingTable) {
      val newAnswer = newTableRow.getOrDefault(id, listOf(""))
      val extendedAnswer = listOf(answers, newAnswer).flatten()
      updatedTable[id] = extendedAnswer
    }

    return updatedTable
  }

  private fun updateTableAnswers(
    existingTable: TableAnswers,
    index: Int,
    updatedTableRow: TableAnswers
  ): TableAnswers {
    val updatedTable = mutableMapOf<UUID, Collection<String>>()

    for ((id, answers) in existingTable) {
      val updatedAnswer = updatedTableRow.getOrDefault(id, listOf(""))
      val before = answers.toList().subList(0, index)
      val after = answers.toList().subList(index + 1, answers.size)
      val extendedAnswer = listOf(before, updatedAnswer, after).flatten()
      updatedTable[id] = extendedAnswer
    }

    return updatedTable
  }

  @Transactional
  fun closeCurrentEpisode(
    assessmentUuid: UUID
  ): AssessmentEpisodeDto {
    val episode = assessmentService.getCurrentEpisode(assessmentUuid)
    val offenderPk: Long? = episode.assessment?.subject?.oasysOffenderPk
    if (episode.assessmentType == null || episode.oasysSetPk == null || offenderPk == null) {
      log.info("Unable to complete OASys Assessment with keys type: ${episode.assessmentType} oasysSet: ${episode.oasysSetPk} offenderPk: $offenderPk")
      return AssessmentEpisodeDto.from(episode, null)
    }
    val oasysResult = completeOASysAssessment(offenderPk, episode)
    if (oasysResult?.hasErrors() == true) {
      log.info("Unable to close episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid} with OASys restclient")
    } else {
      episode.close()
      episodeRepository.save(episode)
      log.info("Saved closed episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid}")
    }
    return AssessmentEpisodeDto.from(episode, oasysResult)
  }

  fun completeOASysAssessment(
    offenderPk: Long,
    episode: AssessmentEpisodeEntity,
  ): AssessmentEpisodeUpdateErrors? {
    val oasysUpdateResult = assessmentUpdateRestClient.completeAssessment(offenderPk, episode.oasysSetPk!!, episode.assessmentType!!)
    if (oasysUpdateResult?.validationErrorDtos?.isNotEmpty() == true) {
      log.info("Could not complete OASys assessment oasysSet ${episode.oasysSetPk} with errors")
    } else log.info("Completed OASys assessment oasysSet $episode.oasysSetPk successfully")

    oasysUpdateResult?.validationErrorDtos?.forEach {
      log.info("Error ${it.sectionCode}.${it.logicalPage}.${it.questionCode}: ${it.message}")
    }
    return AssessmentEpisodeUpdateErrors.mapOasysErrors(episode, null, oasysUpdateResult)
  }
}
