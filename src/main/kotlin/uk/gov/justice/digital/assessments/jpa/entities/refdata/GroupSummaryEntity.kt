package uk.gov.justice.digital.assessments.jpa.entities.refdata

interface GroupSummaryEntity {
  val groupUuid: String
  val groupCode: String
  val heading: String
  val contentCount: Long
  val groupCount: Long
  val questionCount: Long
}
