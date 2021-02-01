package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.AssessmentSubjectDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.services.AssessmentService
import java.util.UUID

@RestController
class AssessmentController(val assessmentService: AssessmentService) {

  @RequestMapping(path = ["/assessments/supervision"], method = [RequestMethod.POST])
  @Operation(description = "Creates a new assessment for a supervision, or a court code and case number")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun createNewAssessment(@Parameter(description = "Supervision Id", required = true) @RequestBody createAssessmentDto: CreateAssessmentDto): AssessmentDto {
    return assessmentService.createNewAssessment(createAssessmentDto)
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/subject"], method = [RequestMethod.GET])
  @Operation(description = "Details of the person who is the subject of the assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun getAssessmentSubject(@Parameter(description = "Assessment UUID", required = true) @PathVariable assessmentUuid: UUID): AssessmentSubjectDto {
    return assessmentService.getAssessmentSubject(assessmentUuid)
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes"], method = [RequestMethod.POST])
  @Operation(description = "Creates a new episode for a supervision")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun createNewAssessmentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Reason for the new Episode of Change", required = true) @RequestBody createAssessmentEpisodeDto: CreateAssessmentEpisodeDto
  ): AssessmentEpisodeDto? {
    return assessmentService.createNewEpisode(assessmentUuid, createAssessmentEpisodeDto.changeReason)
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes"], method = [RequestMethod.GET])
  @Operation(description = "Get all the episodes for an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun getAllEpisodesForAssessment(@Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID): Collection<AssessmentEpisodeDto>? {
    return assessmentService.getAssessmentEpisodes(assessmentUuid)
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes/current"], method = [RequestMethod.GET])
  @Operation(description = "Get the current episodes for an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun getCurrentEpisodeForAssessment(@Parameter(description = "Assessment ID", required = true, example = "1234") @PathVariable assessmentUuid: UUID): AssessmentEpisodeDto {
    return assessmentService.getCurrentAssessmentEpisode(assessmentUuid)
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes/{episodeUuid}"], method = [RequestMethod.POST])
  @Operation(description = "updates the answers for an episode")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun updateAssessmentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode UUID", required = true) @PathVariable episodeUuid: UUID,
    @Parameter(description = "Episode Answers", required = true) @RequestBody episodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto? {
    return assessmentService.updateEpisode(assessmentUuid, episodeUuid, episodeAnswers)
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes/current"], method = [RequestMethod.POST])
  @Operation(description = "updates the answers for the current episode")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun updateAssessmentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode Answers", required = true) @RequestBody episodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto? {
    return assessmentService.updateCurrentEpisode(assessmentUuid, episodeAnswers)
  }
}
