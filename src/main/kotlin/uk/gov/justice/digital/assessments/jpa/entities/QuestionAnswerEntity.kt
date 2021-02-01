package uk.gov.justice.digital.assessments.jpa.entities

import java.io.Serializable
import java.util.UUID

data class QuestionAnswerEntity(

  var answers: Map<UUID, List<AnswerEntity>>

) : Serializable
