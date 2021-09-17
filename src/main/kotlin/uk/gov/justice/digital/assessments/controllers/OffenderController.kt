package uk.gov.justice.digital.assessments.controllers

// TODO from ARN-618: Fix Offender service

//
// import io.swagger.v3.oas.annotations.Operation
// import io.swagger.v3.oas.annotations.Parameter
// import io.swagger.v3.oas.annotations.responses.ApiResponse
// import io.swagger.v3.oas.annotations.responses.ApiResponses
// import org.springframework.web.bind.annotation.PathVariable
// import org.springframework.web.bind.annotation.RequestMapping
// import org.springframework.web.bind.annotation.RequestMethod
// import org.springframework.web.bind.annotation.RestController
// import uk.gov.justice.digital.assessments.api.OffenderDto
// import uk.gov.justice.digital.assessments.services.OffenderService
//
// @RestController
// class OffenderController(val offenderService: OffenderService) {
//
//   @RequestMapping(path = ["/offender/crn/{crn}/eventId/{eventId}"], method = [RequestMethod.GET])
//   @Operation(description = "Gets offender and offence information")
//   @ApiResponses(
//     value = [
//       ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
//       ApiResponse(responseCode = "200", description = "OK")
//     ]
//   )
//   fun getOffenderAndOffence(
//     @Parameter(description = "Offender CRN", required = true) @PathVariable crn: String,
//     @Parameter(description = "Delius Event ID", required = true) @PathVariable eventId: Long
//   ): OffenderDto? {
//     return offenderService.getOffenderAndOffence(crn, eventId)
//   }
// }
