package uk.gov.justice.digital.needs.service

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.AnswerSchemaDto
import uk.gov.justice.digital.assessments.api.AssessmentAnswersDto
import uk.gov.justice.digital.assessments.services.AssessmentService
import uk.gov.justice.digital.needs.api.CriminogenicNeed
import uk.gov.justice.digital.needs.api.CriminogenicNeedDto
import uk.gov.justice.digital.needs.api.NeedStatus
import uk.gov.justice.digital.needs.services.CriminogenicNeedMapping
import uk.gov.justice.digital.needs.services.CriminogenicNeedsService
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Criminogenic Needs Tests")
class CriminogenicNeedsServiceTest {
  private val assessmentService: AssessmentService = mockk()
  private val criminogenicNeedsService: CriminogenicNeedsService = CriminogenicNeedsService(assessmentService)

  @Test
  fun `returns needs when risk of harm is positive`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(setNeedValue("accom_rosha", "YES"))
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.riskOfHarm).isTrue()
    assertThat(need.lowScoringNeed).isNull()
    assertThat(need.overThreshold).isNull()
    assertThat(need.riskOfReoffending).isNull()
    assertThat(need.needStatus).isEqualTo(NeedStatus.NEED_IDENTIFIED)
  }

  @Test
  fun `returns insufficient data when threshold question data is incomplete`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("no_fixed_abode", "0"),
        setNeedValue("accom_rosha", "NO"),
        setNeedValue("accom_offending", "NO")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.riskOfHarm).isFalse()
    assertThat(need.lowScoringNeed).isNull()
    assertThat(need.overThreshold).isNull()
    assertThat(need.riskOfReoffending).isFalse()
    assertThat(need.needStatus).isEqualTo(NeedStatus.INSUFFICIENT_DATA)
  }

  @Test
  fun `returns insufficient data when no harm question provided`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("no_fixed_abode", "0"),
        setNeedValue("accom_suitability", "0"),
        setNeedValue("accom_permanence", "0"),
        setNeedValue("accom_location", "0"),
        setNeedValue("accom_offending", "NO")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.riskOfHarm).isNull()
    assertThat(need.lowScoringNeed).isNull()
    assertThat(need.overThreshold).isFalse()
    assertThat(need.riskOfReoffending).isFalse()
    assertThat(need.needStatus).isEqualTo(NeedStatus.INSUFFICIENT_DATA)
  }

  @Test
  fun `returns insufficient data when no reoffending question provided`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("no_fixed_abode", "0"),
        setNeedValue("accom_suitability", "0"),
        setNeedValue("accom_permanence", "0"),
        setNeedValue("accom_location", "0"),
        setNeedValue("accom_rosha", "NO")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.riskOfHarm).isFalse()
    assertThat(need.lowScoringNeed).isNull()
    assertThat(need.overThreshold).isFalse()
    assertThat(need.riskOfReoffending).isNull()
    assertThat(need.needStatus).isEqualTo(NeedStatus.INSUFFICIENT_DATA)
  }

  @Test
  fun `returns need not identified when harm, reoffending and threshold are false but low scoring need is unset`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("no_fixed_abode", "0"),
        setNeedValue("accom_suitability", "0"),
        setNeedValue("accom_permanence", "0"),
        setNeedValue("accom_location", "0"),
        setNeedValue("accom_rosha", "NO"),
        setNeedValue("accom_offending", "NO")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.riskOfHarm).isFalse()
    assertThat(need.lowScoringNeed).isNull()
    assertThat(need.overThreshold).isFalse()
    assertThat(need.riskOfReoffending).isFalse()
    assertThat(need.needStatus).isEqualTo(NeedStatus.NO_NEED_IDENTIFIED)
  }

  @Test
  fun `returns need not identified when all questions are negative and under threshold`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("no_fixed_abode", "0"),
        setNeedValue("accom_suitability", "0"),
        setNeedValue("accom_permanence", "0"),
        setNeedValue("accom_location", "0"),
        setNeedValue("accom_rosha", "NO"),
        setNeedValue("accom_offending", "NO"),
        setNeedValue("accom_lowscore", "NO")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.riskOfHarm).isFalse()
    assertThat(need.lowScoringNeed).isFalse()
    assertThat(need.overThreshold).isFalse()
    assertThat(need.riskOfReoffending).isFalse()
    assertThat(need.needStatus).isEqualTo(NeedStatus.NO_NEED_IDENTIFIED)
  }

  @Test
  fun `returns needs when risk of reoffending is positive`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("accom_offending", "YES")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.riskOfHarm).isNull()
    assertThat(need.lowScoringNeed).isNull()
    assertThat(need.overThreshold).isNull()
    assertThat(need.riskOfReoffending).isTrue()
    assertThat(need.needStatus).isEqualTo(NeedStatus.NEED_IDENTIFIED)
  }

  @Test
  fun `returns needs when low scoring need is positive`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("accom_lowscore", "YES")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.riskOfHarm).isNull()
    assertThat(need.lowScoringNeed).isTrue()
    assertThat(need.overThreshold).isNull()
    assertThat(need.riskOfReoffending).isNull()
    assertThat(need.needStatus).isEqualTo(NeedStatus.NEED_IDENTIFIED)
  }

  @Test
  fun `returns needs when over threshold`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("no_fixed_abode", "3"),
        setNeedValue("accom_suitability", "2")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.riskOfHarm).isNull()
    assertThat(need.lowScoringNeed).isNull()
    assertThat(need.overThreshold).isTrue()
    assertThat(need.riskOfReoffending).isNull()
    assertThat(need.needStatus).isEqualTo(NeedStatus.NEED_IDENTIFIED)
  }

  @Test
  fun `returns need when over threshold with incomplete threshold questions`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("accom_suitability", "6")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.riskOfHarm).isNull()
    assertThat(need.lowScoringNeed).isNull()
    assertThat(need.overThreshold).isTrue()
    assertThat(need.riskOfReoffending).isNull()
    assertThat(need.needStatus).isEqualTo(NeedStatus.NEED_IDENTIFIED)
  }

  @Test
  fun `returns threshold false when 1 under threshold`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("no_fixed_abode", "1"),
        setNeedValue("accom_suitability", "0"),
        setNeedValue("accom_permanence", "0"),
        setNeedValue("accom_location", "0")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.overThreshold).isFalse()
  }

  @Test
  fun `returns over threshold true when 1 over threshold`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("no_fixed_abode", "3"),
        setNeedValue("accom_suitability", "3")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.overThreshold).isTrue()
  }

  @Test
  fun `returns over threshold true at the threshold`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("no_fixed_abode", "2"),
        setNeedValue("accom_suitability", "3")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val need = calculateAccommodationNeeds(assessmentUUID)

    assertThat(need.overThreshold).isTrue()
  }

  @Test
  fun `returns all needs`() {
    val assessmentUUID = UUID.randomUUID()
    val assessmentAnswerDto = AssessmentAnswersDto(
      assessmentUuid = assessmentUUID,
      answers = mapOf(
        setNeedValue("accom_rosha", "YES")
      )
    )

    every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUUID) } returns (assessmentAnswerDto)
    val result = criminogenicNeedsService.calculateNeeds(assessmentUUID)

    val allNeeds = result.criminogenicNeeds.toList()
    assertThat(allNeeds).hasSize(CriminogenicNeedMapping.needs().size)
  }

  private fun calculateAccommodationNeeds(assessmentUUID: UUID): CriminogenicNeedDto {
    val result = criminogenicNeedsService.calculateNeeds(assessmentUUID)
    return result.criminogenicNeeds.toList().first { it.need == CriminogenicNeed.ACCOMMODATION }
  }

  private fun setNeedValue(need: String, value: String): Pair<String, Set<AnswerSchemaDto>> {
    return Pair(
      need,
      setOf(
        AnswerSchemaDto(
          answerSchemaUuid = UUID.randomUUID(),
          answerSchemaCode = value,
          value = value
        )
      )
    )
  }
}
