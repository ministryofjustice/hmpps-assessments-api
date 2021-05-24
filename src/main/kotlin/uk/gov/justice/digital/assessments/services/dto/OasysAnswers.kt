package uk.gov.justice.digital.assessments.services.dto

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.OASysMappingEntity
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.services.QuestionSchemaEntities
import java.lang.IllegalStateException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class OasysAnswers(
  private val allAnswers: MutableSet<OasysAnswer> = mutableSetOf()
) : Set<OasysAnswer> by allAnswers {
  private fun addAll(answers: Collection<OasysAnswer>) {
    allAnswers.addAll(answers)
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    interface MappingProvider {
      fun getAllQuestions(): QuestionSchemaEntities
      fun getTableQuestions(tableCode: String): QuestionSchemaEntities
    }

    fun from(
      episode: AssessmentEpisodeEntity,
      mappingProvider: MappingProvider
    ): OasysAnswers {
      val episodeAnswers = episode.answers ?: return OasysAnswers()

      val questions = mappingProvider.getAllQuestions()
      val tables = pullTableNames(questions)

      val (processedQuestions, oasysTableAnswers) =
        buildAllTableAnswers(tables, episodeAnswers, mappingProvider)

      val nonTableAnswers = buildOasysAnswers(
        questions,
        episodeAnswers.filterNot { episodeAnswer -> // skip table questions - we've already done them
          processedQuestions.contains(episodeAnswer.key)
        },
        ::mapOasysAnswers
      )

      val oasysAnswers = OasysAnswers()
      oasysAnswers.addAll(oasysTableAnswers)
      oasysAnswers.addAll(nonTableAnswers)
      return oasysAnswers
    }

    private fun pullTableNames(questions: QuestionSchemaEntities): Set<String> {
      return questions
        .filter { it.answerType?.startsWith("table:") == true }
        .map { it.answerType!!.split(":")[1] }
        .toSet()
    }

    private fun buildAllTableAnswers(
      tables: Set<String>,
      answers: Map<UUID, AnswerEntity>,
      mappingProvider: MappingProvider
    ): Pair<Set<UUID>, OasysAnswers> {
      val allTableQuestionIds = mutableSetOf<UUID>()
      val allTableAnswers = OasysAnswers()
      tables.forEach { table ->
        val tableQuestions = mappingProvider.getTableQuestions(table)

        val (tableQuestionIds, tableAnswers) = buildTableAnswers(tableQuestions, answers)

        allTableQuestionIds.addAll(tableQuestionIds)
        allTableAnswers.addAll(tableAnswers)
      }
      return Pair(allTableQuestionIds, allTableAnswers)
    }

    private fun buildTableAnswers(
      tableQuestions: QuestionSchemaEntities,
      answers: Map<UUID, AnswerEntity>
    ): Pair<List<UUID>, OasysAnswers> {
      // gather tables answers
      val questionUuids = tableQuestions.map { it.questionSchemaUuid }
      val tableAnswers = answers.filter { questionUuids.contains(it.key) }

      if (tableAnswers.isEmpty()) return Pair(questionUuids, OasysAnswers())

      // consistency check
      val tableLength = tableAnswers.values.first().answers.size
      if (tableAnswers.values.any { it.answers.size != tableLength }) {
        log.info("Inconsistent table size")
        answers.forEach {
          log.info(" ${it.key} -> ${it.value.answers.joinToString()}")
        }
      }

      // build OasysAnswers
      return Pair(
        questionUuids,
        buildOasysAnswers(tableQuestions, tableAnswers, ::mapOasysTableAnswers)
      )
    }

    private fun buildOasysAnswers(
      questions: QuestionSchemaEntities,
      answers: Map<UUID, AnswerEntity>,
      builder: (OASysMappingEntity, Collection<String>, String?) -> List<OasysAnswer>
    ): OasysAnswers {
      val oasysAnswers = OasysAnswers()
      answers.forEach { tableAnswer ->
        val question = questions[tableAnswer.key]
        // TODO: If we want to handle multiple mappings per question we will need to add assessment type to the mapping
        question?.oasysMappings?.firstOrNull()?.let { oasysMapping ->
          oasysAnswers.addAll(
            builder(oasysMapping, tableAnswer.value.answers, question.answerType)
          )
        }
      }
      return oasysAnswers
    }

    fun mapOasysAnswers(
      oasysMapping: OASysMappingEntity,
      answers: Collection<String>,
      answerType: String?
    ): List<OasysAnswer> {
      return answers.map {
        makeOasysAnswer(it, oasysMapping, answerType)
      }.toList()
    }

    private fun mapOasysTableAnswers(
      oasysMapping: OASysMappingEntity,
      answers: Collection<String>,
      answerType: String?
    ): List<OasysAnswer> {
      return answers.mapIndexed { index, value ->
        makeOasysAnswer(value, oasysMapping, answerType, index.toLong())
      }.toList()
    }

    private fun makeOasysAnswer(
      value: String,
      oasysMapping: OASysMappingEntity,
      answerType: String?,
      index: Long? = null
    ): OasysAnswer {
      val answer = when (answerType) {
        "date" -> toOASysDate(value)
        else -> value
      }

      return OasysAnswer(
        oasysMapping.sectionCode,
        index ?: oasysMapping.logicalPage,
        oasysMapping.questionCode,
        answer,
        oasysMapping.isFixed
      )
    }

    private fun toOASysDate(dateStr: String): String {
      if (dateStr.length < 10)
        return dateStr
      return LocalDate.parse(dateStr.substring(0, 10), DateTimeFormatter.ISO_DATE).format(oasysDateFormatter)
    }

    private val oasysDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  }
}
