package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AnswersDto
import uk.gov.justice.digital.assessments.api.PredictorScoresDto
import uk.gov.justice.digital.assessments.jpa.entities.Answer
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.PredictorFieldMapping
import uk.gov.justice.digital.assessments.services.dto.PredictorType
import uk.gov.justice.digital.assessments.restclient.AssessRisksAndNeedsApiRestClient
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.CurrentOffence
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.Gender
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.OffenderAndOffencesDto
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.RiskPredictorsDto
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.PredictorCalculationException
import java.time.LocalDateTime
import java.util.UUID

@Service
class PredictorService(
  private val assessmentSchemaService: AssessmentSchemaService,
  private val subjectService: SubjectService,
  private val assessRisksAndNeedsApiRestClient: AssessRisksAndNeedsApiRestClient
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun getPredictorResults(
    assessmentSchemaCode: AssessmentSchemaCode,
    episode: AssessmentEpisodeEntity,
  ): List<PredictorScoresDto> {
    val predictors = assessmentSchemaService.getPredictorsForAssessment(assessmentSchemaCode)

    log.info("Found ${predictors.size} predictors for episode ${episode.episodeUuid} with assessment type $assessmentSchemaCode")

    return predictors.map { predictor ->
        fetchResults(episode, predictor.type, extractAnswers(predictor.fields.toList(), episode.answers.orEmpty()),)
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
    episode: AssessmentEpisodeEntity,
    predictorType: PredictorType,
    answers: Map<String, AnswersDto>,
  ): PredictorScoresDto {
    val crn = "X1345"
    log.info("Getting Predictor Score for crn $crn")
    val dateOfFirstSanction = getAnswerFor(answers, "date_first_sanction")
    val totalOffences = getAnswerFor(answers, "total_sanctions")
    val totalViolentOffences = getAnswerFor(answers, "total_violent_offences")
    val dateOfCurrentConviction = getAnswerFor(answers, "date_current_conviction")
    val hasAnySexualOffences = getAnswerFor(answers, "any_sexual_offences")
    val isCurrentSexualOffence = getAnswerFor(answers, "current_sexual_offence")
    val isCurrentOffenceVictimStranger = getAnswerFor(answers, "current_offence_victim_stranger")
    val mostRecentSexualOffenceDate = getAnswerFor(answers, "most_recent_sexual_offence_date")
    val totalSexualOffencesInvolvingAnAdult = getAnswerFor(answers, "total_sexual_offences_adult")
    val totalSexualOffencesInvolvingAChild = getAnswerFor(answers, "total_sexual_offences_child")
    val totalSexualOffencesInvolvingChildImages = getAnswerFor(answers, "total_sexual_offences_child_image")
    val totalNonSexualOffences = getAnswerFor(answers, "total_non_sexual_offences")
    val earliestReleaseDate = getAnswerFor(answers, "earliest_release_date")
    val hasCompletedInterview = getAnswerFor(answers, "completed_interview")


    val offenderAndOffencesDto = OffenderAndOffencesDto(
      crn = crn,
      gender = Gender.MALE,
      dob = subjectService.getSubjectForAssessment(episode.episodeUuid).dateOfBirth,
      assessmentDate = LocalDateTime.of(2021, 1, 1, 0, 0, 0),
      currentOffence = CurrentOffence("138", "00"),
      dateOfFirstSanction = dateOfFirstSanction,
      totalOffences = totalOffences.toInt(),
      totalViolentOffences = totalViolentOffences.toInt(),
      dateOfCurrentConviction = dateOfCurrentConviction,
      hasAnySexualOffences = hasAnySexualOffences.toBoolean(),
      isCurrentSexualOffence = isCurrentSexualOffence.toBoolean(),
      isCurrentOffenceVictimStranger = isCurrentOffenceVictimStranger.toBoolean(),
      mostRecentSexualOffenceDate = mostRecentSexualOffenceDate,
      totalSexualOffencesInvolvingAnAdult = totalSexualOffencesInvolvingAnAdult.toInt(),
      totalSexualOffencesInvolvingAChild = totalSexualOffencesInvolvingAChild.toInt(),
      totalSexualOffencesInvolvingChildImages = totalSexualOffencesInvolvingChildImages.toInt(),
      totalNonSexualOffences = totalNonSexualOffences.toInt(),
      earliestReleaseDate = earliestReleaseDate,
      hasCompletedInterview = hasCompletedInterview.toBoolean()
    )

    return assessRisksAndNeedsApiRestClient.getRiskPredictors(predictorType, offenderAndOffencesDto)
      .toRiskPredictorScores(crn)
  }

  private fun RiskPredictorsDto?.toRiskPredictorScores(crn: String): PredictorScoresDto {
    log.info("Risk Predictors from ARN $this for crn $crn")
    if (this == null) throw PredictorCalculationException("The risk predictors calculation failed for crn $crn")
    return PredictorScoresDto(
      type = this.type,
      scores = this.toRiskPredictorsScores()
    )
  }

  private fun RiskPredictorsDto.toRiskPredictorsScores(): Map<String, uk.gov.justice.digital.assessments.api.Score> {
    return mapOf(
      "RSR" to uk.gov.justice.digital.assessments.api.Score(
        this.rsrScore.level?.name,
        this.rsrScore.score,
        this.rsrScore.isValid,
        this.calculatedAt
      ),
      "OSP/C" to uk.gov.justice.digital.assessments.api.Score(
        this.ospcScore.level?.name,
        this.ospcScore.score,
        this.ospcScore.isValid,
        this.calculatedAt
      ),
      "OSP/I" to uk.gov.justice.digital.assessments.api.Score(
        this.ospiScore.level?.name,
        this.ospiScore.score,
        this.ospiScore.isValid,
        this.calculatedAt
      )
    )
  }

  private fun getAnswerFor(answers: Map<String, AnswersDto>, answerCode: String): String {
    return answers[answerCode]?.answers?.first()?.items?.first()
      ?: throw EntityNotFoundException("Answer $answerCode for predictor not found")
  }

  fun String?.toBoolean(): Boolean {
    return this == ResponseDto.YES.name
  }

  enum class ResponseDto(val value: String) {
    YES("Yes"), NO("No");
  }
}
