package uk.gov.justice.digital.assessments.restclient.courtcaseapi

import java.time.LocalDate

data class CourtCase(
  var courtCode: String? = null,
  var caseNo: String? = null,
  var defendantName: String? = null,
  var defendantDob: LocalDate? = null,
  var pnc: String? = null,
  var crn: String? = null,
  val defendantAddress: DefendantAddress? = null
)

data class DefendantAddress(
  val line1: String? = null,
  val line2: String? = null,
  val line3: String? = null,
  val line4: String? = null,
  val line5: String? = null,
  val postcode: String? = null
)
