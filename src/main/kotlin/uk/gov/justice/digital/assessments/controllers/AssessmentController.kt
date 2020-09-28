package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.*
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.services.AssessmentService

@RestController
class AssessmentController(val assessmentService : AssessmentService) {

    @RequestMapping(path = ["/assessments/supervision"], method = [RequestMethod.POST])
    @Operation(description = "Creates a new assessment for a supervision")
    @ApiResponses(value = [
        ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
        ApiResponse(responseCode = "200", description = "OK")
    ])
    fun createNewAssessment(@Parameter(description = "Supervision Id", required = true) @RequestBody createAssessmentDto : CreateAssessmentDto): AssessmentDto {
        return assessmentService.createNewAssessment(createAssessmentDto.supervisionId)
    }

    @RequestMapping(path = ["/assessments/{assessmentId}/episodes"], method = [RequestMethod.POST])
    @Operation(description = "Creates a new episode for an episode")
    @ApiResponses(value = [
        ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
        ApiResponse(responseCode = "200", description = "OK")
    ])
    fun createNewAssessmentEpisode( @Parameter(description = "Assessment ID", required = true, example = "1234") @PathVariable assessmentId: Long,
                                    @Parameter(description = "Reason for the new Episode of Change", required = true) @RequestBody createAssessmentEpisodeDto : CreateAssessmentEpisodeDto): AssessmentEpisodeDto? {
        return assessmentService.createNewEpisode(assessmentId, createAssessmentEpisodeDto.changeReason)
    }

    @RequestMapping(path = ["/assessments/{assessmentId}/episodes"], method = [RequestMethod.GET])
    @Operation(description = "Get all the episodes for an assessment")
    @ApiResponses(value = [
        ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
        ApiResponse(responseCode = "200", description = "OK")
    ])
    fun getAllEpisodesForAssessment( @Parameter(description = "Assessment ID", required = true, example = "1234") @PathVariable assessmentId: Long): Collection<AssessmentEpisodeDto>? {
        return assessmentService.getAssessmentEpisodes(assessmentId)
    }

    @RequestMapping(path = ["/assessments/{assessmentId}/episodes/current"], method = [RequestMethod.GET])
    @Operation(description = "Get the current episodes for an assessment")
    @ApiResponses(value = [
        ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
        ApiResponse(responseCode = "200", description = "OK")
    ])
    fun getCurrentEpisodeForAssessment( @Parameter(description = "Assessment ID", required = true, example = "1234") @PathVariable assessmentId: Long): AssessmentEpisodeDto {
        return assessmentService.getCurrentAssessmentEpisode(assessmentId)
    }
}
