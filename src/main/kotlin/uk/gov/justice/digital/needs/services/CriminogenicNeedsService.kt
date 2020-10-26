package uk.gov.justice.digital.needs.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AssessmentAnswersDto
import uk.gov.justice.digital.needs.api.CriminogenicNeedsDto
import uk.gov.justice.digital.needs.api.CriminogenicNeedDto
import uk.gov.justice.digital.needs.api.CriminogenicNeed
import uk.gov.justice.digital.needs.api.NeedStatus
import org.springframework.stereotype.Serviceimport java.time.LocalDateTime

@Service
class CriminogenicNeedsService {

    private val POSITIVE_ANSWER = "YES"

    fun calculateNeeds(assessmentAnswersDto: AssessmentAnswersDto): CriminogenicNeedsDto {

        val accommodationNeed = CriminogenicNeedMapping.needs().getValue(CriminogenicNeed.ACCOMMODATION)

        val harmQuestion = assessmentAnswersDto.answers[accommodationNeed.harmQuestion]?.first()
        val isHarm = harmQuestion?.equals(POSITIVE_ANSWER)

        val offendingQuestion = assessmentAnswersDto.answers[accommodationNeed.reoffendingQuestion]?.first()
        val isReoffending = offendingQuestion?.equals(POSITIVE_ANSWER)

        val lowScoringQuestion = assessmentAnswersDto.answers[accommodationNeed.lowScoreNeedQuestion]?.first()
        val isLowScore = lowScoringQuestion?.equals(POSITIVE_ANSWER)

        val thresholdQuestions = assessmentAnswersDto.answers.filterKeys { accommodationNeed.thresholdQuestions.contains(it) }
        val overThreshold = thresholdQuestions.values.map { it.first().toInt() }.sum() >= accommodationNeed.threshold


        val accommodationNeedDto = CriminogenicNeedDto(
                riskOfHarm = isHarm,
                riskOfReoffending = isReoffending,
                lowScoringNeed = isLowScore,
                overThreshold = overThreshold,
                description = "Accommodation",
                need =  CriminogenicNeed.ACCOMMODATION,
                needStatus = NeedStatus.NEED_IDENTIFIED )

        return CriminogenicNeedsDto(listOf(accommodationNeedDto), LocalDateTime.now())
    }

}
