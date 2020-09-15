package uk.gov.justice.digital.assessments.controllers

import io.swagger.annotations.*
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.services.AssessmentService

@RestController
@Api(value = "Assessment resources", tags = ["Assessment"])
class AuthenticationController(val assessmentService : AssessmentService) {

    @RequestMapping(path = ["/assessments/supervision"], method = [RequestMethod.POST])
    @ApiOperation(value = "Creates a new assessment for a supervision")
    @ApiResponses(ApiResponse(code = 401, message = "Invalid JWT Token"), ApiResponse(code = 200, message = "OK"))
    fun createNewAssessment(@ApiParam(value = "Supervision Id", required = true) @RequestBody createAssessmentDto : CreateAssessmentDto): AssessmentDto {
        return assessmentService.createNewAssessment(createAssessmentDto.supervisionId)
    }
}
