package uk.gov.justice.digital.assessments.api

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import java.time.LocalDateTime

@ApiModel(description = "The Assessment Model")
class AssessmentDto (

    @ApiModelProperty(value = "Assessment primary key", example = "1234")
    val assessmentId: Long? = null,

    @ApiModelProperty(value = "Supervision ID", example = "1234")
    val supervisionId: String? = null,

    @ApiModelProperty(value = "Created Date", example = "2020-01-02T16:00:00")
    val createdDate: LocalDateTime? = null,

    @ApiModelProperty(value = "Completed Date", example = "2020-01-02T16:00:00")
    val completedDate: LocalDateTime? = null
) {

    companion object {

        fun from(assessment: AssessmentEntity): AssessmentDto {
            return AssessmentDto(
                    assessment.assessmentId,
                    assessment.supervisionId,
                    assessment.createdDate,
                    assessment.completedDate
            )
        }
    }
}



