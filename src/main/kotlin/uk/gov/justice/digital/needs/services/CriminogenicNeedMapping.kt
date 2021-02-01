package uk.gov.justice.digital.needs.services

import uk.gov.justice.digital.needs.api.CriminogenicNeed

class CriminogenicNeedMapping {

  companion object {
    fun needs(): Map<CriminogenicNeed, NeedConfiguration> {
      return mapOf(
        CriminogenicNeed.ACCOMMODATION to NeedConfiguration(
          "accom_rosha",
          "accom_offending",
          "accom_lowscore",
          2,
          setOf("no_fixed_abode", "accom_suitability", "accom_permanence", "accom_location")
        ),
        CriminogenicNeed.EDUCATION_TRAINING_AND_EMPLOYABILITY to NeedConfiguration(
          "em_rosha",
          "em_offending",
          "em_lowscore",
          3,
          setOf("employment", "em_history", "em_skills", "em_attitude")
        )
      )
    }
  }
}
