package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
import uk.gov.justice.digital.assessments.api.CreateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.GroupContentDto
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
  @PreAuthorize("hasRole('ROLE_PROBATION')")
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
  @PreAuthorize("hasRole('ROLE_PROBATION')")
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
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun createNewAssessmentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(
      description = "Reason for the new Episode of Change",
      required = true
    ) @RequestBody createAssessmentEpisodeDto: CreateAssessmentEpisodeDto
  ): AssessmentEpisodeDto? {
    return assessmentService.createNewEpisode(
      assessmentUuid,
      createAssessmentEpisodeDto.eventID,
      createAssessmentEpisodeDto.changeReason,
      createAssessmentEpisodeDto.assessmentSchemaCode,
      createAssessmentEpisodeDto.deliusEventType
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
  @PreAuthorize("hasRole('ROLE_PROBATION')")
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

  @RequestMapping(
    path = ["/assessments/{assessmentUuid}/episodes/current/table/{tableName}"],
    method = [RequestMethod.POST]
  )
  @Operation(description = "creates an entry to a table for a given assessment episode")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The table couldn't be processed")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun addTableEntryForCurrentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Table name", required = true) @PathVariable tableName: String,
    @Parameter(description = "New Row", required = true) @RequestBody tableEntry: UpdateAssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentUpdateService.addEntryToTableForCurrentEpisode(assessmentUuid, tableName, tableEntry)
    )
  }

  @RequestMapping(
    path = ["/assessments/{assessmentUuid}/episodes/current/table/{tableName}/{index}"],
    method = [RequestMethod.PUT]
  )
  @Operation(description = "updates an entry to a table for a given assessment episode")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The table couldn't be processed")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun updateTableEntryForCurrentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Table name", required = true) @PathVariable tableName: String,
    @Parameter(description = "Index", required = true) @PathVariable index: Int,
    @Parameter(description = "Updated values", required = true) @RequestBody requestBody: UpdateAssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentUpdateService.updateEntryToTableForCurrentEpisode(assessmentUuid, tableName, requestBody, index)
    )
  }

  @RequestMapping(
    path = ["/assessments/{assessmentUuid}/episodes/current/table/{tableName}/{index}"],
    method = [RequestMethod.DELETE]
  )
  @Operation(description = "removes an entry to a table for a given assessment episode")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The table couldn't be processed")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun deleteTableEntryForCurrentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Table name", required = true) @PathVariable tableName: String,
    @Parameter(description = "Index", required = true) @PathVariable index: Int,
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentUpdateService.deleteEntryToTableForCurrentEpisode(assessmentUuid, tableName, index)
    )
  }

  @RequestMapping(
    path = ["/assessments/{assessmentUuid}/episodes/{episodeUuid}/table/{tableName}"],
    method = [RequestMethod.POST]
  )
  @Operation(description = "creates an entry to a table for a given assessment episode")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The table couldn't be processed")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun addTableEntryForEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode UUID", required = true) @PathVariable episodeUuid: UUID,
    @Parameter(description = "Table name", required = true) @PathVariable tableName: String,
    @Parameter(description = "New Row", required = true) @RequestBody tableEntry: UpdateAssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentUpdateService.addEntryToTableForEpisode(assessmentUuid, episodeUuid, tableName, tableEntry)
    )
  }

  @RequestMapping(
    path = ["/assessments/{assessmentUuid}/episodes/{episodeUuid}/table/{tableName}/{index}"],
    method = [RequestMethod.PUT]
  )
  @Operation(description = "updates an entry to a table for a given assessment episode")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The table couldn't be processed")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun updateTableEntryForEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode UUID", required = true) @PathVariable episodeUuid: UUID,
    @Parameter(description = "Table name", required = true) @PathVariable tableName: String,
    @Parameter(description = "Index", required = true) @PathVariable index: Int,
    @Parameter(description = "Updated values", required = true) @RequestBody requestBody: UpdateAssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentUpdateService.updateEntryToTableForEpisode(assessmentUuid, episodeUuid, tableName, requestBody, index)
    )
  }

  @RequestMapping(
    path = ["/assessments/{assessmentUuid}/episodes/{episodeUuid}/table/{tableName}/{index}"],
    method = [RequestMethod.DELETE]
  )
  @Operation(description = "removes an entry to a table for a given assessment episode")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OK"),
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "422", description = "The table couldn't be processed")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun deleteTableEntryForEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode UUID", required = true) @PathVariable episodeUuid: UUID,
    @Parameter(description = "Table name", required = true) @PathVariable tableName: String,
    @Parameter(description = "Index", required = true) @PathVariable index: Int,
  ): ResponseEntity<AssessmentEpisodeDto> {
    return updateResponse(
      assessmentUpdateService.deleteEntryToTableForEpisode(assessmentUuid, episodeUuid, tableName, index)
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
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun updateAssessmentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
    @Parameter(description = "Episode Answers", required = true) @RequestBody episodeAnswers: UpdateAssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    val currentEpisode = assessmentService.getCurrentEpisode(assessmentUuid)

    return updateResponse(
      assessmentUpdateService.updateCurrentEpisode(currentEpisode, episodeAnswers)
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
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun completeAssessmentEpisode(
    @Parameter(description = "Assessment UUID", required = true, example = "1234") @PathVariable assessmentUuid: UUID,
  ): ResponseEntity<AssessmentEpisodeDto> {
    val currentEpisode = assessmentService.getCurrentEpisode(assessmentUuid)
    return updateResponse(assessmentUpdateService.closeEpisode(currentEpisode))
  }

  @RequestMapping(path = ["/assessments/schema/{assessmentSchemaCode}"], method = [RequestMethod.GET])
  @Operation(description = "Gets an assessment schema with all questions in the assessment schema group")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
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
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun getAssessmentSchemaSummary(
    @Parameter(
      description = "Assessment Schema Code",
      required = true,
      example = "ROSH"
    ) @PathVariable assessmentSchemaCode: AssessmentSchemaCode
  ): GroupSectionsDto {
    return assessmentSchemaService.getAssessmentSchemaSummary(assessmentSchemaCode)
  }

  @RequestMapping(path = ["/assessments/schema/{assessmentSchemaCode}/questions"], method = [RequestMethod.GET])
  @Operation(description = "Gets questions for Assessment Schema code")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "404", description = "Questions not found for Assessment Schema code"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun getQuestionsForAssessmentSchemaCode(@PathVariable("assessmentSchemaCode") assessmentSchemaCode: String): List<GroupContentDto> {
    return assessmentSchemaService.getQuestionsForSchemaCode(AssessmentSchemaCode.valueOf(assessmentSchemaCode))
  }

  private fun updateResponse(
    assessmentEpisode: AssessmentEpisodeDto
  ): ResponseEntity<AssessmentEpisodeDto> {
    val code = if (assessmentEpisode.errors == null) HttpStatus.OK else HttpStatus.UNPROCESSABLE_ENTITY
    return ResponseEntity(assessmentEpisode, code)
  }
}
