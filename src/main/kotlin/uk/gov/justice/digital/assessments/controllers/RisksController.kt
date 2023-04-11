package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.assessments.api.RegistrationsDto
import uk.gov.justice.digital.assessments.api.RoshRiskSummaryDto
import uk.gov.justice.digital.assessments.services.RisksService

@RestController
class RisksController(
  val risksService: RisksService,
) {
  @RequestMapping(path = ["/assessments/{crn}/event/{eventId}/registrations"], method = [RequestMethod.GET])
  @Operation(description = "Get Mappa and risk flags for the assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK"),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_PROBATION','ROLE_ARN_READ_ONLY')")
  fun getRegistrationsForAssessment(
    @Parameter(
      description = "CRN",
      required = true,
    ) @PathVariable eventId: Long,
    @Parameter(
      description = "CRN",
      required = true,
    ) @PathVariable crn: String,
  ): RegistrationsDto {
    return risksService.getRegistrationsForAssessment(crn, eventId)
  }

  @RequestMapping(path = ["/assessments/{crn}/ROSH/summary"], method = [RequestMethod.GET])
  @Operation(description = "Get ROSH risk summary for the assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK"),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_PROBATION','ROLE_ARN_READ_ONLY')")
  fun getRoshRiskSummaryForAssessment(
    @Parameter(
      description = "CRN",
      required = true,
    ) @PathVariable crn: String,
  ): RoshRiskSummaryDto {
    return risksService.getRoshRiskSummaryForAssessment(crn)
  }
}
