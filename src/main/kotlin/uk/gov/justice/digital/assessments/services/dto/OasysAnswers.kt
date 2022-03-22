package uk.gov.justice.digital.assessments.services.dto

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Answers
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OASysMappingEntity
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
    private val problemsLevelQuestions: Set<String> = setOf("3.4", "6.4", "9.1", "9.2", "11.2", "11.4", "12.1")
    private val employmentTypeQuestions: Set<String> = setOf("4.2")

    interface MappingProvider {
      fun getAllQuestions(): QuestionSchemaEntities
      fun getTableQuestions(tableCode: String): QuestionSchemaEntities
    }

    fun from(
      episode: AssessmentEpisodeEntity,
      mappingProvider: MappingProvider
    ): OasysAnswers {
      val questions = mappingProvider.getAllQuestions()
      val nonTableAnswers = buildOasysAnswers(
        questions,
        episode.answers,
        ::mapOasysAnswers
      )

      val answers = OasysAnswers()
      answers.addAll(nonTableAnswers)
      return OasysAnswers(answers.map { it.toOasysRealAnswerValues() }.toMutableSet())
    }

    private fun OasysAnswer.toOasysRealAnswerValues(): OasysAnswer {
      if (problemsLevelQuestions.contains(this.questionCode)) {
        return this.copy(answer = ProblemsLevel.valueOf(this.answer).oasysValue.toString())
      } else if (employmentTypeQuestions.contains(this.questionCode)) {
        return this.copy(answer = EmploymentType.valueOf(this.answer).oasysValue.toString())
      }
      return this
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
