package uk.gov.justice.digital.assessments.api

import io.swagger.annotations.ApiModelProperty
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import java.util.*

class GroupDto (

        @ApiModelProperty
        val groupId: Long,

        @ApiModelProperty
        val groupUuid: UUID,

        @ApiModelProperty
        val heading: String? = null,

        @ApiModelProperty
        val subheading: String? = null,

        @ApiModelProperty
        val helpText: String? = null
){
    companion object{
        fun from(groupEntity: GroupEntity): GroupDto{
        return GroupDto(
                groupEntity.groupId,
                groupEntity.groupUuid,
                groupEntity.heading,
                groupEntity.subheading,
                groupEntity.helpText )
        }
    }
}
