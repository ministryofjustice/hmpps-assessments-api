package uk.gov.justice.digital.assessments.jpa.entities

import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.MDC
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.utils.RequestData
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Entity Tests")
class AssessmentEntityTest {

  private val assessmentID: Long = 1L
  private val episodeId: Long = 1L
  private val assessmentSchemaCode = AssessmentSchemaCode.ROSH

  @BeforeEach
  fun setup() {
    MDC.put(RequestData.USER_NAME_HEADER, "User name")
  }

  @Test
  fun `should create new episode if none exist`() {
    val assessment = AssessmentEntity(assessmentId = assessmentID)
    assertThat(assessment.episodes).hasSize(0)
    val newEpisode = assessment.newEpisode("Change of Circs", assessmentSchemaCode = assessmentSchemaCode)
    assertThat(newEpisode.episodeId).isNull()
    assertThat(newEpisode.changeReason).isEqualTo("Change of Circs")
    assertThat(assessment.episodes).hasSize(1)
  }

  @Test
  fun `should return existing episode if exists`() {
    val assessment = AssessmentEntity(
      assessmentId = assessmentID,
      episodes = mutableListOf(
        AssessmentEpisodeEntity(
          episodeId = episodeId,
          changeReason = "Change of Circs",
          createdDate = LocalDateTime.now(),
          assessmentSchemaCode = AssessmentSchemaCode.ROSH
        )
      )
    )
    assertThat(assessment.episodes).hasSize(1)
    val newEpisode = assessment.newEpisode("Another change of Circs", assessmentSchemaCode = assessmentSchemaCode)
    assertThat(newEpisode.episodeId).isEqualTo(episodeId)
    assertThat(newEpisode.changeReason).isEqualTo("Change of Circs")
    assertThat(assessment.episodes).hasSize(1)
  }

  @Test
  fun `should return latest episode if one exists`() {
    val assessment = AssessmentEntity(
      assessmentId = assessmentID,
      episodes = mutableListOf(
        AssessmentEpisodeEntity(
          episodeId = episodeId,
          changeReason = "Change of Circs",
          createdDate = LocalDateTime.now(),
          assessmentSchemaCode = AssessmentSchemaCode.ROSH
        )
      )
    )
    val episode = assessment.getCurrentEpisode()
    assertThat(episode?.episodeId).isEqualTo(episodeId)
    assertThat(episode?.changeReason).isEqualTo("Change of Circs")
  }

  @Test
  fun `should return null if no current episode`() {
    val assessment = AssessmentEntity(
      assessmentId = assessmentID,
      episodes = mutableListOf(
        AssessmentEpisodeEntity(
          episodeId = episodeId,
          changeReason = "Change of Circs",
          createdDate = LocalDateTime.now(),
          endDate = LocalDateTime.now().minusDays(1),
          assessmentSchemaCode = AssessmentSchemaCode.ROSH
        )
      )
    )
    val episode = assessment.getCurrentEpisode()
    assertThat(episode).isNull()
  }
}
