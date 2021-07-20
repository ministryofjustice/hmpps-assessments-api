package uk.gov.justice.digital.assessments.services

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat as assertThat1

class AssessmentUpdateServiceITTest() : IntegrationTest() {
  @Autowired
  internal lateinit var assessmentUpdateService: AssessmentUpdateService
  private val assessmentId = 1L
  private val episodeId2 = 2L
  private val episodeUuid = UUID.randomUUID()

  @Test
  fun `Trying to push to Create OASys Assessment that should not be pushed into Oasys returns null`() {
    val returnAssessmentPk =
      assessmentUpdateService.createOasysAssessment(crn = "X13456", assessmentSchemaCode = AssessmentSchemaCode.RSR)
    assertThat1(returnAssessmentPk).isEqualTo(Pair(null, null))
  }

  @Test
  fun `Trying to push to OASys Assessment update that should not be pushed into Oasys returns null`() {
    val assessment = rsrAssessment()
    val updateAssessmentResponse =
      assessmentUpdateService.updateOASysAssessment(assessment.episodes.first(), mutableMapOf())
    assertThat1(updateAssessmentResponse).isEqualTo(null)
  }

  @Test
  fun `Trying to push to OASys Assessment completion that should not be pushed into Oasys returns null`() {
    val assessment = rsrAssessment()

    val updateAssessmentResponse =
      assessmentUpdateService.completeOASysAssessment(assessment.episodes.first(), null)
    assertThat1(updateAssessmentResponse).isEqualTo(null)
  }

  private fun rsrAssessment() = AssessmentEntity(
    assessmentId = assessmentId,
    episodes = mutableListOf(
      AssessmentEpisodeEntity(
        episodeUuid = episodeUuid,
        episodeId = episodeId2,
        changeReason = "Change of Circs 2",
        assessmentSchemaCode = AssessmentSchemaCode.RSR,
        answers = mutableMapOf()
      )
    )
  )
}
