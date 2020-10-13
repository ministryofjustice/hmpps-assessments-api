package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.assessments.api.GroupSummaryDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.services.QuestionService
import java.util.*

@RestController
class QuestionController(val questionService: QuestionService ) {

    @RequestMapping(path = ["/questions/id/{questionSchemaId}"], method = [RequestMethod.GET])
    @Operation(description = "Gets a Question Schema by its ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "404", description = "Question not found "),
        ApiResponse(responseCode = "200", description = "OK")])
    fun getQuestionSchema(@PathVariable("questionSchemaId") questionSchemaUUId: UUID): QuestionSchemaDto {
        return questionService.getQuestionSchema(questionSchemaUUId)
    }

    @RequestMapping(path = ["/questions/list"], method = [RequestMethod.GET])
    @Operation(description = "Lists available question groups")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OK")])
    fun listQuestionGroups(): Collection<GroupSummaryDto> {
        return questionService.listGroups()
    }

    @RequestMapping(path = ["/questions/{groupUuid}"], method = [RequestMethod.GET])
    @Operation(description = "Gets Questions for a Group")
    @ApiResponses(value = [
        ApiResponse(responseCode = "404", description = "Questions not found for Group"),
        ApiResponse(responseCode = "200", description = "OK")])
    fun getQuestionsForGroup(@PathVariable("groupUuid") groupId: UUID): GroupWithContentsDto {
        return questionService.getQuestionGroup(groupId)
    }
}
