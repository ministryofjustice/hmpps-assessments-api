package uk.gov.justice.digital.assessments.api.groups

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import uk.gov.justice.digital.assessments.api.answers.CheckboxGroupDto

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type"
)
@JsonSubTypes(
  Type(value = GroupQuestionDto::class, name = "question"),
  Type(value = GroupWithContentsDto::class, name = "group"),
  Type(value = CheckboxGroupDto::class, name = "checkboxGroup")
)
interface GroupContentDto
