package uk.gov.justice.digital.needs.services

import uk.gov.justice.digital.needs.api.CriminogenicNeed

class CriminogenicNeedMapping {

    companion object {
         fun needs(): Map<CriminogenicNeed, NeedConfiguration> {
            return mapOf(

                    CriminogenicNeed.ACCOMMODATION to NeedConfiguration("3.98", "3.99","3.97", 5, setOf("3.90", "3.91")),
                    CriminogenicNeed.EDUCATION_TRAINING_AND_EMPLOYABILITY to NeedConfiguration("4.96","4.98", "4.95", 5, setOf("4.90", "4.91")))
        }
    }
}