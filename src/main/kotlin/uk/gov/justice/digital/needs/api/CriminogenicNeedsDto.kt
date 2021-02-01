package uk.gov.justice.digital.needs.api

import java.time.LocalDateTime

class CriminogenicNeedsDto(

  val criminogenicNeeds: Collection<CriminogenicNeedDto>,

  val dateCalculated: LocalDateTime

)
