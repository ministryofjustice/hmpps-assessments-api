package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.Answers
import uk.gov.justice.digital.assessments.api.PredictorScoresDto
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.OffenceEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.PredictorFieldMappingEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import uk.gov.justice.digital.assessments.restclient.AssessRisksAndNeedsApiRestClient
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.CurrentOffence
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.CurrentOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.DynamicScoringOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.Gender
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.OffenderAndOffencesDto
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.PreviousOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.RiskPredictorsDto
import uk.gov.justice.digital.assessments.services.dto.EmploymentType
import uk.gov.justice.digital.assessments.services.dto.PredictorType
import uk.gov.justice.digital.assessments.services.dto.ProblemsLevel
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.PredictorCalculationException
import java.util.UUID

@Service
class RiskPredictorsService(
  private val assessmentSchemaService: AssessmentSchemaService,
  private val subjectService: SubjectService,
  private val episodeRepository: EpisodeRepository,
  private val assessRisksAndNeedsApiRestClient: AssessRisksAndNeedsApiRestClient,
  private val offenderService: OffenderService
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun getPredictorResults(episodeUuid: UUID, final: Boolean = false): List<PredictorScoresDto> {
    val episode = episodeRepository.findByEpisodeUuid(episodeUuid)
      ?: throw EntityNotFoundException("Episode with $episodeUuid not found")
    episode.assessment.subject?.crn?.let { offenderService.validateUserAccess(it) }
    return getPredictorResults(episode)
  }

  fun getPredictorResults(
    episode: AssessmentEpisodeEntity,
    final: Boolean = false
  ): List<PredictorScoresDto> {
    val predictors = assessmentSchemaService.getPredictorsForAssessment(episode.assessmentSchemaCode)
    log.info("Found ${predictors.size} predictors for episode ${episode.episodeUuid} with assessment type ${episode.assessmentSchemaCode}")

    return predictors.map { predictor ->
      fetchResults(
        episode,
        final,
        predictor.type,
        extractAnswers(predictor.fieldEntities.toList(), episode.answers.orEmpty())
      )
    }
  }

  private fun extractAnswers(
    predictorFieldEntities: List<PredictorFieldMappingEntity>,
    answers: Answers
  ): Answers {
    return predictorFieldEntities
      .associate { predictorField ->
        val questionCode = predictorField.questionSchema.questionCode
        val questionAnswer = answers[questionCode]
        predictorField.predictorFieldName to questionAnswer.orEmpty()
      }
      .filterValues { answer -> answer.isNotEmpty() }
  }

  private fun fetchResults(
    episode: AssessmentEpisodeEntity,
    final: Boolean,
    predictorType: PredictorType,
    answers: Answers,
  ): PredictorScoresDto {
    val assessmentUuid = getEpisodeAssessmentUuid(episode)
    val offender = subjectService.getSubjectForAssessment(assessmentUuid)
    val crn = offender.crn
    if (offender.gender == null) throw PredictorCalculationException("The risk predictors calculation failed for crn $crn: gender must not be null")
    log.info("Getting Predictor Score for crn $crn and type $predictorType and answers: $answers")
    val hasCompletedInterview = getRequiredAnswer(answers, "completed_interview").toBoolean()
    val offence = getEpisodeOffence(episode)
    val offenderAndOffencesDto = OffenderAndOffencesDto(
      crn = crn,
      gender = Gender.valueOf(offender.gender!!),
      dob = offender.dateOfBirth,
      assessmentDate = episode.createdDate,
      currentOffence = CurrentOffence(offence.offenceCode!!, offence.offenceSubCode!!),
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
      totalNonContactSexualOffences = getNonRequiredAnswer(answers, "total_non_contact_sexual_offences")?.toInt(),
      earliestReleaseDate = getRequiredAnswer(answers, "earliest_release_date"),
      hasCompletedInterview = hasCompletedInterview,
      dynamicScoringOffences = getDynamicScoringOffences(hasCompletedInterview, answers)
    )

    return assessRisksAndNeedsApiRestClient.getRiskPredictors(
      predictorType,
      offenderAndOffencesDto,
      final,
      episode.episodeUuid
    )
      .toRiskPredictorScores(crn)
  }

  private fun getEpisodeOffence(episode: AssessmentEpisodeEntity): OffenceEntity {
    val offence = episode.offence
    if (offence == null || offence.offenceCode.isNullOrEmpty() || offence.offenceSubCode.isNullOrEmpty()) {
      throw EntityNotFoundException("Episode ${episode.episodeUuid} should be associated with an offence and contain both offence code and subcode")
    }
    return offence
  }

  private fun getEpisodeAssessmentUuid(episode: AssessmentEpisodeEntity): UUID {
    return episode.assessment.assessmentUuid
  }

  private fun getDynamicScoringOffences(
    hasCompletedInterview: Boolean,
    answers: Answers,
  ): DynamicScoringOffences? {
    if (!hasCompletedInterview) return null
    return DynamicScoringOffences(
      hasSuitableAccommodation = getNonRequiredAnswer(answers, "suitable_accommodation").toProblemsLevel(),
      employment = getNonRequiredAnswer(answers, "unemployed_on_release").toEmploymentType(),
      currentRelationshipWithPartner = getNonRequiredAnswer(
        answers,
        "current_relationship_with_partner"
      ).toProblemsLevel(),
      evidenceOfDomesticViolence = getNonRequiredAnswer(answers, "evidence_domestic_violence").toBoolean(),
      isPerpetrator = getNonRequiredAnswer(answers, "perpetrator_domestic_violence").tempPerpetratorBoolean(
        getNonRequiredAnswer(answers, "evidence_domestic_violence").toBoolean()
      ),
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
      scoreType = this.scoreType,
      scores = this.toRiskPredictorsScores()
    )
  }

  private fun RiskPredictorsDto.toRiskPredictorsScores(): Map<String, uk.gov.justice.digital.assessments.api.Score> {
    val rsrScore = this.scores["RSR"]
    val ospcScore = this.scores["OSPC"]
    val ospiScore = this.scores["OSPI"]

    return mapOf(
      "RSR" to uk.gov.justice.digital.assessments.api.Score(
        rsrScore?.level?.name,
        rsrScore?.score,
        rsrScore?.isValid == true,
        this.calculatedAt
      ),
      "OSPC" to uk.gov.justice.digital.assessments.api.Score(
        ospcScore?.level?.name,
        ospcScore?.score,
        ospcScore?.isValid == true,
        this.calculatedAt
      ),
      "OSPI" to uk.gov.justice.digital.assessments.api.Score(
        ospiScore?.level?.name,
        ospiScore?.score,
        ospiScore?.isValid == true,
        this.calculatedAt
      )
    )
  }

  private fun getRequiredAnswer(answers: Answers, answerCode: String): String {
    return answers[answerCode]?.first()
      ?: throw EntityNotFoundException("Answer $answerCode for predictor not found")
  }

  private fun getNonRequiredAnswer(answers: Answers, answerCode: String): String? {
    return answers[answerCode]?.first()
  }

  fun String?.toBoolean(): Boolean? {
    return if (this == null) null else this == ResponseDto.YES.name
  }

  fun String?.toPerpetratorBoolean(): Boolean? {
    return if (this == null) null else this == "perpetrator"
  }

  fun String?.tempPerpetratorBoolean(domViolence: Boolean?): Boolean? {
    return if (domViolence == true) {
      true
    } else {
      null
    }
  }

  fun String.toBoolean(): Boolean {
    return this == ResponseDto.YES.name
  }

  private fun String?.toProblemsLevel(): String {
    return this?.let { ProblemsLevel.valueOf(this).name } ?: ProblemsLevel.MISSING.name
  }

  private fun String?.toEmploymentType(): String {
    return this?.let { EmploymentType.valueOf(this).name } ?: EmploymentType.MISSING.name
  }

  enum class ResponseDto(val value: String) {
    YES("Yes"), NO("No");
  }
}
