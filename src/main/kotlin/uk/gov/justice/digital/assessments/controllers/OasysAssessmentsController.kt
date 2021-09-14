package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.assessments.api.OasysAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.services.OasysAssessmentService

@RestController
class OasysAssessmentsController(val oasysAssessmentService: OasysAssessmentService) {

  @RequestMapping(
    path = ["/subject/{crn}/assessments/episodes/{assessmentSchemaCode}/current"],
    method = [RequestMethod.GET]
  )
  @Operation(description = "Gets current assessment episode with oasys formatted answers for a specific schema type")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "403", description = "Unauthorized"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun getCurrentAssessmentEpisodeForAssessmentType(
    @Parameter(description = "crn", required = true, example = "D19873")
    @PathVariable crn: String,
    @Parameter(
      description = "Assessment Schema Code",
      required = true,
      example = "RSR"
    ) @PathVariable assessmentSchemaCode: AssessmentSchemaCode
  ): OasysAssessmentEpisodeDto {
    log.info("Get current episode for an Offender with crn:$crn and assessment schema code:$assessmentSchemaCode")
    return oasysAssessmentService.getLatestEpisodeOfTypeForSubjectWithCrn(assessmentSchemaCode, crn)
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

}