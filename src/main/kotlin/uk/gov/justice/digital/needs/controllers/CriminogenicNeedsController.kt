package uk.gov.justice.digital.needs.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.*
import uk.gov.justice.digital.needs.api.CriminogenicNeedsDto
import uk.gov.justice.digital.needs.services.CriminogenicNeedsService
import java.util.UUID

@RestController
class CriminogenicNeedsController(val criminogenicNeedsService: CriminogenicNeedsService) {

    @RequestMapping(path = ["/assessments/{assessmentUuid}/needs"], method = [RequestMethod.GET])
    @Operation(description = "Calculates needs for an assessment")
    @ApiResponses(value = [
        ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
        ApiResponse(responseCode = "200", description = "OK")
    ])
    fun calculateNeeds(@Parameter(description = "Assessment UUID", required = true) @PathVariable assessmentUuid: UUID): CriminogenicNeedsDto {
        return criminogenicNeedsService.calculateNeeds(assessmentUuid)
    }
}