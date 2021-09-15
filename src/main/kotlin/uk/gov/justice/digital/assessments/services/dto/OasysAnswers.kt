package uk.gov.justice.digital.assessments.services.dto

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Answers
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.TableRow
import uk.gov.justice.digital.assessments.jpa.entities.assessments.TableRows
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Tables
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OASysMappingEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionSchemaEntity
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
      val questions = mappingProvider.getAllQuestions()
      val tables = pullTableNames(questions)

      val oasysTableAnswers = buildAllTableAnswers(tables, episode.tables, mappingProvider)

      val nonTableAnswers = buildOasysAnswers(
        questions,
        episode.answers,
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
      listOfTables: Set<String>,
      tables: Tables? = mutableMapOf(),
      mappingProvider: MappingProvider
    ): OasysAnswers {
      val oasysAnswers = listOfTables.map { tableName ->
        convertTableAnswersToOasysAnswers(
          tables!![tableName] ?: mutableListOf(),
          mappingProvider.getTableQuestions(tableName),
        )
      }.flatten().toMutableSet()
      return OasysAnswers(oasysAnswers)
    }

    private fun convertTableAnswersToOasysAnswers(tableRows: TableRows, tableFields: QuestionSchemaEntities): List<OasysAnswer> {
      val results = mutableListOf<OasysAnswer>()
      tableRows.mapIndexed { index, row ->
        tableFields.forEach { field ->
          results.addAll(createOasysAnswerForTableEntry(row, field, index.toLong()))
        }
      }
      return results
    }

    private fun createOasysAnswerForTableEntry(tableRow: TableRow, field: QuestionSchemaEntity, index: Long = 0): List<OasysAnswer> {
      val answersForField = tableRow.getOrDefault(field.questionCode, emptyList())
      return answersForField.mapNotNull { answer ->
        createOasysAnswer(answer, field, index)
      }
    }

    private fun createOasysAnswer(answer: String, field: QuestionSchemaEntity, index: Long = 0): OasysAnswer? {
      return field.oasysMappings.firstOrNull()?.let { mapping ->
        makeOasysAnswer(
          answer,
          mapping,
          field.answerType,
          index,
        )
      }
    }

    private fun buildOasysAnswers(
      questions: QuestionSchemaEntities,
      answers: Answers?,
      builder: (OASysMappingEntity, List<String>, String?) -> List<OasysAnswer>
    ): OasysAnswers {
      val oasysAnswers = OasysAnswers()
      answers?.forEach { (questionCode, answerEntity) ->
        val question = questions[questionCode]
        // TODO: If we want to handle multiple mappings per question we will need to add assessment type to the mapping
        question?.oasysMappings?.firstOrNull()?.let { oasysMapping ->
          oasysAnswers.addAll(
            builder(oasysMapping, answerEntity, question.answerType)
          )
        }
      }
      return oasysAnswers
    }

    fun mapOasysAnswers(
      oasysMapping: OASysMappingEntity,
      answers: List<String>,
      answerType: String?
    ): List<OasysAnswer> {
      return answers.map { answer ->
        makeOasysAnswer(answer, oasysMapping, answerType)
      }
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
