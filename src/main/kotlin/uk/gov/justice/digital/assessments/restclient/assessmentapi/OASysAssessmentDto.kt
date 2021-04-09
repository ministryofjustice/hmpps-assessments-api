package uk.gov.justice.digital.assessments.restclient.assessmentapi

import java.time.LocalDateTime

data class OASysAssessmentDto(
  val assessmentId: Long? = null,
  val refAssessmentVersionCode: String? = null,
  val refAssessmentVersionNumber: String? = null,
  val refAssessmentId: Long? = null,
  val assessmentType: String? = null,
  val assessmentStatus: String? = null,
  val historicStatus: String? = null,
  val refAssessmentOasysScoringAlgorithmVersion: Long? = null,
  val assessorName: String? = null,
  val created: LocalDateTime? = null,
  val completed: LocalDateTime? = null,
  val voided: LocalDateTime? = null,
  val sections: Collection<SectionDto?>? = null,
)
