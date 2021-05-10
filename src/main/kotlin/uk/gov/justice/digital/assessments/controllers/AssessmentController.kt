package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

  @RequestMapping(path = ["/assessments"], method = [RequestMethod.POST])
  @Operation(description = "Creates a new assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun createNewAssessment(@RequestBody createAssessmentDto: CreateAssessmentDto): AssessmentDto {
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
  @Operation(description = "Creates a new episode for an assessment")
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
    return assessmentService.createNewEpisode(assessmentUuid, createAssessmentEpisodeDto.changeReason, createAssessmentEpisodeDto.assessmentType)
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
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The update couldn't be processed")
    ]
  )
  fun updateAssessmentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode UUID", required = true) @PathVariable episodeUuid: UUID,
    @Parameter(description = "Episode Answers", required = true) @RequestBody episodeAnswers: UpdateAssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentService.updateEpisode(assessmentUuid, episodeUuid, episodeAnswers)
    )
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes/{episodeUuid}/{tableName}"], method = [RequestMethod.POST])
  @Operation(description = "adds a row to a table answer")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The update couldn't be processed")
    ]
  )
  fun addAssessmentEpisodeTableRow(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode UUID", required = true) @PathVariable episodeUuid: UUID,
    @Parameter(description = "Table Name", required = true) @PathVariable tableName: String,
    @Parameter(description = "New Row", required = true) @RequestBody episodeAnswers: UpdateAssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentService.addEpisodeTableRow(assessmentUuid, episodeUuid, tableName, episodeAnswers)
    )
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes/{episodeUuid}/{tableName}/{index}"], method = [RequestMethod.POST])
  @Operation(description = "adds a row to a table answer")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The update couldn't be processed")
    ]
  )
  fun updateAssessmentEpisodeTableRow(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode UUID", required = true) @PathVariable episodeUuid: UUID,
    @Parameter(description = "Table Name", required = true) @PathVariable tableName: String,
    @Parameter(description = "Row index", required = true) @PathVariable index: Int,
    @Parameter(description = "Updated Row", required = true) @RequestBody episodeAnswers: UpdateAssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentService.updateEpisodeTableRow(assessmentUuid, episodeUuid, tableName, index, episodeAnswers)
    )
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes/current"], method = [RequestMethod.POST])
  @Operation(description = "updates the answers for the current episode")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The update couldn't be processed")
    ]
  )
  fun updateAssessmentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode Answers", required = true) @RequestBody episodeAnswers: UpdateAssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentService.updateCurrentEpisode(assessmentUuid, episodeAnswers)
    )
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/complete"], method = [RequestMethod.POST])
  @Operation(description = "completes current episode")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The update couldn't be processed")
    ]
  )
  fun completeAssessmentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentService.closeCurrentEpisode(assessmentUuid)
    )
  }

  private fun updateResponse(
    assessmentEpisode: AssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    val code = if (assessmentEpisode.errors == null) HttpStatus.OK else HttpStatus.UNPROCESSABLE_ENTITY
    return ResponseEntity(assessmentEpisode, code)
  }
}
