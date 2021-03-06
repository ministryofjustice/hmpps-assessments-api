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
import uk.gov.justice.digital.assessments.api.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.services.AssessmentSchemaService
import uk.gov.justice.digital.assessments.services.AssessmentService
import uk.gov.justice.digital.assessments.services.AssessmentUpdateService
import java.util.UUID

@RestController
class AssessmentController(
  val assessmentService: AssessmentService,
  val assessmentUpdateService: AssessmentUpdateService,
  val assessmentSchemaService: AssessmentSchemaService
) {

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
  fun getAssessmentSubject(
    @Parameter(
      description = "Assessment UUID",
      required = true
    ) @PathVariable assessmentUuid: UUID
  ): AssessmentSubjectDto {
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
    @Parameter(
      description = "Reason for the new Episode of Change",
      required = true
    ) @RequestBody createAssessmentEpisodeDto: CreateAssessmentEpisodeDto
  ): AssessmentEpisodeDto? {
    return assessmentService.createNewEpisode(
      assessmentUuid,
      createAssessmentEpisodeDto.changeReason,
      createAssessmentEpisodeDto.assessmentSchemaCode
    )
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes"], method = [RequestMethod.GET])
  @Operation(description = "Get all the episodes for an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun getAllEpisodesForAssessment(
    @Parameter(
      description = "Assessment UUID",
      required = true,
      example = "1234"
    ) @PathVariable assessmentUuid: UUID
  ): Collection<AssessmentEpisodeDto>? {
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
  fun getCurrentEpisodeForAssessment(
    @Parameter(
      description = "Assessment ID",
      required = true,
      example = "1234"
    ) @PathVariable assessmentUuid: UUID
  ): AssessmentEpisodeDto {
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
      assessmentUpdateService.updateEpisode(assessmentUuid, episodeUuid, episodeAnswers)
    )
  }

  @RequestMapping(
    path = ["/assessments/{assessmentUuid}/episodes/{episodeUuid}/{tableName}"],
    method = [RequestMethod.POST]
  )
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
      assessmentUpdateService.addEpisodeTableRow(assessmentUuid, episodeUuid, tableName, episodeAnswers)
    )
  }

  @RequestMapping(path = ["/assessments/{assessmentUuid}/episodes/current/{tableName}"], method = [RequestMethod.POST])
  @Operation(description = "adds a row to a table answer")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The update couldn't be processed")
    ]
  )
  fun addAssessmentCurrentEpisodeTableRow(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Table Name", required = true) @PathVariable tableName: String,
    @Parameter(description = "New Row", required = true) @RequestBody episodeAnswers: UpdateAssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentUpdateService.addCurrentEpisodeTableRow(assessmentUuid, tableName, episodeAnswers)
    )
  }

  @RequestMapping(
    path = ["/assessments/{assessmentUuid}/episodes/{episodeUuid}/{tableName}/{index}"],
    method = [RequestMethod.POST]
  )
  @Operation(description = "update a row in a table answer")
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
      assessmentUpdateService.updateEpisodeTableRow(assessmentUuid, episodeUuid, tableName, index, episodeAnswers)
    )
  }

  @RequestMapping(
    path = ["/assessments/{assessmentUuid}/episodes/current/{tableName}/{index}"],
    method = [RequestMethod.POST]
  )
  @Operation(description = "updates a row in a table answer")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The update couldn't be processed")
    ]
  )
  fun updateAssessmentCurrentEpisodeTableRow(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Table Name", required = true) @PathVariable tableName: String,
    @Parameter(description = "Row index", required = true) @PathVariable index: Int,
    @Parameter(description = "Updated Row", required = true) @RequestBody episodeAnswers: UpdateAssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentUpdateService.updateCurrentEpisodeTableRow(assessmentUuid, tableName, index, episodeAnswers)
    )
  }

  @RequestMapping(
    path = ["/assessments/{assessmentUuid}/episodes/{episodeUuid}/{tableName}/{index}"],
    method = [RequestMethod.DELETE]
  )
  @Operation(description = "remove a row from a table answer")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The update couldn't be processed")
    ]
  )
  fun deleteAssessmentEpisodeTableRow(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode UUID", required = true) @PathVariable episodeUuid: UUID,
    @Parameter(description = "Table Name", required = true) @PathVariable tableName: String,
    @Parameter(description = "Row index", required = true) @PathVariable index: Int
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentUpdateService.deleteEpisodeTableRow(assessmentUuid, episodeUuid, tableName, index)
    )
  }

  @RequestMapping(
    path = ["/assessments/{assessmentUuid}/episodes/current/{tableName}/{index}"],
    method = [RequestMethod.DELETE]
  )
  @Operation(description = "removes a row in a table answer in the current episode")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The update couldn't be processed")
    ]
  )
  fun deleteAssessmentCurrentEpisodeTableRow(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Table Name", required = true) @PathVariable tableName: String,
    @Parameter(description = "Row index", required = true) @PathVariable index: Int
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentUpdateService.deleteCurrentEpisodeTableRow(assessmentUuid, tableName, index)
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
      assessmentUpdateService.updateCurrentEpisode(assessmentUuid, episodeAnswers)
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
      assessmentUpdateService.closeCurrentEpisode(assessmentUuid)
    )
  }

  @RequestMapping(path = ["/assessments/schema/{assessmentSchemaCode}"], method = [RequestMethod.GET])
  @Operation(description = "Gets an assessment schema with all questions in the assessment schema group")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun getAssessmentSchema(
    @Parameter(
      description = "Assessment Schema Code",
      required = true,
      example = "ROSH"
    ) @PathVariable assessmentSchemaCode: AssessmentSchemaCode
  ): GroupWithContentsDto {
    return assessmentSchemaService.getAssessmentSchema(assessmentSchemaCode)
  }

  @RequestMapping(path = ["/assessments/schema/{assessmentSchemaCode}/summary"], method = [RequestMethod.GET])
  @Operation(description = "Gets Summary information for an assessment schema")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun getAssessmentSchemaSummary(
    @Parameter(
      description = "Assessment Schema Code",
      required = true,
      example = "ROSH"
    ) @PathVariable assessmentSchemaCode: AssessmentSchemaCode
  ): GroupSectionsDto {
    return assessmentSchemaService.getAssessmentSchemaSummary(assessmentSchemaCode)
  }

  private fun updateResponse(
    assessmentEpisode: AssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    val code = if (assessmentEpisode.errors == null) HttpStatus.OK else HttpStatus.UNPROCESSABLE_ENTITY
    return ResponseEntity(assessmentEpisode, code)
  }
}
