package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.Answer
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

typealias TableAnswers = Map<UUID, Collection<Answer>>

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

    episode.updateEpisodeAnswers(updatedEpisodeAnswers)
    log.info("Updated episode ${episode.episodeUuid} with ${updatedEpisodeAnswers.answers.size} answer(s) for assessment ${episode.assessment?.assessmentUuid}")

    val oasysResult = updateOASysAssessment(episode, updatedEpisodeAnswers)

    // shouldn't need this because of the transactional annotation, unless there is an exception which needs handling.
    assessmentRepository.save(episode.assessment)
    log.info("Saved episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid}")

    return AssessmentEpisodeDto.from(episode, oasysResult)
  }

  fun AssessmentEpisodeEntity.updateEpisodeAnswers(
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ) {
    for (updatedAnswer in updatedEpisodeAnswers.answers) {
      val currentQuestionAnswer = this.answers?.get(updatedAnswer.key)

      if (currentQuestionAnswer == null) {
        this.answers?.put(
          updatedAnswer.key,
          AnswerEntity(updatedAnswer.value)
        )
      } else {
        currentQuestionAnswer.answers = updatedAnswer.value
      }
    }
  }

  fun updateOASysAssessment(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeUpdateErrors? {
    val offenderPk = episode.assessment?.subject?.oasysOffenderPk
    if (episode.assessmentType == null || episode.oasysSetPk == null || offenderPk == null) {
      log.info("Unable to update OASys Assessment with keys type: ${episode.assessmentType} oasysSet: ${episode.oasysSetPk} offenderPk: $offenderPk")
      return null
    }

    val oasysAnswers = OasysAnswers.from(
      episode,
      object : OasysAnswers.Companion.MappingProvider {
        override fun getAllQuestions(): QuestionSchemaEntities = questionService.getAllSectionQuestionsForQuestions(updatedEpisodeAnswers.answers.keys.toList())
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

    return AssessmentEpisodeUpdateErrors.mapOasysErrors(episode, questionService.getAllQuestions(), oasysUpdateResult)
  }

  @Transactional
  fun addEpisodeTableRow(
    assessmentUuid: UUID,
    episodeUuid: UUID,
    tableName: String,
    newTableRow: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return addTableRow(
      assessmentService.getEpisode(episodeUuid, assessmentUuid),
      tableName,
      newTableRow
    )
  }

  @Transactional
  fun addCurrentEpisodeTableRow(
    assessmentUuid: UUID,
    tableName: String,
    newTableRow: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return addTableRow(
      assessmentService.getCurrentEpisode(assessmentUuid),
      tableName,
      newTableRow
    )
  }

  private fun addTableRow(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    newTableRow: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return modifyEpisodeTable(
      episode,
      tableName
    ) { existingTable ->
      extendTableAnswers(existingTable, newTableRow.answers)
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
    return updateTableRow(
      assessmentService.getEpisode(episodeUuid, assessmentUuid),
      tableName,
      index,
      updatedTableRow
    )
  }

  @Transactional
  fun updateCurrentEpisodeTableRow(
    assessmentUuid: UUID,
    tableName: String,
    index: Int,
    updatedTableRow: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return updateTableRow(
      assessmentService.getCurrentEpisode(assessmentUuid),
      tableName,
      index,
      updatedTableRow
    )
  }

  private fun updateTableRow(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    index: Int,
    updatedTableRow: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return modifyEpisodeTable(
      episode,
      tableName
    ) { existingTable ->
      checkValidTableIndex(tableName, index, existingTable)

      updateTableAnswers(existingTable, index, updatedTableRow.answers)
    }
  }

  @Transactional
  fun deleteEpisodeTableRow(
    assessmentUuid: UUID,
    episodeUuid: UUID,
    tableName: String,
    index: Int
  ): AssessmentEpisodeDto {
    return deleteTableRow(
      assessmentService.getEpisode(episodeUuid, assessmentUuid),
      tableName,
      index
    )
  }

  @Transactional
  fun deleteCurrentEpisodeTableRow(
    assessmentUuid: UUID,
    tableName: String,
    index: Int
  ): AssessmentEpisodeDto {
    return deleteTableRow(
      assessmentService.getCurrentEpisode(assessmentUuid),
      tableName,
      index
    )
  }

  private fun deleteTableRow(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    index: Int
  ): AssessmentEpisodeDto {
    return modifyEpisodeTable(
      episode,
      tableName
    ) { existingTable ->
      checkValidTableIndex(tableName, index, existingTable)

      removeTableAnswers(existingTable, index)
    }
  }

  private fun modifyEpisodeTable(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    modifyFn: (TableAnswers) -> TableAnswers
  ): AssessmentEpisodeDto {
    val tableQuestions = questionService.getAllGroupQuestions(tableName)
    if (tableQuestions.isEmpty())
      throw IllegalStateException("No questions found for table $tableName")

    val existingTable = grabExistingTableAnswers(episode, tableQuestions)
    val updatedTable = modifyFn(existingTable)

    return updateEpisode(episode, UpdateAssessmentEpisodeDto(updatedTable))
  }

  private fun checkValidTableIndex(tableName: String, index: Int, table: TableAnswers) {
    if ((index < 0) || (index >= table.values.first().size))
      throw IllegalStateException("Bad index $index for table $tableName")
  }

  private fun grabExistingTableAnswers(
    episode: AssessmentEpisodeEntity,
    tableQuestions: QuestionSchemaEntities
  ): TableAnswers {
    val existingTable = mutableMapOf<UUID, Collection<Answer>>()

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
    val updatedTable = mutableMapOf<UUID, Collection<Answer>>()

    for ((id, answers) in existingTable) {
      val newAnswer = newTableRow.getOrDefault(id, listOf(Answer("")))
      val extendedAnswer = answers + newAnswer
      updatedTable[id] = extendedAnswer
    }

    return updatedTable
  }

  private fun updateTableAnswers(
    existingTable: TableAnswers,
    index: Int,
    updatedTableRow: TableAnswers
  ): TableAnswers {
    val updatedTable = mutableMapOf<UUID, Collection<Answer>>()

    for ((id, answers) in existingTable) {
      val updatedAnswer = updatedTableRow.getOrDefault(id, listOf(Answer("")))
      val before = answers.toList().subList(0, index)
      val after = answers.toList().subList(index + 1, answers.size)
      val extendedAnswer = listOf(before, updatedAnswer, after).flatten()
      updatedTable[id] = extendedAnswer
    }

    return updatedTable
  }

  private fun removeTableAnswers(
    existingTable: TableAnswers,
    index: Int
  ): TableAnswers {
    val updatedTable = mutableMapOf<UUID, Collection<Answer>>()

    for ((id, answers) in existingTable) {
      val before = answers.toList().subList(0, index)
      val after = answers.toList().subList(index + 1, answers.size)
      val trimmedAnswer = listOf(before, after).flatten()
      updatedTable[id] = trimmedAnswer
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
