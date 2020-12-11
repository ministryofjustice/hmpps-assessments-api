package uk.gov.justice.digital.assessments.restclient.courtcaseapi

import java.time.LocalDate

data class CourtCase (
    var defendantName: String? = null,
    var defendantDob: LocalDate? = null,
    var pnc: String? = null,
    var crn: String? = null
)
