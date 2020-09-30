package uk.gov.justice.digital.assessments.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.assessments.api.QuestionGroupDto
import uk.gov.justice.digital.assessments.api.QuestionSchemaDto
import uk.gov.justice.digital.assessments.services.QuestionService
import java.util.*

@RestController
@Api(value = "Question Reference resources", tags = ["Question"])
class QuestionController(val questionService: QuestionService ) {

    @RequestMapping(path = ["/questions/id/{questionSchemaId}"], method = [RequestMethod.GET])
    @ApiOperation(value = "Gets a Question Schema by its ID")
    @ApiResponses(ApiResponse(code = 404, message = "Question not found "), ApiResponse(code = 200, message = "OK"))
    fun getQuestionSchema(@PathVariable("questionSchemaId") questionSchemaUUId: UUID): QuestionSchemaDto {
        return questionService.getQuestionSchema(questionSchemaUUId)
    }


    @RequestMapping(path = ["/questions/{groupUuid}"], method = [RequestMethod.GET])
    @ApiOperation(value = "Gets Questions for a Group")
    @ApiResponses(ApiResponse(code = 404, message = "Questions not found for Group"), ApiResponse(code = 200, message = "OK"))
    fun getQuestionsForGroup(@PathVariable("groupUuid") groupId: UUID): QuestionGroupDto {
        return questionService.getQuestionGroups(groupId)
    }
}
