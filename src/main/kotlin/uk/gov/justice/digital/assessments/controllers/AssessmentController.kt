package uk.gov.justice.digital.assessments.controllers

import io.swagger.annotations.*
import org.springframework.web.bind.annotation.*
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.services.AssessmentService

@RestController
@Api(value = "Assessment resources", tags = ["Assessment"])
class AssessmentController(val assessmentService : AssessmentService) {

    @RequestMapping(path = ["/assessments/supervision"], method = [RequestMethod.POST])
    @ApiOperation(value = "Creates a new assessment for a supervision")
    @ApiResponses(ApiResponse(code = 401, message = "Invalid JWT Token"), ApiResponse(code = 200, message = "OK"))
    fun createNewAssessment(@ApiParam(value = "Supervision Id", required = true) @RequestBody createAssessmentDto : CreateAssessmentDto): AssessmentDto {
        return assessmentService.createNewAssessment(createAssessmentDto.supervisionId)
    }

    @RequestMapping(path = ["/assessments/{assessmentId}/episodes"], method = [RequestMethod.POST])
    @ApiOperation(value = "Creates a new episode for an episode")
    @ApiResponses(ApiResponse(code = 401, message = "Invalid JWT Token"), ApiResponse(code = 200, message = "OK"))
    fun createNewAssessmentEpisode( @ApiParam(value = "Assessment ID", required = true, example = "1234") @PathVariable assessmentId: Long,
                                    @ApiParam(value = "Reason for the new Episode of Change", required = true) @RequestBody createAssessmentEpisodeDto : CreateAssessmentEpisodeDto): AssessmentEpisodeDto? {
        return assessmentService.createNewEpisode(assessmentId, createAssessmentEpisodeDto.changeReason)
    }

    @RequestMapping(path = ["/assessments/{assessmentId}/episodes"], method = [RequestMethod.GET])
    @ApiOperation(value = "Get all the episodes for an assessment")
    @ApiResponses(ApiResponse(code = 401, message = "Invalid JWT Token"), ApiResponse(code = 200, message = "OK"))
    fun getAllEpisodesForAssessment( @ApiParam(value = "Assessment ID", required = true, example = "1234") @PathVariable assessmentId: Long): Collection<AssessmentEpisodeDto>? {
        return assessmentService.getAssessmentEpisodes(assessmentId)
    }

    @RequestMapping(path = ["/assessments/{assessmentId}/episodes/current"], method = [RequestMethod.GET])
    @ApiOperation(value = "Get the current episodes for an assessment")
    @ApiResponses(ApiResponse(code = 401, message = "Invalid JWT Token"), ApiResponse(code = 200, message = "OK"))
    fun getCurrentEpisodeForAssessment( @ApiParam(value = "Assessment ID", required = true, example = "1234") @PathVariable assessmentId: Long): AssessmentEpisodeDto {
        return assessmentService.getCurrentAssessmentEpisode(assessmentId)
    }
}
