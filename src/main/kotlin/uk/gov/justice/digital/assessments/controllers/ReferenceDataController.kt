package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.assessments.api.FilteredReferenceDataRequest
import uk.gov.justice.digital.assessments.restclient.assessmentapi.RefElementDto
import uk.gov.justice.digital.assessments.services.ReferenceDataService

@RestController
class ReferenceDataController(val referenceDataService: ReferenceDataService) {

  @RequestMapping(path = ["/referenceData/filtered"], method = [RequestMethod.POST])
  @Operation(description = "Gets filtered reference data for a field")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun getFilteredReferenceData(@Parameter(description = "Supervision Id", required = true) @RequestBody request: FilteredReferenceDataRequest): Map<String, Collection<RefElementDto>> {
    return referenceDataService.getFilteredReferenceData(
      request.assessmentUuid,
      request.episodeUuid,
      request.fieldName,
      request.parentList,
    )
  }
}
