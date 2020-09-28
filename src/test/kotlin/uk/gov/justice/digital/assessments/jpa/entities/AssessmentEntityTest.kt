package uk.gov.justice.digital.assessments.jpa.entities


import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Entity Tests")
class AssessmentEntityTest {

    @Test
    fun `should create new episode if none exist`() {
        val assessment = AssessmentEntity(assessmentId = 1)
        assertThat(assessment.episodes).hasSize(0)
        val newEpisode = assessment.newEpisode("Change of Circs")
        assertThat(newEpisode.episodeId).isNull()
        assertThat(newEpisode.changeReason).isEqualTo("Change of Circs")
        assertThat(assessment.episodes).hasSize(1)
    }

    @Test
    fun `should return existing episode if exists`() {
        val assessment = AssessmentEntity(assessmentId = 1, episodes = mutableListOf(AssessmentEpisodeEntity(episodeId = 5, changeReason = "Change of Circs" )))
        assertThat(assessment.episodes).hasSize(1)
        val newEpisode = assessment.newEpisode("Another change of Circs")
        assertThat(newEpisode.episodeId).isEqualTo(5)
        assertThat(newEpisode.changeReason).isEqualTo("Change of Circs")
        assertThat(assessment.episodes).hasSize(1)
    }

    @Test
    fun `should return latest episode if one exists`() {
        val assessment = AssessmentEntity(assessmentId = 1, episodes = mutableListOf(AssessmentEpisodeEntity(episodeId = 5, changeReason = "Change of Circs" )))
        val episode = assessment.getCurrentEpisode()
        assertThat(episode?.episodeId).isEqualTo(5)
        assertThat(episode?.changeReason).isEqualTo("Change of Circs")
    }

    @Test
    fun `should return null if no current episode`() {
        val assessment = AssessmentEntity(assessmentId = 1, episodes = mutableListOf(AssessmentEpisodeEntity(episodeId = 5, changeReason = "Change of Circs", endDate = LocalDateTime.now().minusDays(1))))
        val episode = assessment.getCurrentEpisode()
        assertThat(episode).isNull()
    }

}