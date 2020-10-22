package uk.gov.justice.digital.needs.api

import java.time.LocalDateTime

class CriminogenicNeedsDto (

        val criminogenicNeeds: Collection<CriminogenicNeedDto>,

        val dateCalculated: LocalDateTime,

        ) {




    //The output should include:A list of all needs with an ENUM and DescriptionA Boolean flag for the following:
    //
    //Risk of Harm
    //
    //Risk of Reoffending
    //
    //Low Scoring Need
    //
    //High Scoring Need (over threshold)
}
