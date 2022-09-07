package uk.gov.justice.digital.assessments.services.dto

data class AssessmentEpisodeUpdateErrors(
  private val answerErrors: MutableMap<String, MutableCollection<String>> = mutableMapOf(),
  private val errorsOnPage: MutableList<String> = mutableListOf(),
  private val errorsInAssessment: MutableList<String> = mutableListOf(),
) {
  val errors: Map<String, Collection<String>>?
    get() = answerErrors.ifEmpty { null }
  val pageErrors: Collection<String>?
    get() = errorsOnPage.ifEmpty { null }
  val assessmentErrors: Collection<String>?
    get() = errorsInAssessment.ifEmpty { null }
}
