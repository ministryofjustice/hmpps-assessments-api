package uk.gov.justice.digital.assessments.services.dto

import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.OASysMappingEntity
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.services.QuestionSchemaEntities
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OasysAnswers(
  private val allAnswers: MutableSet<OasysAnswer> = mutableSetOf()
) : Set<OasysAnswer> by allAnswers {
  private fun addAll(answers: Collection<OasysAnswer>) {
    allAnswers.addAll(answers)
  }

  companion object {
    interface MappingProvider {
      fun getAllQuestions(): QuestionSchemaEntities
    }

    fun from(
      episode: AssessmentEpisodeEntity,
      mappingProvider: MappingProvider
    ): OasysAnswers {
      val oasysAnswers = OasysAnswers()

      val questions = mappingProvider.getAllQuestions()
      // TODO: If we want to handle multiple mappings per question we will need to add assessment type to the mapping
      episode.answers?.forEach { episodeAnswer ->
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
  }
}
