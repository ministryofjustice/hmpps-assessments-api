package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.assessments.restclient.communityapi.GetOffenderDto
import uk.gov.justice.digital.assessments.services.OffenderService

@RestController
class OffenderController(val offenderService: OffenderService) {

    @RequestMapping(path = ["/offender/crn/{crn}"], method = [RequestMethod.GET])
    @Operation(description = "Gets offender information")
    @ApiResponses(value = [
        ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
        ApiResponse(responseCode = "200", description = "OK")
    ])
    fun getAssessmentSubject(@Parameter(description = "Assessment UUID", required = true) @PathVariable crn: String): GetOffenderDto? {
        return offenderService.getOffender(crn)
    }
}