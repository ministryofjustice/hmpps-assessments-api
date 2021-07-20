package uk.gov.justice.digital.assessments.services

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.PredictorResultStatus
import uk.gov.justice.digital.assessments.api.PredictorScoreDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.PredictorType
import java.time.LocalDateTime

class AssessmentUpdateServiceITTest() : IntegrationTest() {
  @Autowired
  internal lateinit var assessmentUpdateService: AssessmentUpdateService
  private val assessmentId = 1L
  private val episodeId2 = 2L
  private val episodeUuid = UUID.randomUUID()

  @Test
  fun `Trying to push update assessment to OASys`() {
    val assessment = rsrAssessment()
    val updateAssessmentResponse =
      assessmentUpdateService.updateEpisode(assessment.episodes.first(), UpdateAssessmentEpisodeDto(mutableMapOf()))
    assertThat(updateAssessmentResponse).isEqualTo(
      AssessmentEpisodeDto.from(
        assessment.episodes.first(),
        null,
        listOf(PredictorScoreDto(type = PredictorType.RSR, status = PredictorResultStatus.UNDETERMINED, score = null))
      )
    )
  }

  @Test
  fun `Trying to push assessment completion to OASys`() {
    val assessment = rsrAssessment()
    val updateAssessmentResponse =
      assessmentUpdateService.closeEpisode(assessment.episodes.first())
    assertThat(updateAssessmentResponse).isEqualTo(
      AssessmentEpisodeDto.from(
        assessment.episodes.first(),
        null,
       emptyList()
      )
    )
  }

  private fun rsrAssessment() = AssessmentEntity(
    assessmentId = assessmentId,
    episodes = mutableListOf(
      AssessmentEpisodeEntity(
        episodeUuid = episodeUuid,
        assessment = AssessmentEntity(),
        episodeId = episodeId2,
        changeReason = "Change of Circs 2",
        assessmentSchemaCode = AssessmentSchemaCode.RSR,
        answers = mutableMapOf(),
        createdDate = LocalDateTime.now(),
      )
    )
  )
}
