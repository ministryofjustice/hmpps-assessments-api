package uk.gov.justice.digital.needs.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AssessmentAnswersDto
import uk.gov.justice.digital.assessments.services.AssessmentService
import uk.gov.justice.digital.needs.api.CriminogenicNeedsDto
import uk.gov.justice.digital.needs.api.CriminogenicNeedDto
import uk.gov.justice.digital.needs.api.NeedStatus
import java.time.LocalDateTime
import java.util.UUID

@Service
class CriminogenicNeedsService (val assessmentService :  AssessmentService) {

    private val POSITIVE_ANSWER = "YES"


    fun calculateNeeds(assessmentUuid: UUID): CriminogenicNeedsDto {

        val assessmentAnswersDto = assessmentService.getCurrentAssessmentCodedAnswers(assessmentUuid)

        val needs: MutableList<CriminogenicNeedDto> = mutableListOf()
        for ((criminogenicNeed, needQuestions) in  CriminogenicNeedMapping.needs()) {

            val isHarm = isHarmRisk(assessmentAnswersDto, needQuestions)
            val isReoffending = isOffendingRisk(assessmentAnswersDto, needQuestions)
            val isLowScore = isLowScoringRisk(assessmentAnswersDto, needQuestions)

            val overThreshold = areValuesOverThreshold(assessmentAnswersDto, needQuestions)
            val sufficientThresholdQuestions = calculateSufficientThresholdQuestions(assessmentAnswersDto, needQuestions)

            val isValidOverThreshold = isOverThresholdAndSufficient(overThreshold, sufficientThresholdQuestions)
            val sufficientData = calculateSufficientData(sufficientThresholdQuestions, overThreshold, isHarm, isReoffending)

            val needsStatus = calculateNeedStatus(isHarm, isReoffending, isLowScore, isValidOverThreshold, sufficientData)

             needs.add(CriminogenicNeedDto(
                    riskOfHarm = isHarm,
                    riskOfReoffending = isReoffending,
                    lowScoringNeed = isLowScore,
                    overThreshold = isValidOverThreshold,
                    description = criminogenicNeed.description,
                    need = criminogenicNeed,
                    needStatus = needsStatus))
        }

        return CriminogenicNeedsDto(needs, LocalDateTime.now())
    }

    private fun isHarmRisk(assessmentAnswersDto: AssessmentAnswersDto, needQuestions: NeedConfiguration): Boolean? {
        val harmQuestion = assessmentAnswersDto.answers[needQuestions.harmQuestion]?.first()
        return harmQuestion?.equals(POSITIVE_ANSWER)
}

    private fun isOffendingRisk(assessmentAnswersDto: AssessmentAnswersDto, needQuestions: NeedConfiguration): Boolean? {
        val offendingQuestion = assessmentAnswersDto.answers[needQuestions.reoffendingQuestion]?.first()
        return offendingQuestion?.equals(POSITIVE_ANSWER)
    }

    private fun isLowScoringRisk(assessmentAnswersDto: AssessmentAnswersDto, needQuestions: NeedConfiguration): Boolean? {
        val lowScoringQuestion = assessmentAnswersDto.answers[needQuestions.lowScoreNeedQuestion]?.first()
        return lowScoringQuestion?.equals(POSITIVE_ANSWER)
    }

    private fun areValuesOverThreshold(assessmentAnswersDto: AssessmentAnswersDto, needQuestions: NeedConfiguration): Boolean {
        val thresholdQuestions = assessmentAnswersDto.answers.filterKeys { needQuestions.thresholdQuestions.contains(it) }
        return thresholdQuestions.values.map { it.first().toInt() }.sum() >= needQuestions.threshold
    }

    private fun calculateSufficientThresholdQuestions(assessmentAnswersDto: AssessmentAnswersDto, needQuestions: NeedConfiguration): Boolean {
        return assessmentAnswersDto.answers.keys.containsAll(needQuestions.thresholdQuestions)
    }

    private fun isOverThresholdAndSufficient(overThreshold: Boolean, sufficientThresholdQuestions: Boolean): Boolean? {
        return if(overThreshold) {
             true
        }
        else return if (!overThreshold && sufficientThresholdQuestions) {
             false
        }
        else null
    }

    private fun calculateSufficientData(sufficientThresholdQuestions: Boolean, overThreshold: Boolean, isHarm: Boolean?, isReoffending: Boolean?): Boolean {
        return (sufficientThresholdQuestions || overThreshold) && isHarm != null && isReoffending != null
    }
    
    private fun calculateNeedStatus(isHarm: Boolean?, isReoffending: Boolean?, isLowScore: Boolean?, isOverThreshold: Boolean?, sufficientData: Boolean): NeedStatus {
        return if(listOf(isHarm, isReoffending, isLowScore, isOverThreshold).contains(true)) {
            NeedStatus.NEED_IDENTIFIED
        }
        else return if(!sufficientData) {
            NeedStatus.INSUFFICIENT_DATA
        }
        else NeedStatus.NO_NEED_IDENTIFIED
    }

}
