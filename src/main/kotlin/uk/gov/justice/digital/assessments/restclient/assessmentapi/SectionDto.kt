package uk.gov.justice.digital.assessments.restclient.assessmentapi

data class SectionDto(
  val sectionId: Long? = null,
  val assessmentId: Long? = null,
  val refAssessmentVersionCode: String? = null,
  val refSectionVersionNumber: String? = null,
  val refSectionCode: String? = null,
  val refSectionCrimNeedScoreThreshold: Long? = null,
  val status: String? = null,
  val sectionOgpWeightedScore: Long? = null,
  val sectionOgpRawScore: Long? = null,
  val sectionOvpWeightedScore: Long? = null,
  val sectionOvpRawScore: Long? = null,
  val sectionOtherWeightedScore: Long? = null,
  val sectionOtherRawScore: Long? = null,
  val lowScoreAttentionNeeded: Boolean? = null,
  val questions: Collection<OASysQuestionDto?>? = null
)
