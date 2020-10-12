package uk.gov.justice.digital.assessments.jpa.entities;

import java.util.*

interface GroupSummaryEntity {
    val groupUuid: UUID
    val heading: String
    val contentCount: Long
    val groupCount: Long
    val questionCount: Long
}