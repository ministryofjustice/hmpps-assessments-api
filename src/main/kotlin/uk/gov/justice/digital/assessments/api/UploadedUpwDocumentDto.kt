package uk.gov.justice.digital.assessments.api

import java.time.LocalDateTime

data class UploadedUpwDocumentDto(
  val id: Long? = null,
  val documentName: String? = null,
  val crn: String? = null,
  val dateLastModified: LocalDateTime? = null,
  val lastModifiedUser: String? = null,
  val creationDate: LocalDateTime? = null,
)
