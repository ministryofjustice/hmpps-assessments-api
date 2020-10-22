package uk.gov.justice.digital.needs.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.*
import uk.gov.justice.digital.needs.api.CalculateNeedsDto
import uk.gov.justice.digital.needs.api.CriminogenicNeedsDto
import uk.gov.justice.digital.needs.services.CriminogenicNeedsService

@RestController
class CriminogenicNeedsController(val criminogenicNeedsService: CriminogenicNeedsService) {


// The needs service should accept a map of questions and answers and return a list of Criminogenic Needs it is able to calculate.
//

    @RequestMapping(path = ["/needs/{supervisionId}"], method = [RequestMethod.POST])
    @Operation(description = "Calculates needs for a supervision")
    @ApiResponses(value = [
        ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
        ApiResponse(responseCode = "200", description = "OK")
    ])
    fun calculateNeeds(@Parameter(description = "Supervision Id", required = true) @PathVariable supervisionId: String,
                       @Parameter(description = "Questions and answers for calculation", required = true) @RequestBody calculateNeedsDto : CalculateNeedsDto): CriminogenicNeedsDto {
        return criminogenicNeedsService.calculateNeeds(calculateNeedsDto)
    }
}