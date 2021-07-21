package uk.gov.justice.digital.assessments.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.services.dto.AssessmentEpisodeUpdateErrors
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.utils.RequestData
import java.time.LocalDateTime
import java.util.UUID

class OasysAssessmentUpdateServiceITTest() : IntegrationTest() {
  @Autowired
  internal lateinit var oasysAssessmentUpdateService: OasysAssessmentUpdateService
  private val assessmentId = 1L
  private val episodeId2 = 2L
  private val episodeUuid = UUID.randomUUID()

  @BeforeEach
  fun setup() {
    MDC.put(RequestData.USER_AREA_HEADER, "WWS")
    MDC.put(RequestData.USER_ID_HEADER, "1")
  }

  @Test
  fun `Trying to push to Create OASys Assessment that should not be pushed into Oasys returns null`() {
    val returnAssessmentPk =
      oasysAssessmentUpdateService.createOasysAssessment(
        crn = "DX12340A",
        assessmentSchemaCode = AssessmentSchemaCode.RSR
      )
    assertThat(returnAssessmentPk).isEqualTo(Pair(null, null))
  }

  @Test
  fun `Trying to push to OASys Assessment update that should not be pushed into Oasys returns null`() {
    val assessment = rsrAssessment()
    val updateAssessmentResponse =
      oasysAssessmentUpdateService.updateOASysAssessment(assessment.episodes.first(), mutableMapOf())
    assertThat(updateAssessmentResponse).isEqualTo(null)
  }

  @Test
  fun `Trying to push to OASys Assessment completion that should not be pushed into Oasys returns null`() {
    val assessment = rsrAssessment()

    val updateAssessmentResponse =
      oasysAssessmentUpdateService.completeOASysAssessment(assessment.episodes.first(), null)
    assertThat(updateAssessmentResponse).isEqualTo(null)
  }

  @Test
  fun `Trying to push to Create OASys Assessment that should be pushed into Oasys returns the assessment and offender created`() {
    val returnAssessmentPk =
      oasysAssessmentUpdateService.createOasysAssessment(
        crn = "DX12340A",
        assessmentSchemaCode = AssessmentSchemaCode.ROSH
      )
    assertThat(returnAssessmentPk).isEqualTo(Pair(1, 1))
  }

  @Test
  fun `Trying to push to OASys Assessment update that should be pushed into Oasys returns no errors`() {
    val assessment = roshAssessment()

    val updateAssessmentResponse =
      oasysAssessmentUpdateService.updateOASysAssessment(assessment.episodes.first(), mutableMapOf())

    assertThat(updateAssessmentResponse).isEqualTo(AssessmentEpisodeUpdateErrors())
  }

  @Test
  fun `Trying to push to OASys Assessment completion that should be pushed into Oasys returns no errors`() {
    val assessment = roshAssessment()

    val updateAssessmentResponse =
      oasysAssessmentUpdateService.completeOASysAssessment(assessment.episodes.first(), offenderPk = 1L)

    assertThat(updateAssessmentResponse).isEqualTo(AssessmentEpisodeUpdateErrors())
  }

  private fun roshAssessment() = AssessmentEntity(
    assessmentId = assessmentId,
    episodes = mutableListOf(
      AssessmentEpisodeEntity(
        episodeUuid = episodeUuid,
        assessment = AssessmentEntity(),
        episodeId = episodeId2,
        changeReason = "Change of Circs 2",
        oasysSetPk = 1L,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        answers = mutableMapOf(),
        createdDate = LocalDateTime.now(),
      )
    )
  )
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
