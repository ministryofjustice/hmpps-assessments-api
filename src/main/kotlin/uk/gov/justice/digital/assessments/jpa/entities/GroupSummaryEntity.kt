package uk.gov.justice.digital.assessments.jpa.entities

interface GroupSummaryEntity {
    val groupUuid: String
    val heading: String
    val contentCount: Long
    val groupCount: Long
    val questionCount: Long
}