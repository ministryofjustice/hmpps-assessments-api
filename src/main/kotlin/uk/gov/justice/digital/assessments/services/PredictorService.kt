package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AnswersDto
import uk.gov.justice.digital.assessments.api.PredictorScoreDto
import uk.gov.justice.digital.assessments.jpa.entities.Answer
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.PredictorFieldMapping
import uk.gov.justice.digital.assessments.jpa.entities.PredictorType
import uk.gov.justice.digital.assessments.restclient.AssessRisksAndNeedsApiRestClient
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.CurrentOffence
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.CurrentOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.DynamicScoringOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.EmploymentType
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.Gender
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.OffenderAndOffencesDto
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.PreviousOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.ProblemsLevel
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Service
class PredictorService(
  private val assessmentSchemaService: AssessmentSchemaService,
  private val assessRisksAndNeedsApiRestClient: AssessRisksAndNeedsApiRestClient
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun getPredictorResults(
    assessmentSchemaCode: AssessmentSchemaCode,
    episode: AssessmentEpisodeEntity,
  ): List<PredictorScoreDto> {
    val predictors = assessmentSchemaService.getPredictorsForAssessment(assessmentSchemaCode)

    log.info("Found ${predictors.size} predictors for episode ${episode.episodeUuid} with assessment type $assessmentSchemaCode")

    return predictors.map { predictor ->
      val predictorFields = predictor.fields.toList()
      val extractedAnswers = extractAnswers(predictorFields, episode.answers.orEmpty())
      if (predictorFields.isNotEmpty() && predictorFields.size == extractedAnswers.size)
        fetchResults(predictor.type, extractedAnswers) else PredictorScoreDto.incomplete(predictor.type)
    }
  }

  private fun extractAnswers(
    predictorFields: List<PredictorFieldMapping>,
    answers: Map<UUID, AnswerEntity>
  ): Map<String, AnswersDto> {
    return predictorFields
      .associate { predictorField ->
        val questionUuid = predictorField.questionSchema.questionSchemaUuid
        val questionAnswer = answers[questionUuid]
        predictorField.predictorFieldName to questionAnswer?.answers
      }
      .filterValues { it != null && it.isNotEmpty() }
      .mapValues {
        AnswersDto.from(it.value as Collection<Answer>)
      }
      .filterValues { answersDto -> answersDto.answers.flatMap { answerDto -> answerDto.items }.isNotEmpty() }
  }

  private fun fetchResults(
    predictorType: PredictorType,
    answers: Map<String, AnswersDto>,
  ): PredictorScoreDto {
    log.info("Stubbed call to get Predictor Score")
    val offenderAndOffencesDto = OffenderAndOffencesDto(
      crn = "X1345",
      gender = Gender.MALE,
      dob = LocalDate.of(2021, 1, 1).minusYears(20),
      assessmentDate = LocalDateTime.of(2021, 1, 1, 0, 0, 0),
      currentOffence = CurrentOffence("138", "00"),
      dateOfFirstSanction = LocalDate.of(2021, 1, 1).minusYears(1),
      totalOffences = 10,
      totalViolentOffences = 8,
      dateOfCurrentConviction = LocalDate.of(2021, 1, 1).minusWeeks(2),
      hasAnySexualOffences = true,
      isCurrentSexualOffence = true,
      isCurrentOffenceVictimStranger = true,
      mostRecentSexualOffenceDate = LocalDate.of(2021, 1, 1).minusWeeks(3),
      totalSexualOffencesInvolvingAnAdult = 5,
      totalSexualOffencesInvolvingAChild = 3,
      totalSexualOffencesInvolvingChildImages = 2,
      totalNonSexualOffences = 2,
      earliestReleaseDate = LocalDate.of(2021, 1, 1).plusMonths(10),
      hasCompletedInterview = true,
      dynamicScoringOffences = DynamicScoringOffences(
        committedOffenceUsingWeapon = true,
        hasSuitableAccommodation = ProblemsLevel.MISSING,
        employment = EmploymentType.NOT_AVAILABLE_FOR_WORK,
        currentRelationshipWithPartner = ProblemsLevel.SIGNIFICANT_PROBLEMS,
        evidenceOfDomesticViolence = true,
        isAVictim = true,
        isAPerpetrator = true,
        alcoholUseIssues = ProblemsLevel.SIGNIFICANT_PROBLEMS,
        bingeDrinkingIssues = ProblemsLevel.SIGNIFICANT_PROBLEMS,
        impulsivityIssues = ProblemsLevel.SOME_PROBLEMS,
        temperControlIssues = ProblemsLevel.SIGNIFICANT_PROBLEMS,
        proCriminalAttitudes = ProblemsLevel.SOME_PROBLEMS,
        previousOffences = PreviousOffences(
          murderAttempt = true,
          wounding = true,
          aggravatedBurglary = true,
          arson = true,
          criminalDamage = true,
          kidnapping = true,
          firearmPossession = true,
          robbery = true,
          offencesWithWeapon = true
        ),
        currentOffences = CurrentOffences(
          firearmPossession = true,
          offencesWithWeapon = true
        )
      )
    )
    val riskPredictors = assessRisksAndNeedsApiRestClient.getRiskPredictors(PredictorType.RSR, offenderAndOffencesDto)
    log.info("Risk Predictors from ARN $riskPredictors")
    return PredictorScoreDto(
      type = predictorType,
      score = 1234
    )
  }
}
