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
import uk.gov.justice.digital.assessments.api.groups.GroupContentDto
import uk.gov.justice.digital.assessments.api.groups.GroupSectionsDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.services.AssessmentReferenceDataService

@RestController
class AssessmentReferenceDataController(
  val assessmentReferenceDataService: AssessmentReferenceDataService,
) {
  @RequestMapping(path = ["/assessments/{assessmentType}/summary"], method = [RequestMethod.GET])
  @Operation(description = "Gets Summary information for an assessment type")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun getAssessmentSummary(
    @Parameter(
      description = "Assessment Type Code",
      required = true,
      example = "ROSH",
    ) @PathVariable assessmentType: AssessmentType,
  ): GroupSectionsDto {
    return assessmentReferenceDataService.getAssessmentSummary(assessmentType)
  }

  @RequestMapping(path = ["/assessments/{assessmentType}/questions"], method = [RequestMethod.GET])
  @Operation(description = "Retrieve questions for Assessment type")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "404", description = "Questions not found for Assessment type"),
      ApiResponse(responseCode = "200", description = "OK"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_PROBATION')")
  fun getQuestionsForAssessmentSchemaCode(@PathVariable("assessmentType") assessmentType: String): List<GroupContentDto> {
    return assessmentReferenceDataService.getQuestionsForAssessmentType(AssessmentType.valueOf(assessmentType))
  }
}
