package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.api.Answers
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.TableRows
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import uk.gov.justice.digital.assessments.restclient.audit.AuditType
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import java.util.UUID

@Service
class AssessmentUpdateService(
  private val assessmentRepository: AssessmentRepository,
  private val episodeRepository: EpisodeRepository,
  private val questionService: QuestionService,
  private val riskPredictorsService: RiskPredictorsService,
  private val oasysAssessmentUpdateService: OasysAssessmentUpdateService,
  private val assessmentService: AssessmentService,
  private val authorService: AuthorService,
  private val auditService: AuditService
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Transactional("assessmentsTransactionManager")
  fun updateEpisode(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return updateEpisode(episode, updatedEpisodeAnswers.answers)
  }

  @Transactional("assessmentsTransactionManager")
  fun updateCurrentEpisode(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return updateEpisode(episode, updatedEpisodeAnswers.answers)
  }

  private fun updateEpisode(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: Answers
  ): AssessmentEpisodeDto {
    if (episode.isClosed()) throw UpdateClosedEpisodeException("Cannot update closed Episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid}")

    episode.updateEpisodeAnswers(updatedEpisodeAnswers)

    val currentAuthor = episode.author
    episode.author = authorService.getOrCreateAuthor()

    log.info("Updated episode ${episode.episodeUuid} with ${updatedEpisodeAnswers.size} answer(s) for assessment ${episode.assessment.assessmentUuid}")

    val oasysResult = oasysAssessmentUpdateService.updateOASysAssessment(episode, updatedEpisodeAnswers)

    // shouldn't need this because of the transactional annotation, unless there is an exception which needs handling.
    assessmentRepository.save(episode.assessment)
    log.info("Saved episode ${episode.episodeUuid} for assessment ${episode.assessment.assessmentUuid}")
    auditEpisodeUpdate(currentAuthor, episode)
    return AssessmentEpisodeDto.from(episode, oasysResult)
  }

  fun AssessmentEpisodeEntity.updateEpisodeAnswers(
    updatedEpisodeAnswers: Answers
  ) {
    for (updatedAnswer in updatedEpisodeAnswers) {
      this.answers?.let {
        it[updatedAnswer.key] = updatedAnswer.value
      }
    }
  }

  @Transactional("assessmentsTransactionManager")
  fun closeEpisode(
    episode: AssessmentEpisodeEntity
  ): AssessmentEpisodeDto {
    val offenderPk: Long? = episode.assessment.subject?.oasysOffenderPk
    episode.author = authorService.getOrCreateAuthor()

    val oasysResult = oasysAssessmentUpdateService.completeOASysAssessment(episode, offenderPk)
    if (oasysResult?.hasErrors() == true) {
      log.info("Unable to close episode ${episode.episodeUuid} for assessment ${episode.assessment?.assessmentUuid} with OASys restclient")
    } else {
      episode.close()
      episodeRepository.save(episode)
      auditService.createAuditEvent(
        AuditType.ARN_ASSESSMENT_COMPLETED,
        episode.assessment.assessmentUuid,
        episode.episodeUuid,
        episode.assessment.subject?.crn,
        episode.author
      )
      log.info("Saved closed episode ${episode.episodeUuid} for assessment ${episode.assessment.assessmentUuid}")
    }
    val predictorResults = riskPredictorsService.getPredictorResults(episode = episode, final = true)

    log.info("Predictors for assessment ${episode.assessment.assessmentUuid} are $predictorResults")
    return AssessmentEpisodeDto.from(episode, oasysResult, predictorResults)
  }

  private fun getTableFieldCodes(tableName: String): List<String> {
    val tableFields = questionService.getAllGroupQuestionsByGroupCode(tableName)
    return tableFields.map { it.questionCode }
  }

  private fun getTableFromEpisode(episode: AssessmentEpisodeEntity, tableName: String): TableRows {
    val tables = episode.tables.orEmpty()
    return tables[tableName]
      .orEmpty()
      .toMutableList()
  }

  private fun updateTableForEpisode(episode: AssessmentEpisodeEntity, tableName: String, updatedTable: TableRows) {
    if (episode.tables == null) {
      episode.tables = mutableMapOf(tableName to updatedTable)
    } else {
      episode.tables!![tableName] = updatedTable
    }
  }

  @Transactional("assessmentsTransactionManager")
  fun addEntryToTableForCurrentEpisode(
    assessmentUuid: UUID,
    tableName: String,
    tableEntry: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    val episode = assessmentService.getCurrentEpisode(assessmentUuid)
    return addEntryToTable(episode, tableName, tableEntry)
  }

  @Transactional("assessmentsTransactionManager")
  fun addEntryToTableForEpisode(
    assessmentUuid: UUID,
    episodeUuid: UUID,
    tableName: String,
    tableEntry: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    val episode = assessmentService.getEpisode(assessmentUuid, episodeUuid)
    return addEntryToTable(episode, tableName, tableEntry)
  }

  private fun addEntryToTable(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    newTableEntry: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    val tableFieldCodes = getTableFieldCodes(tableName)

    val tableEntry = newTableEntry.answers
      .filterKeys { it in tableFieldCodes }
      .map { it.key to it.value.toList() }
      .toMap()

    val table = getTableFromEpisode(episode, tableName)

    table.let {
      table.add(tableEntry)
      updateTableForEpisode(episode, tableName, table)

      val oasysResult = oasysAssessmentUpdateService.updateOASysAssessment(episode)

      assessmentRepository.save(episode.assessment)
      log.info("Added row to table $tableName on episode ${episode.episodeUuid} for assessment ${episode.assessment.assessmentUuid}")

      return AssessmentEpisodeDto.from(episode, oasysResult)
    }
  }

  @Transactional("assessmentsTransactionManager")
  fun updateEntryToTableForCurrentEpisode(
    assessmentUuid: UUID,
    tableName: String,
    tableEntry: UpdateAssessmentEpisodeDto,
    index: Int,
  ): AssessmentEpisodeDto {
    val episode = assessmentService.getCurrentEpisode(assessmentUuid)
    return updateTableEntry(episode, tableName, tableEntry, index)
  }

  @Transactional("assessmentsTransactionManager")
  fun updateEntryToTableForEpisode(
    assessmentUuid: UUID,
    episodeUuid: UUID,
    tableName: String,
    tableEntry: UpdateAssessmentEpisodeDto,
    index: Int,
  ): AssessmentEpisodeDto {
    val episode = assessmentService.getEpisode(assessmentUuid, episodeUuid)
    return updateTableEntry(episode, tableName, tableEntry, index)
  }

  private fun updateTableEntry(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    tableEntry: UpdateAssessmentEpisodeDto,
    index: Int,
  ): AssessmentEpisodeDto {
    val tableFieldCodes = getTableFieldCodes(tableName)

    val newValues = tableEntry.answers
      .filterKeys { it in tableFieldCodes }
      .map { it.key to it.value.toList() }
      .toMap()

    val table = getTableFromEpisode(episode, tableName)

    val existingEntry = table[index]
    val updatedEntry = existingEntry
      .toMutableMap()
      .apply { putAll(newValues) }

    table.let {
      table[index] = updatedEntry
      updateTableForEpisode(episode, tableName, table)

      val oasysResult = oasysAssessmentUpdateService.updateOASysAssessment(episode)

      assessmentRepository.save(episode.assessment)
      log.info("Updated row $index for table $tableName on episode ${episode.episodeUuid} for assessment ${episode.assessment.assessmentUuid}")

      return AssessmentEpisodeDto.from(episode, oasysResult)
    }
  }

  @Transactional("assessmentsTransactionManager")
  fun deleteEntryToTableForCurrentEpisode(
    assessmentUuid: UUID,
    tableName: String,
    index: Int,
  ): AssessmentEpisodeDto {
    val episode = assessmentService.getCurrentEpisode(assessmentUuid)
    return deleteTableEntry(episode, tableName, index)
  }

  @Transactional("assessmentsTransactionManager")
  fun deleteEntryToTableForEpisode(
    assessmentUuid: UUID,
    episodeUuid: UUID,
    tableName: String,
    index: Int,
  ): AssessmentEpisodeDto {
    val episode = assessmentService.getEpisode(assessmentUuid, episodeUuid)
    return deleteTableEntry(episode, tableName, index)
  }

  private fun deleteTableEntry(
    episode: AssessmentEpisodeEntity,
    tableName: String,
    index: Int,
  ): AssessmentEpisodeDto {
    val table = getTableFromEpisode(episode, tableName)

    table.let {
      table.removeAt(index)
      updateTableForEpisode(episode, tableName, table)

      val oasysResult = oasysAssessmentUpdateService.updateOASysAssessment(episode)

      assessmentRepository.save(episode.assessment)
      log.info("Removed row $index for table $tableName on episode ${episode.episodeUuid} for assessment ${episode.assessment.assessmentUuid}")

      return AssessmentEpisodeDto.from(episode, oasysResult)
    }
  }

  private fun auditEpisodeUpdate(currentAuthor: AuthorEntity, episode: AssessmentEpisodeEntity) {
    auditService.createAuditEvent(
      AuditType.ARN_ASSESSMENT_UPDATED,
      episode.assessment.assessmentUuid,
      episode.episodeUuid,
      episode.assessment.subject?.crn,
      episode.author
    )
    if (currentAuthor != episode.author) {
      auditService.createAuditEvent(
        AuditType.ARN_ASSESSMENT_REASSIGNED,
        episode.assessment.assessmentUuid,
        episode.episodeUuid,
        episode.assessment.subject?.crn,
        episode.author,
        mapOf("AssignedFrom" to currentAuthor.userName, "assignedTo" to episode.author.userName)
      )
    }
  }
}
