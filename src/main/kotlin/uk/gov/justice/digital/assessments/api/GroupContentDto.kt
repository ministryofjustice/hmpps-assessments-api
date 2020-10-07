package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes(
    Type(value = GroupQuestionDto::class, name = "question"),
    Type(value = GroupWithContentsDto::class, name = "group")
)
interface GroupContentDto
