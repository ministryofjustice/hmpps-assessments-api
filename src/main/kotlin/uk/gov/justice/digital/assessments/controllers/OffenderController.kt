package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.services.OffenderService

@RestController
class OffenderController(val offenderService: OffenderService) {

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @RequestMapping(
    path = [
      "/offender/crn/{crn}/eventId/{eventId}",
    ],
    method = [RequestMethod.GET],
  )
  @Operation(description = "Get offender information")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun getOffenderDetails(
    @Parameter(description = "Offender CRN", required = true) @PathVariable crn: String,
    @Parameter(description = "Delius Event ID", required = true) @PathVariable eventId: Long,
  ): OffenderDto? {
    log.debug("Entered getOffenderDetails($crn, $eventId)")

    return offenderService.getOffender(crn, eventId)
  }
}
