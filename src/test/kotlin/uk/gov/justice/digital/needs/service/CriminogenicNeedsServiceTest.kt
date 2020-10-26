package uk.gov.justice.digital.needs.service

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.AssessmentAnswersDto
import uk.gov.justice.digital.assessments.services.AssessmentService
import uk.gov.justice.digital.needs.api.CriminogenicNeed
import uk.gov.justice.digital.needs.api.NeedStatus
import uk.gov.justice.digital.needs.services.CriminogenicNeedsService
import java.util.*


@ExtendWith(MockKExtension::class)
class CriminogenicNeedsServiceTest {

    private val criminogenicNeedsService: CriminogenicNeedsService = CriminogenicNeedsService()
    private val assessmentService: AssessmentService = mockk()

    @Test
    fun `returns needs when risk of harm is positive`(){
        val assessmentUUID = UUID.randomUUID()
        val assessmentAnswerDto = AssessmentAnswersDto(
                assessmentUuid = assessmentUUID,
                answers = mapOf("3.98" to setOf("YES")))

        every { assessmentService.getCurrentAssessmentAnswers(assessmentUUID) } returns (assessmentAnswerDto)
        val result = criminogenicNeedsService.calculateNeeds(assessmentAnswerDto)
        val need = result.criminogenicNeeds.toList().first { it.need == CriminogenicNeed.ACCOMMODATION }
        assertThat(need.riskOfHarm).isTrue()
        assertThat(need.lowScoringNeed).isNull()
        assertThat(need.overThreshold).isNull()
        assertThat(need.riskOfReoffending).isNull()
        assertThat(need.needStatus).isEqualTo(NeedStatus.NEED_IDENTIFIED)

    }

//    @Test
//    fun `does not return need if risk of harm is negative`(){
//        val assessmentUUID = UUID.randomUUID()
//        val assessmentAnswerDto = AssessmentAnswersDto(
//                assessmentUuid = assessmentUUID,
//                answers = mapOf("3.98" to setOf("YES")))
//
//        every { assessmentService.getCurrentAssessmentAnswers(assessmentUUID) } returns (assessmentAnswerDto)
//        val result = criminogenicNeedsService.calculateNeeds(assessmentAnswerDto)
//        val need = result.criminogenicNeeds.toList().first { it.need == CriminogenicNeed.ACCOMMODATION }
//        assertThat(need.riskOfHarm).isFalse()
//        assertThat(need.lowScoringNeed).isNull()
//        assertThat(need.overThreshold).isNull()
//        assertThat(need.riskOfReoffending).isNull()
//        assertThat(need.needStatus).isEqualTo(NeedStatus.INSUFFICENT_DATA)
//
//    }

    @Test
    fun `returns needs when risk of reoffending is positive`(){
        val assessmentUUID = UUID.randomUUID()
        val assessmentAnswerDto = AssessmentAnswersDto(
                assessmentUuid = assessmentUUID,
                answers = mapOf("3.99" to setOf("YES")))

        every { assessmentService.getCurrentAssessmentAnswers(assessmentUUID) } returns (assessmentAnswerDto)
        val result = criminogenicNeedsService.calculateNeeds(assessmentAnswerDto)
        val need = result.criminogenicNeeds.toList().first { it.need == CriminogenicNeed.ACCOMMODATION }
        assertThat(need.riskOfHarm).isNull()
        assertThat(need.lowScoringNeed).isNull()
        assertThat(need.overThreshold).isNull()
        assertThat(need.riskOfReoffending).isTrue()
        assertThat(need.needStatus).isEqualTo(NeedStatus.NEED_IDENTIFIED)

    }

    // Returns needs when Risk of Reoffending question is YES
    // Response is NEED IDENTIFIED


    @Test
    fun `returns needs when low scoring need is positive`(){
        val assessmentUUID = UUID.randomUUID()
        val assessmentAnswerDto = AssessmentAnswersDto(
                assessmentUuid = assessmentUUID,
                answers = mapOf("3.97" to setOf("YES")))

        every { assessmentService.getCurrentAssessmentAnswers(assessmentUUID) } returns (assessmentAnswerDto)
        val result = criminogenicNeedsService.calculateNeeds(assessmentAnswerDto)
        val need = result.criminogenicNeeds.toList().first { it.need == CriminogenicNeed.ACCOMMODATION }
        assertThat(need.riskOfHarm).isNull()
        assertThat(need.lowScoringNeed).isTrue()
        assertThat(need.overThreshold).isNull()
        assertThat(need.riskOfReoffending).isNull()
        assertThat(need.needStatus).isEqualTo(NeedStatus.NEED_IDENTIFIED)

    }

    @Test
    fun `returns needs when over threshold`(){
        val assessmentUUID = UUID.randomUUID()
        val assessmentAnswerDto = AssessmentAnswersDto(
                assessmentUuid = assessmentUUID,
                answers = mapOf("3.90" to setOf("3"), "3.91" to setOf("2")))

        every { assessmentService.getCurrentAssessmentAnswers(assessmentUUID) } returns (assessmentAnswerDto)
        val result = criminogenicNeedsService.calculateNeeds(assessmentAnswerDto)
        val need = result.criminogenicNeeds.toList().first { it.need == CriminogenicNeed.ACCOMMODATION }
        assertThat(need.riskOfHarm).isNull()
        assertThat(need.lowScoringNeed).isNull()
        assertThat(need.overThreshold).isTrue()
        assertThat(need.riskOfReoffending).isNull()
        assertThat(need.needStatus).isEqualTo(NeedStatus.NEED_IDENTIFIED)

    }

    // Returns needs when over threshold (when total or threshold questions is over the threshold in the need config)
    // Boundary conditions
    // Response is NEED IDENTIFIED

    // Returns need when low scoring need question is YES
    // Response is NEED IDENTIFIED


    // Returns Insufficient Data for needs where a need cannot be identified using the above rules and not all
    // questions related to the need (Threshold, harm, reoffending but not the low score need) are set



    // Negative of all the business rules (ie harm question NO, Reoffending question NO, Low Scoring Need is NO and Under Threshold)
    // Response is NO NEED IDENTIFIED


    // Check works for multiple Needs



}