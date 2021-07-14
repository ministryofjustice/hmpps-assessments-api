package uk.gov.justice.digital.assessments.utils.offenderStubResource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class OffenderStubController(val offenderStubService: OffenderStubService) {

  @RequestMapping(path = ["/offender/stub"], method = [RequestMethod.POST])
  @Operation(description = "Creates a stub in oasys for a delius offender")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun createNewStub():Any? {
    return offenderStubService.createStub()
  }
}