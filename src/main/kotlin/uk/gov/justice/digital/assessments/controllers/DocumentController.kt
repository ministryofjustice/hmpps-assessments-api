package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.assessments.api.UploadedUpwDocumentDto
import uk.gov.justice.digital.assessments.services.DocumentService
import java.util.UUID

@Controller
class DocumentController(val documentService: DocumentService) {

  @RequestMapping(
    path = ["/assessments/{assessmentId}/episode/{episodeId}/document"], method = [RequestMethod.POST],
    consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
  )
  @Operation(description = "Send an Unpaid Work pdf to Delius")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "401", description = "Invalid JWT Token"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  @PreAuthorize("hasRole('ROLE_COMMUNITY')")
  fun uploadUPWDocument(
    @RequestParam("fileData") fileData: MultipartFile,
    @PathVariable episodeId: UUID,
    @PathVariable assessmentId: UUID
  ): ResponseEntity<UploadedUpwDocumentDto> {
    val responseBody = documentService.uploadUpwDocument(assessmentId, episodeId, fileData)!!
    return ResponseEntity(responseBody, HttpStatus.OK)
  }
}
