package uk.gov.justice.digital.assessments.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.assessments.api.PredictorScoresDto
import uk.gov.justice.digital.assessments.services.RiskPredictorsService
import java.util.UUID

@RestController
class RiskPredictorsController(val riskPredictorsService: RiskPredictorsService) {

  @RequestMapping(path = ["/risks/predictors/episodes/{episodeUuid}"], method = [RequestMethod.GET])
  @Operation(description = "Gets risk predictors for a specific predictor type")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "403", description = "Unauthorized"),
      ApiResponse(responseCode = "200", description = "OK")
    ]
  )
  fun calculateRiskPredictorsForEpisode(
    @Parameter(description = "Episode UUID", required = true, example = "90f2b674-ae1c-488d-8b85-0251708ef6b6")
    @PathVariable episodeUuid: UUID,
    @RequestParam(value = "final", required = false) final: Boolean = false,
  ): List<PredictorScoresDto> {
    log.info("Calculate predictors for parameters final:$final episodeUuid:$episodeUuid")
    return riskPredictorsService.getPredictorResults(
      episodeUuid,
      final
    )
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
