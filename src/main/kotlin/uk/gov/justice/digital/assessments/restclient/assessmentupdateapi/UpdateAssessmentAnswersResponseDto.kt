package uk.gov.justice.digital.assessments.restclient.assessmentupdateapi

data class UpdateAssessmentAnswersResponseDto(val oasysSetPk: Long? = null, val validationErrorDtos: Set<ValidationErrorDto> = emptySet())
