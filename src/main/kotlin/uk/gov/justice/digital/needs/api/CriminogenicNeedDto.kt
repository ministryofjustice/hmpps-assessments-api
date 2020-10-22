package uk.gov.justice.digital.needs.api

class CriminogenicNeedDto(

        val need: CriminogenicNeed,
        val description: String,
        val overThreshold: Boolean,
        val riskOfHarm: Boolean,
        val riskOfReoffending: Boolean,
        val lowScoringNeed: Boolean,
        val needStatus: NeedStatus



) {




}
