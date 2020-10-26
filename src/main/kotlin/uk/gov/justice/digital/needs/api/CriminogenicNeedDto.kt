package uk.gov.justice.digital.needs.api

class CriminogenicNeedDto(

        val need: CriminogenicNeed,
        val description: String,
        val overThreshold: Boolean? = null,
        val riskOfHarm: Boolean? = null,
        val riskOfReoffending: Boolean? = null,
        val lowScoringNeed: Boolean? = null,
        val needStatus: NeedStatus



) {




}
