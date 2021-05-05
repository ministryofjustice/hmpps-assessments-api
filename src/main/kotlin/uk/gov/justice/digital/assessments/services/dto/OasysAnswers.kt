package uk.gov.justice.digital.assessments.services.dto

import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.OASysMappingEntity
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.services.QuestionSchemaEntities
import java.lang.IllegalStateException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class OasysAnswers(
  private val allAnswers: MutableSet<OasysAnswer> = mutableSetOf()
) : Set<OasysAnswer> by allAnswers {
  private fun addAll(answers: Collection<OasysAnswer>) {
    allAnswers.addAll(answers)
  }

  companion object {
    interface MappingProvider {
      fun getAllQuestions(): QuestionSchemaEntities
      fun getTableQuestions(tableCode: String): QuestionSchemaEntities
    }

    fun from(
      episode: AssessmentEpisodeEntity,
      mappingProvider: MappingProvider
    ): OasysAnswers {
      val oasysAnswers = OasysAnswers()
      val episodeAnswers = episode.answers ?: return oasysAnswers

      val questions = mappingProvider.getAllQuestions()
      val tables = pullTableNames(questions)

      val (processedQuestions, oasysTableAnswers) =
        buildAllTableAnswers(tables, episodeAnswers, mappingProvider)

      oasysAnswers.addAll(oasysTableAnswers)

      // TODO: If we want to handle multiple mappings per question we will need to add assessment type to the mapping
      episodeAnswers
        .filterNot { episodeAnswer ->
          processedQuestions.contains(episodeAnswer.key) } // skip table questions - we've already done them
        .forEach { episodeAnswer ->
          val question = questions[episodeAnswer.key]
          val oasysMapping = question?.oasysMappings?.toList()?.getOrNull(0)
          oasysAnswers.addAll(
            mapOasysAnswer(
              oasysMapping,
              episodeAnswer.value.answers,
              question?.answerType
            )
          )
        }

      return oasysAnswers
    }

    fun mapOasysAnswer(
      oasysMapping: OASysMappingEntity?,
      answers: Collection<String>,
      answerType: String?
    ): List<OasysAnswer> {
      if (oasysMapping == null) return emptyList()

      return answers.map { it ->
        val answer = when (answerType) {
          "date" -> toOASysDate(it)
          else -> it
        }

        OasysAnswer(
          oasysMapping.sectionCode,
          oasysMapping.logicalPage,
          oasysMapping.questionCode,
          answer,
          oasysMapping.isFixed
        )
      }.toList()
    }

    private fun toOASysDate(dateStr: String): String {
      if (dateStr.length < 10)
        return dateStr
      return LocalDate.parse(dateStr.substring(0, 10), DateTimeFormatter.ISO_DATE).format(oasysDateFormatter)
    }

    val oasysDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private fun pullTableNames(questions: QuestionSchemaEntities): Set<String> {
      return questions
        .filter { it.answerType?.startsWith("table:") == true }
        .map { it.answerType!!.split(":")[1] }
        .toSet()
    }

    private fun buildAllTableAnswers(
      tables: Set<String>,
      answers: Map<UUID, AnswerEntity>,
      mappingProvider: MappingProvider): Pair<Set<UUID>, OasysAnswers> {

      val tableQuestionIds = mutableSetOf<UUID>()
      val mappingAnswers = OasysAnswers()
      tables.forEach { table ->
        val tableQuestions = mappingProvider.getTableQuestions(table)

        tableQuestionIds.addAll(tableQuestions.map { it.questionSchemaUuid })
        mappingAnswers.addAll(buildTableAnswers(tableQuestions, answers))
      }
      return Pair(tableQuestionIds, mappingAnswers)
    }

    private fun buildTableAnswers(
      tableQuestions: QuestionSchemaEntities,
      answers: Map<UUID, AnswerEntity>
    ): OasysAnswers {
      val oasysAnswers = OasysAnswers()

      // gather tables answers
      val questionUuids = tableQuestions.map { it.questionSchemaUuid }
      val tableAnswers = answers.filter { questionUuids.contains(it.key) }

      if (tableAnswers.isEmpty()) return oasysAnswers

      // consistency check
      val tableLength = tableAnswers.values.first().answers.size
      if (tableAnswers.values.filter { it.answers.size != tableLength }.isNotEmpty())
        throw IllegalStateException("Inconsistent table answers") // this is rubbish message

      // build OasysAnswers
      tableAnswers.forEach { tableAnswer ->
        val question = tableQuestions[tableAnswer.key]
        val oasysMapping = question?.oasysMappings?.toList()?.getOrNull(0)
        oasysAnswers.addAll(
          mapOasysTableAnswer(
            oasysMapping,
            tableAnswer.value.answers,
            question?.answerType
          )
        )
      }

      return oasysAnswers
    }

    fun mapOasysTableAnswer(
      oasysMapping: OASysMappingEntity?,
      answers: Collection<String>,
      answerType: String?
    ): List<OasysAnswer> {
      if (oasysMapping == null) return emptyList()

      return answers.mapIndexed { index, value ->
        val answer = when (answerType) {
          "date" -> toOASysDate(value)
          else -> value
        }

        OasysAnswer(
          oasysMapping.sectionCode,
          index.toLong(),
          oasysMapping.questionCode,
          answer,
          oasysMapping.isFixed
        )
      }.toList()
    }

  }
}
