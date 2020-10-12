package uk.gov.justice.digital.assessments.jpa.entities;

import java.util.*

interface GroupSummaryEntity {
    val groupUuid: String
    val heading: String
    val contentCount: Long
    val groupCount: Long
    val questionCount: Long
}