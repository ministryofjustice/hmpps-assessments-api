package uk.gov.justice.digital.assessments.api

import java.util.*

data class AssessmentAnswersDto (

        val assessmentUuid: UUID,
        //Question Code -> List Of Answer Codes
        val answers: Map<String, Set<String>>
        )