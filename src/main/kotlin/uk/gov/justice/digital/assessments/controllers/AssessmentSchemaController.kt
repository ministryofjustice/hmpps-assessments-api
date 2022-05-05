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
import uk.gov.justice.digital.assessments.api.GroupContentDto
import uk.gov.justice.digital.assessments.api.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.services.AssessmentReferenceDataService
import uk.gov.justice.digital.assessments.services.AssessmentService
import uk.gov.justice.digital.assessments.services.AssessmentUpdateService

@RestController
class AssessmentSchemaController(
  val assessmentService: AssessmentService,
  val assessmentUpdateService: AssessmentUpdateService,
  val assessmentReferenceDataService: AssessmentReferenceDataService
) {
  @RequestMapping(path = ["/assessments/schema/{assessmentSchemaCode}"], method = [RequestMethod.GET])
  @Operation(description = "Gets an assessment schema with all questions in the assessment schema group")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun getAssessmentSchema(
    @Parameter(
      description = "Assessment Schema Code",
      required = true,
      example = "ROSH"
    ) @PathVariable assessmentSchemaCode: AssessmentSchemaCode
  ): GroupWithContentsDto {
    return assessmentReferenceDataService.getAssessmentSchema(assessmentSchemaCode)
  }

  @RequestMapping(path = ["/assessments/schema/{assessmentSchemaCode}/summary"], method = [RequestMethod.GET])
  @Operation(description = "Gets Summary information for an assessment schema")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun getAssessmentSchemaSummary(
    @Parameter(
      description = "Assessment Schema Code",
      required = true,
      example = "ROSH"
    ) @PathVariable assessmentSchemaCode: AssessmentSchemaCode
  ): GroupSectionsDto {
    return assessmentReferenceDataService.getAssessmentSchemaSummary(assessmentSchemaCode)
  }

  @RequestMapping(path = ["/assessments/schema/{assessmentSchemaCode}/questions"], method = [RequestMethod.GET])
  @Operation(description = "Gets questions for Assessment Schema code")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "404", description = "Questions not found for Assessment Schema code"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun getQuestionsForAssessmentSchemaCode(@PathVariable("assessmentSchemaCode") assessmentSchemaCode: String): List<GroupContentDto> {
    return assessmentReferenceDataService.getQuestionsForSchemaCode(AssessmentSchemaCode.valueOf(assessmentSchemaCode))
  }
}
