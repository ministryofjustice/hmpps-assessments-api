package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uk.gov.justice.digital.assessments.jpa.entities.Answer
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.Predictor
import uk.gov.justice.digital.assessments.jpa.entities.PredictorFieldMapping
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import uk.gov.justice.digital.assessments.restclient.AssessRisksAndNeedsApiRestClient
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.CurrentOffence
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.CurrentOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.DynamicScoringOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.Gender
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.OffenderAndOffencesDto
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.PredictorSubType
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.PreviousOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.RiskPredictorsDto
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.Score
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.ScoreLevel
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.ScoreType
import uk.gov.justice.digital.assessments.services.dto.PredictorType
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class PredictorServiceTest {
  private val assessmentSchemaService: AssessmentSchemaService = mockk()
  private val subjectService: SubjectService = mockk()
  private val assessRisksAndNeedsApiRestClient: AssessRisksAndNeedsApiRestClient = mockk()

  private val predictorService =
    PredictorService(assessmentSchemaService, subjectService, assessRisksAndNeedsApiRestClient)

  private val testQuestion1 = QuestionSchemaEntity(
    questionSchemaId = 1,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion2 = QuestionSchemaEntity(
    questionSchemaId = 2,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion3 = QuestionSchemaEntity(
    questionSchemaId = 3,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion4 = QuestionSchemaEntity(
    questionSchemaId = 4,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion5 = QuestionSchemaEntity(
    questionSchemaId = 5,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion6 = QuestionSchemaEntity(
    questionSchemaId = 6,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion7 = QuestionSchemaEntity(
    questionSchemaId = 7,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion8 = QuestionSchemaEntity(
    questionSchemaId = 8,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion9 = QuestionSchemaEntity(
    questionSchemaId = 9,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion10 = QuestionSchemaEntity(
    questionSchemaId = 10,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion11 = QuestionSchemaEntity(
    questionSchemaId = 11,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion12 = QuestionSchemaEntity(
    questionSchemaId = 12,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion13 = QuestionSchemaEntity(
    questionSchemaId = 13,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion14 = QuestionSchemaEntity(
    questionSchemaId = 14,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion15 = QuestionSchemaEntity(
    questionSchemaId = 15,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion16 = QuestionSchemaEntity(
    questionSchemaId = 16,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion17 = QuestionSchemaEntity(
    questionSchemaId = 17,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion18 = QuestionSchemaEntity(
    questionSchemaId = 18,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion19 = QuestionSchemaEntity(
    questionSchemaId = 19,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion20 = QuestionSchemaEntity(
    questionSchemaId = 20,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion21 = QuestionSchemaEntity(
    questionSchemaId = 21,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion22 = QuestionSchemaEntity(
    questionSchemaId = 22,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion23 = QuestionSchemaEntity(
    questionSchemaId = 23,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion24 = QuestionSchemaEntity(
    questionSchemaId = 24,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion25 = QuestionSchemaEntity(
    questionSchemaId = 25,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion26 = QuestionSchemaEntity(
    questionSchemaId = 26,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion27 = QuestionSchemaEntity(
    questionSchemaId = 27,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion28 = QuestionSchemaEntity(
    questionSchemaId = 28,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion29 = QuestionSchemaEntity(
    questionSchemaId = 29,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion30 = QuestionSchemaEntity(
    questionSchemaId = 30,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion31 = QuestionSchemaEntity(
    questionSchemaId = 31,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion32 = QuestionSchemaEntity(
    questionSchemaId = 32,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion33 = QuestionSchemaEntity(
    questionSchemaId = 33,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion34 = QuestionSchemaEntity(
    questionSchemaId = 34,
    questionSchemaUuid = UUID.randomUUID(),
  )
  private val testQuestion35 = QuestionSchemaEntity(
    questionSchemaId = 35,
    questionSchemaUuid = UUID.randomUUID(),
  )

  private val predictors = listOf(
    Predictor(
      1,
      AssessmentSchemaCode.RSR,
      PredictorType.RSR,
      listOf(
        PredictorFieldMapping(
          1,
          UUID.randomUUID(),
          testQuestion1,
          PredictorType.RSR,
          "date_first_sanction"
        ),
        PredictorFieldMapping(
          2,
          UUID.randomUUID(),
          testQuestion2,
          PredictorType.RSR,
          "total_sanctions"
        ),
        PredictorFieldMapping(
          3,
          UUID.randomUUID(),
          testQuestion3,
          PredictorType.RSR,
          "total_violent_offences"
        ),
        PredictorFieldMapping(
          4,
          UUID.randomUUID(),
          testQuestion4,
          PredictorType.RSR,
          "date_current_conviction"
        ),
        PredictorFieldMapping(
          5,
          UUID.randomUUID(),
          testQuestion5,
          PredictorType.RSR,
          "any_sexual_offences"
        ),
        PredictorFieldMapping(
          6,
          UUID.randomUUID(),
          testQuestion6,
          PredictorType.RSR,
          "current_sexual_offence"
        ),
        PredictorFieldMapping(
          7,
          UUID.randomUUID(),
          testQuestion7,
          PredictorType.RSR,
          "current_offence_victim_stranger"
        ),
        PredictorFieldMapping(
          8,
          UUID.randomUUID(),
          testQuestion8,
          PredictorType.RSR,
          "most_recent_sexual_offence_date"
        ),
        PredictorFieldMapping(
          9,
          UUID.randomUUID(),
          testQuestion9,
          PredictorType.RSR,
          "total_sexual_offences_adult"
        ),
        PredictorFieldMapping(
          10,
          UUID.randomUUID(),
          testQuestion10,
          PredictorType.RSR,
          "total_sexual_offences_child"
        ),

        PredictorFieldMapping(
          11,
          UUID.randomUUID(),
          testQuestion11,
          PredictorType.RSR,
          "total_sexual_offences_child_image"
        ),
        PredictorFieldMapping(
          12,
          UUID.randomUUID(),
          testQuestion12,
          PredictorType.RSR,
          "total_non_sexual_offences"
        ),
        PredictorFieldMapping(
          13,
          UUID.randomUUID(),
          testQuestion13,
          PredictorType.RSR,
          "earliest_release_date"
        ),
        PredictorFieldMapping(
          14,
          UUID.randomUUID(),
          testQuestion14,
          PredictorType.RSR,
          "completed_interview"
        ),
        PredictorFieldMapping(
          15,
          UUID.randomUUID(),
          testQuestion15,
          PredictorType.RSR,
          "suitable_accommodation"
        ),
        PredictorFieldMapping(
          16,
          UUID.randomUUID(),
          testQuestion16,
          PredictorType.RSR,
          "unemployed_on_release"
        ),
        PredictorFieldMapping(
          17,
          UUID.randomUUID(),
          testQuestion17,
          PredictorType.RSR,
          "current_relationship_with_partner"
        ),
        PredictorFieldMapping(
          18,
          UUID.randomUUID(),
          testQuestion18,
          PredictorType.RSR,
          "evidence_domestic_violence"
        ),
        PredictorFieldMapping(
          19,
          UUID.randomUUID(),
          testQuestion19,
          PredictorType.RSR,
          "perpetrator_domestic_violence"
        ),
        PredictorFieldMapping(
          20,
          UUID.randomUUID(),
          testQuestion20,
          PredictorType.RSR,
          "use_of_alcohol"
        ),
        PredictorFieldMapping(
          21,
          UUID.randomUUID(),
          testQuestion21,
          PredictorType.RSR,
          "binge_drinking"
        ),
        PredictorFieldMapping(
          22,
          UUID.randomUUID(),
          testQuestion22,
          PredictorType.RSR,
          "impulsivity_issues"
        ),
        PredictorFieldMapping(
          23,
          UUID.randomUUID(),
          testQuestion23,
          PredictorType.RSR,
          "temper_control_issues"
        ),
        PredictorFieldMapping(
          24,
          UUID.randomUUID(),
          testQuestion24,
          PredictorType.RSR,
          "pro_criminal_attitudes"
        ),
        PredictorFieldMapping(
          25,
          UUID.randomUUID(),
          testQuestion25,
          PredictorType.RSR,
          "previous_murder_attempt"
        ),
        PredictorFieldMapping(
          26,
          UUID.randomUUID(),
          testQuestion26,
          PredictorType.RSR,
          "previous_wounding"
        ),
        PredictorFieldMapping(
          27,
          UUID.randomUUID(),
          testQuestion27,
          PredictorType.RSR,
          "previous_aggravated_burglary"
        ),
        PredictorFieldMapping(
          28,
          UUID.randomUUID(),
          testQuestion28,
          PredictorType.RSR,
          "previous_arson"
        ),
        PredictorFieldMapping(
          29,
          UUID.randomUUID(),
          testQuestion29,
          PredictorType.RSR,
          "previous_criminal_damage"
        ),
        PredictorFieldMapping(
          30,
          UUID.randomUUID(),
          testQuestion30,
          PredictorType.RSR,
          "previous_kidnapping"
        ),
        PredictorFieldMapping(
          31,
          UUID.randomUUID(),
          testQuestion31,
          PredictorType.RSR,
          "previous_possession_firearm"
        ),
        PredictorFieldMapping(
          32,
          UUID.randomUUID(),
          testQuestion32,
          PredictorType.RSR,
          "previous_robbery"
        ),
        PredictorFieldMapping(
          33,
          UUID.randomUUID(),
          testQuestion33,
          PredictorType.RSR,
          "previous_offence_weapon"
        ),
        PredictorFieldMapping(
          34,
          UUID.randomUUID(),
          testQuestion34,
          PredictorType.RSR,
          "current_possession_firearm"
        ),
        PredictorFieldMapping(
          35,
          UUID.randomUUID(),
          testQuestion35,
          PredictorType.RSR,
          "current_offence_weapon"
        ),
      )
    )
  )

  private val answers = mutableMapOf(
    testQuestion1.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("2021-10-01")))
    ),
    testQuestion2.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("10")))
    ),
    testQuestion3.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("9")))
    ),
    testQuestion4.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("2021-11-01")))
    ),
    testQuestion5.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion6.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion7.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion8.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("2021-09-01")))
    ),
    testQuestion9.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("2")))
    ),
    testQuestion10.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("4")))
    ),
    testQuestion11.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("10")))
    ),
    testQuestion12.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("9")))
    ),
    testQuestion13.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("2025-11-01")))
    ),
    testQuestion14.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion15.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("")))
    ),
    testQuestion16.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("not available for work")))
    ),
    testQuestion17.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("significant problems")))
    ),
    testQuestion18.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion19.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion20.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("significant problems")))
    ),
    testQuestion21.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("significant problems")))
    ),
    testQuestion22.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("some problems")))
    ),
    testQuestion23.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("significant problems")))
    ),
    testQuestion24.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("some problems")))
    ),
    testQuestion25.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion26.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion27.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion28.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion29.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion30.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion31.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion32.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion33.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion34.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
    testQuestion35.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("YES")))
    ),
  )

  val assessment = AssessmentEntity()
  private val assessmentEpisode = AssessmentEpisodeEntity(
    episodeId = 1,
    episodeUuid = UUID.randomUUID(),
    answers = answers,
    createdDate = LocalDateTime.now(),
    assessment = assessment
  )

  private val assessmentEpisodeNoAnswers = AssessmentEpisodeEntity(
    episodeId = 2,
    episodeUuid = UUID.randomUUID(),
    createdDate = LocalDateTime.now(),
    assessment = assessment
  )
  val final = true
  val episodeUuid = assessmentEpisode.episodeUuid

  @BeforeEach
  private fun setup() {
    every {
      assessRisksAndNeedsApiRestClient.getRiskPredictors(any(), any(), final, episodeUuid)
    } returns null
  }

  @Nested
  @DisplayName("get predictor results")
  inner class GetPredictorResults {
    @Test
    fun `throws exception when required answer is not found`() {
      every { assessmentSchemaService.getPredictorsForAssessment(AssessmentSchemaCode.RSR) } returns predictors
      every {
        subjectService.getSubjectForAssessment(assessment.assessmentUuid)
      } returns SubjectEntity(
        oasysOffenderPk = 9999, dateOfBirth = LocalDate.of(2001, 1, 1), gender = "MALE", crn = "X1345"
      )

      assertThrows<EntityNotFoundException> {
        predictorService.getPredictorResults(
          AssessmentSchemaCode.RSR,
          assessmentEpisodeNoAnswers
        )
      }
    }

    @Test
    fun `returns predictor scores for the assessment code`() {
      every { assessmentSchemaService.getPredictorsForAssessment(AssessmentSchemaCode.RSR) } returns predictors

      val offenderAndOffencesDto = OffenderAndOffencesDto(
        crn = "X1345",
        gender = Gender.FEMALE,
        dob = LocalDate.of(2001, 1, 1),
        assessmentDate = assessmentEpisode.createdDate,
        currentOffence = CurrentOffence("138", "00"),
        dateOfFirstSanction = "2021-10-01",
        totalOffences = 10,
        totalViolentOffences = 9,
        dateOfCurrentConviction = "2021-11-01",
        hasAnySexualOffences = true,
        isCurrentSexualOffence = true,
        isCurrentOffenceVictimStranger = true,
        mostRecentSexualOffenceDate = "2021-09-01",
        totalSexualOffencesInvolvingAnAdult = 2,
        totalSexualOffencesInvolvingAChild = 4,
        totalSexualOffencesInvolvingChildImages = 10,
        totalNonContactSexualOffences = 9,
        earliestReleaseDate = "2025-11-01",
        hasCompletedInterview = true,
        dynamicScoringOffences = DynamicScoringOffences(
          hasSuitableAccommodation = "MISSING",
          employment = "NOT_AVAILABLE_FOR_WORK",
          currentRelationshipWithPartner = "SIGNIFICANT_PROBLEMS",
          evidenceOfDomesticViolence = true,
          isPerpetrator = true,
          alcoholUseIssues = "SIGNIFICANT_PROBLEMS",
          bingeDrinkingIssues = "SIGNIFICANT_PROBLEMS",
          impulsivityIssues = "SOME_PROBLEMS",
          temperControlIssues = "SIGNIFICANT_PROBLEMS",
          proCriminalAttitudes = "SOME_PROBLEMS",
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

      every {
        assessRisksAndNeedsApiRestClient.getRiskPredictors(
          PredictorType.RSR,
          offenderAndOffencesDto,
          final,
          episodeUuid
        )
      } returns RiskPredictorsDto(
        type = PredictorType.RSR,
        scoreType = ScoreType.STATIC,
        scores = mapOf(
          PredictorSubType.RSR to Score(level = ScoreLevel.HIGH, score = BigDecimal("11.34"), isValid = true),
          PredictorSubType.OSPC to Score(level = ScoreLevel.NOT_APPLICABLE, score = BigDecimal("0"), isValid = false),
          PredictorSubType.OSPI to Score(level = ScoreLevel.NOT_APPLICABLE, score = BigDecimal("0"), isValid = false),
        ),
        calculatedAt = "2021-08-09 14:46:48"
      )

      every {
        subjectService.getSubjectForAssessment(assessment.assessmentUuid)
      } returns SubjectEntity(
        oasysOffenderPk = 9999, dateOfBirth = LocalDate.of(2001, 1, 1), gender = "FEMALE", crn = "X1345"
      )

      val results = predictorService.getPredictorResults(AssessmentSchemaCode.RSR, assessmentEpisode)

      assertThat(results).hasSize(1)
      assertThat(results.first().type).isEqualTo(PredictorType.RSR)
      assertThat(results.first().scores).contains(
        entry(
          "RSR",
          uk.gov.justice.digital.assessments.api.Score(
            "HIGH",
            BigDecimal("11.34"),
            true,
            "2021-08-09 14:46:48"
          )
        ),
        entry(
          "OSP/C",
          uk.gov.justice.digital.assessments.api.Score(
            "NOT_APPLICABLE",
            BigDecimal("0"),
            false,
            "2021-08-09 14:46:48"
          )
        ),
        entry(
          "OSP/I",
          uk.gov.justice.digital.assessments.api.Score(
            "NOT_APPLICABLE",
            BigDecimal("0"),
            false,
            "2021-08-09 14:46:48"
          )
        )
      )
    }
  }
}
