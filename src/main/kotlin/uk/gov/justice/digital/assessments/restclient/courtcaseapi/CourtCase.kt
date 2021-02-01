package uk.gov.justice.digital.assessments.restclient.courtcaseapi

import java.time.LocalDate

data class CourtCase(
  var courtCode: String? = null,
  var caseNo: String? = null,
  var defendantName: String? = null,
  var defendantDob: LocalDate? = null,
  var pnc: String? = null,
  var crn: String? = null
)
