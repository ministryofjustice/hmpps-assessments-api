package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.api.AnswersDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Answer
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException

typealias TableAnswers = Map<String, Collection<Answer>>

@Service
class AssessmentUpdateService(
  private val assessmentRepository: AssessmentRepository,
  private val episodeRepository: EpisodeRepository,
  private val questionService: QuestionService,
  private val riskPredictorsService: RiskPredictorsService,
  private val oasysAssessmentUpdateService: OasysAssessmentUpdateService,
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Transactional("assessmentsTransactionManager")
  fun updateEpisode(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return updateEpisode(episode, updatedEpisodeAnswers.asAnswersDtos())
  }

  @Transactional("assessmentsTransactionManager")
  fun updateCurrentEpisode(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return updateEpisode(episode, updatedEpisodeAnswers.asAnswersDtos())
  }

  private fun updateEpisode(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: Map<String, AnswersDto>
  ): AssessmentEpisodeDto {
    if (episode.isClosed()) throw UpdateClosedEpisodeException("Cannot update closed Episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid}")

    episode.updateEpisodeAnswers(updatedEpisodeAnswers)
    log.info("Updated episode ${episode.episodeUuid} with ${updatedEpisodeAnswers.size} answer(s) for assessment ${episode.assessment?.assessmentUuid}")

    val oasysResult = oasysAssessmentUpdateService.updateOASysAssessment(episode, updatedEpisodeAnswers)

    // shouldn't need this because of the transactional annotation, unless there is an exception which needs handling.
    assessmentRepository.save(episode.assessment)
    log.info("Saved episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid}")

    return AssessmentEpisodeDto.from(episode, oasysResult)
  }

  fun AssessmentEpisodeEntity.updateEpisodeAnswers(
    updatedEpisodeAnswers: Map<String, AnswersDto>
  ) {
    for (updatedAnswer in updatedEpisodeAnswers) {
      val currentQuestionAnswer = this.answers?.get(updatedAnswer.key)

      if (currentQuestionAnswer == null) {
        this.answers?.put(
          updatedAnswer.key,
          AnswerEntity(updatedAnswer.value.toAnswers())
        )
      } else {
        currentQuestionAnswer.answers = updatedAnswer.value.toAnswers()
      }
    }
  }

  @Transactional("assessmentsTransactionManager")
  fun addEpisodeTableRow(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    newTableRow: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return addTableRow(
      episode,
      tableName,
      newTableRow
    )
  }

  @Transactional("assessmentsTransactionManager")
  fun addCurrentEpisodeTableRow(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    newTableRow: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return addTableRow(
      episode,
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
      extendTableAnswers(existingTable, newTableRow.asAnswersDtos())
    }
  }

  @Transactional("assessmentsTransactionManager")
  fun updateEpisodeTableRow(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    index: Int,
    updatedTableRow: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return updateTableRow(
      episode,
      tableName,
      index,
      updatedTableRow
    )
  }

  @Transactional("assessmentsTransactionManager")
  fun updateCurrentEpisodeTableRow(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    index: Int,
    updatedTableRow: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return updateTableRow(
      episode,
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

      updateTableAnswers(existingTable, index, updatedTableRow.asAnswersDtos())
    }
  }

  @Transactional("assessmentsTransactionManager")
  fun deleteEpisodeTableRow(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    index: Int
  ): AssessmentEpisodeDto {
    return deleteTableRow(
      episode,
      tableName,
      index
    )
  }

  @Transactional("assessmentsTransactionManager")
  fun deleteCurrentEpisodeTableRow(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    index: Int
  ): AssessmentEpisodeDto {
    return deleteTableRow(
      episode,
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
    modifyFn: (TableAnswers) -> Map<String, AnswersDto>
  ): AssessmentEpisodeDto {
    val tableQuestions = questionService.getAllGroupQuestionsByGroupCode(tableName)
    if (tableQuestions.isEmpty())
      throw IllegalStateException("No questions found for table $tableName")

    val existingTable = grabExistingTableAnswers(episode, tableQuestions)
    val updatedTable = modifyFn(existingTable)

    return updateEpisode(episode, updatedTable)
  }

  private fun checkValidTableIndex(tableName: String, index: Int, table: TableAnswers) {
    if ((index < 0) || (index >= table.values.first().size))
      throw IllegalStateException("Bad index $index for table $tableName")
  }

  private fun grabExistingTableAnswers(
    episode: AssessmentEpisodeEntity,
    tableQuestions: QuestionSchemaEntities
  ): TableAnswers {
    val existingTable = mutableMapOf<String, Collection<Answer>>()

    for (questionCode in tableQuestions.map { it.questionCode }) {
      val answer = episode.answers?.get(questionCode) ?: AnswerEntity()
      existingTable[questionCode] = answer.answers
    }

    return existingTable
  }

  private fun extendTableAnswers(
    existingTable: TableAnswers,
    newTableRow: Map<String, AnswersDto>
  ): Map<String, AnswersDto> {
    val updatedTable = mutableMapOf<String, AnswersDto>()

    for ((questionCode, answers) in existingTable) {
      val newAnswer = newTableRow[questionCode]?.toAnswers() ?: listOf(Answer(""))
      val extendedAnswer = answers + newAnswer
      updatedTable[questionCode] = AnswersDto.from(extendedAnswer)
    }

    return updatedTable
  }

  private fun updateTableAnswers(
    existingTable: TableAnswers,
    index: Int,
    updatedTableRow: Map<String, AnswersDto>
  ): Map<String, AnswersDto> {
    val updatedTable = mutableMapOf<String, AnswersDto>()

    for ((question_code, answers) in existingTable) {
      val updatedAnswer = updatedTableRow[question_code]?.toAnswers() ?: listOf(Answer(""))
      val before = answers.toList().subList(0, index)
      val after = answers.toList().subList(index + 1, answers.size)
      val extendedAnswer = listOf(before, updatedAnswer, after).flatten()
      updatedTable[question_code] = AnswersDto.from(extendedAnswer)
    }

    return updatedTable
  }

  private fun removeTableAnswers(
    existingTable: TableAnswers,
    index: Int
  ): Map<String, AnswersDto> {
    val updatedTable = mutableMapOf<String, AnswersDto>()

    for ((question_code, answers) in existingTable) {
      val before = answers.toList().subList(0, index)
      val after = answers.toList().subList(index + 1, answers.size)
      val trimmedAnswer = listOf(before, after).flatten()
      updatedTable[question_code] = AnswersDto.from(trimmedAnswer)
    }

    return updatedTable
  }

  @Transactional("assessmentsTransactionManager")
  fun closeEpisode(
    episode: AssessmentEpisodeEntity
  ): AssessmentEpisodeDto {
    val offenderPk: Long? = episode.assessment?.subject?.oasysOffenderPk
    val oasysResult = oasysAssessmentUpdateService.completeOASysAssessment(episode, offenderPk)
    if (oasysResult?.hasErrors() == true) {
      log.info("Unable to close episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid} with OASys restclient")
    } else {
      episode.close()
      episodeRepository.save(episode)
      log.info("Saved closed episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid}")
    }
    val predictorResults = riskPredictorsService.getPredictorResults(episode = episode, final = true)

    log.info("Predictors for assessment ${episode.assessment?.assessmentUuid} are $predictorResults")
    return AssessmentEpisodeDto.from(episode, oasysResult, predictorResults)
  }

  private fun getTableFieldCodes(tableName: String): List<String> {
      val tableFields = questionService.getAllGroupQuestionsByGroupCode(tableName)
      return tableFields.map {it.questionCode}
  }

  fun addEntryToTable(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    requestBody: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    val tableFieldCodes = getTableFieldCodes(tableName)

    val tableEntry = requestBody.answers
      .filterKeys { it in tableFieldCodes }
      .map { it.key to it.value.toList() }
      .toMap()

    val table = episode.tables[tableName]
      .orEmpty()
      .toMutableList()

    table.let {
      table.add(tableEntry)
      episode.tables[tableName] = table
      // update in OASys
      episodeRepository.save(episode)
    }

    return AssessmentEpisodeDto.from(episode)
  }

  fun updateTableEntry(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    requestBody: UpdateAssessmentEpisodeDto,
    index: Int,
  ): AssessmentEpisodeDto {
    val tableFieldCodes = getTableFieldCodes(tableName)

    val newValues = requestBody.answers
      .filterKeys { it in tableFieldCodes }
      .map { it.key to it.value.toList() }
      .toMap()

    val table = episode.tables[tableName]
      .orEmpty()
      .toMutableList()

    val existingEntry = table[index]
    val updatedEntry = existingEntry
      .toMutableMap()
      .apply { putAll(newValues) }

    table.let {
      table[index] = updatedEntry
      episode.tables[tableName] = table
      // update in OASys
      episodeRepository.save(episode)
    }

    return AssessmentEpisodeDto.from(episode)
  }

  fun deleteTableEntry(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    index: Int,
  ): AssessmentEpisodeDto {
    val table = episode.tables[tableName]
      .orEmpty()
      .toMutableList()

    table.let {
      table.removeAt(index)
      episode.tables[tableName] = table
      // update in OASys
      episodeRepository.save(episode)
    }

    return AssessmentEpisodeDto.from(episode)
  }
}
