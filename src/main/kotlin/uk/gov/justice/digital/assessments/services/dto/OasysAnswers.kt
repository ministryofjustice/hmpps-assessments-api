package uk.gov.justice.digital.assessments.services.dto

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Answer
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OASysMappingEntity
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.services.QuestionSchemaEntities
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class OasysAnswers(
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
      answers: Map<String, AnswerEntity>,
      mappingProvider: MappingProvider
    ): Pair<Set<String>, OasysAnswers> {
      val allTableQuestionCodes = mutableSetOf<String>()
      val allTableAnswers = OasysAnswers()
      tables.forEach { table ->
        val tableQuestions = mappingProvider.getTableQuestions(table)

        val (tableQuestionIds, tableAnswers) = buildTableAnswers(tableQuestions, answers)

        allTableQuestionCodes.addAll(tableQuestionIds)
        allTableAnswers.addAll(tableAnswers)
      }
      return Pair(allTableQuestionCodes, allTableAnswers)
    }

    private fun buildTableAnswers(
      tableQuestions: QuestionSchemaEntities,
      answers: Map<String, AnswerEntity>
    ): Pair<List<String>, OasysAnswers> {
      // gather tables answers
      val questionCodes = tableQuestions.map { it.questionCode }
      val tableAnswers = answers.filter { questionCodes.contains(it.key) }

      if (tableAnswers.isEmpty()) return Pair(questionCodes, OasysAnswers())

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
        questionCodes,
        buildOasysAnswers(tableQuestions, tableAnswers, ::mapOasysTableAnswers)
      )
    }

    private fun buildOasysAnswers(
      questions: QuestionSchemaEntities,
      answers: Map<String, AnswerEntity>,
      builder: (OASysMappingEntity, Collection<Answer>, String?) -> List<OasysAnswer>
    ): OasysAnswers {
      val oasysAnswers = OasysAnswers()
      answers.forEach { (questionCode, answerEntity) ->
        val question = questions[questionCode]
        // TODO: If we want to handle multiple mappings per question we will need to add assessment type to the mapping
        question?.oasysMappings?.firstOrNull()?.let { oasysMapping ->
          oasysAnswers.addAll(
            builder(oasysMapping, answerEntity.answers, question.answerType)
          )
        }
      }
      return oasysAnswers
    }

    fun mapOasysAnswers(
      oasysMapping: OASysMappingEntity,
      answers: Collection<Answer>,
      answerType: String?
    ): List<OasysAnswer> {
      return answers.map { answer ->
        answer.items.map { item ->
          makeOasysAnswer(item, oasysMapping, answerType)
        }
      }.flatten()
    }

    private fun mapOasysTableAnswers(
      oasysMapping: OASysMappingEntity,
      answers: Collection<Answer>,
      answerType: String?
    ): List<OasysAnswer> {
      return answers.mapIndexed { index, answer ->
        answer.items.map { item ->
          makeOasysAnswer(item, oasysMapping, answerType, index.toLong())
        }
      }.flatten()
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
