package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes(
    Type(value = GroupContentQuestionDto::class, name = "question"),
    Type(value = GroupContentGroupDto::class, name = "group")
)
interface GroupContentDto
