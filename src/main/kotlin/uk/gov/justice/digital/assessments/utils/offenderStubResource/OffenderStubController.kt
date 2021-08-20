package uk.gov.justice.digital.assessments.utils.offenderStubResource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@Profile("dev", "test")
class OffenderStubController(val offenderStubService: OffenderStubService) {

  @RequestMapping(path = ["/offender/stub"], method = [RequestMethod.GET])
  @Operation(description = "Creates a stub in oasys for a delius offender")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun createNewStub(): OffenderStubDto {
    return offenderStubService.createStub()
  }
}
