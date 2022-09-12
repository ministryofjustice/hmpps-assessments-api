package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.AssessmentSubjectDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.services.AssessmentService
import uk.gov.justice.digital.assessments.services.AssessmentUpdateService
import java.util.UUID

@RestController
class AssessmentController(
  val assessmentService: AssessmentService,
  val assessmentUpdateService: AssessmentUpdateService,
) {

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @RequestMapping(path = ["/assessments"], method = [RequestMethod.POST])
  @Operation(description = "Creates a new assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun createNewAssessment(@RequestBody createAssessmentDto: CreateAssessmentDto): AssessmentDto {
    return assessmentService.createNewAssessment(createAssessmentDto)
  }

  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun getAssessmentSubject(
    @Parameter(
      description = "Assessment UUID",
      required = true
    ) @PathVariable assessmentUuid: UUID
  ): AssessmentSubjectDto {
    return assessmentService.getAssessmentSubject(assessmentUuid)
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes/current"], method = [RequestMethod.GET])
  @Operation(description = "Get the current episodes for an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun getCurrentEpisodeForAssessment(
    @Parameter(
      description = "Assessment ID",
      required = true,
      example = "1234"
    ) @PathVariable assessmentUuid: UUID
  ): AssessmentEpisodeDto {
    return assessmentService.getCurrentAssessmentEpisode(assessmentUuid)
  }

  @RequestMapping(path = ["/assessments/subject/{crn}/episodes/current"], method = [RequestMethod.GET])
  @Operation(description = "Get the current episodes for a given crn")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun getCurrentEpisode(@PathVariable crn: String): AssessmentEpisodeDto {
    log.debug("Entered getCurrentEpisode{}", crn)
    return assessmentService.getCurrentEpisode(crn)
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes/{episodeUuid}"], method = [RequestMethod.GET])
  @Operation(description = "Get an episode for an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun getEpisodeForAssessment(
    @Parameter(
      description = "Assessment ID",
      required = true,
      example = "1234"
    ) @PathVariable assessmentUuid: UUID,
    @Parameter(
      description = "Episode ID",
      required = true,
      example = "1234"
    ) @PathVariable episodeUuid: UUID
  ): AssessmentEpisodeDto {
    return AssessmentEpisodeDto.from(assessmentService.getEpisode(assessmentUuid, episodeUuid))
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
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun updateAssessmentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode UUID", required = true) @PathVariable episodeUuid: UUID,
    @Parameter(description = "Episode Answers", required = true) @RequestBody episodeAnswers: UpdateAssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    val episode = assessmentService.getEpisode(assessmentUuid, episodeUuid)
    return updateResponse(assessmentUpdateService.updateEpisode(episode, episodeAnswers))
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes/{episodeUuid}/complete"], method = [RequestMethod.POST])
  @Operation(description = "completes the episode")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The update couldn't be processed")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun completeAssessmentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode UUID", required = true) @PathVariable episodeUuid: UUID,
  ): ResponseEntity<AssessmentEpisodeDto> {
    val episode = assessmentService.getEpisode(assessmentUuid, episodeUuid)
    return updateResponse(assessmentUpdateService.completeEpisode(episode))
  }

  private fun updateResponse(
    assessmentEpisode: AssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    return ResponseEntity(assessmentEpisode, HttpStatus.OK)
  }
}
