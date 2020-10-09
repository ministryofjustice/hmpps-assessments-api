package uk.gov.justice.digital.assessments.jpa.entities

import java.io.Serializable
import java.util.*


data class AnswerEntity (

        var freeTextAnswer: String? = null,
        var answers: Map<UUID, String> = emptyMap()

) :Serializable
