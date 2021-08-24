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
import uk.gov.justice.digital.assessments.restclient.AssessRisksAndNeedsApiRestClient
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.CurrentOffence
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.CurrentOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.DynamicScoringOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.Gender
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.OffenderAndOffencesDto
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.PredictorSubType
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.PreviousOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.RiskPredictorsDto
import uk.gov.justice.digital.assessments.services.dto.PredictorType
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.PredictorCalculationException
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
      fetchResults(episode, predictor.type, extractAnswers(predictor.fields.toList(), episode.answers.orEmpty()))
    }
  }

  private fun extractAnswers(
    predictorFields: List<PredictorFieldMapping>,
    answers: Map<String, AnswerEntity>
  ): Map<String, AnswersDto> {
    return predictorFields
      .associate { predictorField ->
        val questionCode = predictorField.questionSchema.questionCode
        val questionAnswer = answers[questionCode]
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
    val assessmentUuid = episode.assessment?.assessmentUuid
      ?: throw EntityNotFoundException("Episode ${episode.episodeUuid} is not associated with an assessment")
    val offender = subjectService.getSubjectForAssessment(assessmentUuid)
    val crn = offender.crn
    if (offender.gender == null) throw PredictorCalculationException("The risk predictors calculation failed for crn $crn: gender must not be null")
    log.info("Getting Predictor Score for crn $crn and type $predictorType")
    val hasCompletedInterview = getRequiredAnswer(answers, "completed_interview").toBoolean()
    val offenderAndOffencesDto = OffenderAndOffencesDto(
      crn = crn,
      gender = Gender.valueOf(offender.gender!!),
      dob = offender.dateOfBirth,
      assessmentDate = episode.createdDate,
      currentOffence = CurrentOffence("138", "00"),
      dateOfFirstSanction = getRequiredAnswer(answers, "date_first_sanction"),
      totalOffences = getRequiredAnswer(answers, "total_sanctions").toInt(),
      totalViolentOffences = getRequiredAnswer(answers, "total_violent_offences").toInt(),
      dateOfCurrentConviction = getRequiredAnswer(answers, "date_current_conviction"),
      hasAnySexualOffences = getRequiredAnswer(answers, "any_sexual_offences").toBoolean(),
      isCurrentSexualOffence = getNonRequiredAnswer(answers, "current_sexual_offence").toBoolean(),
      isCurrentOffenceVictimStranger = getNonRequiredAnswer(answers, "current_offence_victim_stranger").toBoolean(),
      mostRecentSexualOffenceDate = getNonRequiredAnswer(answers, "most_recent_sexual_offence_date"),
      totalSexualOffencesInvolvingAnAdult = getNonRequiredAnswer(answers, "total_sexual_offences_adult")?.toInt(),
      totalSexualOffencesInvolvingAChild = getNonRequiredAnswer(answers, "total_sexual_offences_child")?.toInt(),
      totalSexualOffencesInvolvingChildImages = getNonRequiredAnswer(
        answers,
        "total_sexual_offences_child_image"
      )?.toInt(),
      totalNonContactSexualOffences = getNonRequiredAnswer(answers, "total_non_sexual_offences")?.toInt(),
      earliestReleaseDate = getRequiredAnswer(answers, "earliest_release_date"),
      hasCompletedInterview = hasCompletedInterview,
      dynamicScoringOffences = getDynamicScoringOffences(hasCompletedInterview, answers)
    )

    val final = true
    return assessRisksAndNeedsApiRestClient.getRiskPredictors(predictorType, offenderAndOffencesDto, final, episode.episodeUuid)
      .toRiskPredictorScores(crn)
  }

  private fun getDynamicScoringOffences(
    hasCompletedInterview: Boolean,
    answers: Map<String, AnswersDto>
  ): DynamicScoringOffences? {
    if (!hasCompletedInterview) return null
    return DynamicScoringOffences(
      hasSuitableAccommodation = getNonRequiredAnswer(answers, "suitable_accommodation").toProblemsLevel(),
      employment = getNonRequiredAnswer(answers, "unemployed_on_release").toEmploymentType(),
      currentRelationshipWithPartner = getNonRequiredAnswer(answers, "current_relationship_with_partner").toProblemsLevel(),
      evidenceOfDomesticViolence = getNonRequiredAnswer(answers, "evidence_domestic_violence").toBoolean(),
      isPerpetrator = getNonRequiredAnswer(answers, "perpetrator_domestic_violence").toBoolean(),
      alcoholUseIssues = getNonRequiredAnswer(answers, "use_of_alcohol").toProblemsLevel(),
      bingeDrinkingIssues = getNonRequiredAnswer(answers, "binge_drinking").toProblemsLevel(),
      impulsivityIssues = getNonRequiredAnswer(answers, "impulsivity_issues").toProblemsLevel(),
      temperControlIssues = getNonRequiredAnswer(answers, "temper_control_issues").toProblemsLevel(),
      proCriminalAttitudes = getNonRequiredAnswer(answers, "pro_criminal_attitudes").toProblemsLevel(),
      previousOffences = PreviousOffences(
        murderAttempt = getNonRequiredAnswer(answers, "previous_murder_attempt").toBoolean(),
        wounding = getNonRequiredAnswer(answers, "previous_wounding").toBoolean(),
        aggravatedBurglary = getNonRequiredAnswer(answers, "previous_aggravated_burglary").toBoolean(),
        arson = getNonRequiredAnswer(answers, "previous_arson").toBoolean(),
        criminalDamage = getNonRequiredAnswer(answers, "previous_criminal_damage").toBoolean(),
        kidnapping = getNonRequiredAnswer(answers, "previous_kidnapping").toBoolean(),
        firearmPossession = getNonRequiredAnswer(answers, "previous_possession_firearm").toBoolean(),
        robbery = getNonRequiredAnswer(answers, "previous_robbery").toBoolean(),
        offencesWithWeapon = getNonRequiredAnswer(answers, "previous_offence_weapon").toBoolean(),
      ),
      currentOffences = CurrentOffences(
        firearmPossession = getNonRequiredAnswer(answers, "current_possession_firearm").toBoolean(),
        offencesWithWeapon = getNonRequiredAnswer(answers, "current_offence_weapon").toBoolean(),
      )
    )
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
    val rsrScore = this.scores[PredictorSubType.RSR]
    val ospcScore = this.scores[PredictorSubType.OSPC]
    val ospiScore = this.scores[PredictorSubType.OSPI]

    return mapOf(
      PredictorSubType.RSR.name to uk.gov.justice.digital.assessments.api.Score(
        rsrScore?.level?.name,
        rsrScore?.score,
        rsrScore?.isValid == true,
        this.calculatedAt
      ),
      PredictorSubType.OSPC.name to uk.gov.justice.digital.assessments.api.Score(
        ospcScore?.level?.name,
        ospcScore?.score,
        ospcScore?.isValid == true,
        this.calculatedAt
      ),
      PredictorSubType.OSPI.name to uk.gov.justice.digital.assessments.api.Score(
        ospiScore?.level?.name,
        ospiScore?.score,
        ospiScore?.isValid == true,
        this.calculatedAt
      )
    )
  }

  private fun getRequiredAnswer(answers: Map<String, AnswersDto>, answerCode: String): String {
    return answers[answerCode]?.answers?.first()?.items?.first()
      ?: throw EntityNotFoundException("Answer $answerCode for predictor not found")
  }

  private fun getNonRequiredAnswer(answers: Map<String, AnswersDto>, answerCode: String): String? {
    return answers[answerCode]?.answers?.first()?.items?.first()
  }

  fun String?.toBoolean(): Boolean? {
    return if (this == null) null else this == ResponseDto.YES.name
  }

  fun String.toBoolean(): Boolean {
    return this == ResponseDto.YES.name
  }

  private fun String?.toProblemsLevel(): String? {
    return ProblemsLevel.fromString(this).name
  }

  private fun String?.toEmploymentType(): String? {
    return EmploymentType.fromString(this).name
  }

  enum class ResponseDto(val value: String) {
    YES("Yes"), NO("No");
  }

  enum class EmploymentType(val value: String? = null) {
    NO("no"), NOT_AVAILABLE_FOR_WORK("not available for work"), YES("yes"), MISSING;

    companion object {
      fun fromString(enumValue: String?): EmploymentType {
        return values().firstOrNull { it.value == enumValue }
          ?: MISSING
      }
    }
  }

  enum class ProblemsLevel(val value: String? = null) {
    NO_PROBLEMS("no problems"), SOME_PROBLEMS("some problems"), SIGNIFICANT_PROBLEMS("significant problems"), MISSING;

    companion object {
      fun fromString(enumValue: String?): ProblemsLevel {
        return values().firstOrNull { it.value == enumValue }
          ?: MISSING
      }
    }
  }
}
