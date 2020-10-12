package uk.gov.justice.digital.assessments.jpa.entities;

interface GroupSummaryEntity {
    val heading: String
    val groupCount: Long
    val questionCount: Long
}