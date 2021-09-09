package uk.gov.justice.digital.assessments.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.time.LocalDateTime
import java.util.UUID

class AssessmentUpdateServiceITTest() : IntegrationTest() {
  @Autowired
  internal lateinit var assessmentUpdateService: AssessmentUpdateService

  @Autowired
  internal lateinit var assessmentRepository: AssessmentRepository

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
        emptyList()
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

  private fun rsrAssessment(): AssessmentEntity {
    val assessment = AssessmentEntity()
    assessmentRepository.save(assessment)
    return AssessmentEntity(
      assessmentId = assessmentId,
      episodes = mutableListOf(
        AssessmentEpisodeEntity(
          episodeUuid = episodeUuid,
          assessment = assessment,
          episodeId = episodeId2,
          changeReason = "Change of Circs 2",
          assessmentSchemaCode = AssessmentSchemaCode.RSR,
          answers = mutableMapOf(),
          createdDate = LocalDateTime.now(),
          userId = "AALON"
        )
      )
    )
  }
}
