package uk.gov.justice.digital.assessments.services


import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Tests")
class AssessmentServiceTest {

    private val assessmentRepository: AssessmentRepository = mockk()
    private val assessmentsService = AssessmentService(assessmentRepository)

    private val assessmentUuid = UUID.randomUUID()
    private val assessmentId = 1L

    private val episodeId1 = 1L
    private val episodeId2 = 2L

    @Test
    fun `should save new assessment`() {
        every { assessmentRepository.findBySupervisionId(any()) } returns null
        every { assessmentRepository.save(any()) } returns AssessmentEntity(assessmentId = assessmentId)

        assessmentsService.createNewAssessment("SupervisionId")
        verify(exactly = 1) { assessmentRepository.save(any()) }
    }

    @Test
    fun `should return existing assessment if one exists`() {
        every { assessmentRepository.findBySupervisionId(any()) } returns AssessmentEntity(assessmentId = assessmentId, assessmentUuid = assessmentUuid)

        val assessmentDto = assessmentsService.createNewAssessment("SupervisionId")
        assertThat(assessmentDto.assessmentUuid).isEqualTo(assessmentUuid)
        verify(exactly = 0) { assessmentRepository.save(any()) }
    }

    @Test
    fun `should create new episode`() {
        val assessment : AssessmentEntity = mockk()
        every { assessment.assessmentUuid } returns assessmentUuid
        every { assessment.assessmentId } returns 0
        every { assessment.newEpisode("Change of Circs") } returns AssessmentEpisodeEntity(episodeId =  episodeId1, assessment = assessment)
        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        val episodeDto = assessmentsService.createNewEpisode(assessmentUuid, "Change of Circs")

        assertThat(episodeDto?.assessmentUuid).isEqualTo(assessmentUuid)
        assertThat(episodeDto?.episodeId).isEqualTo(episodeId1)
    }

    @Test
    fun `should return all episodes for an assessment`() {
        val assessment = AssessmentEntity( assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeId = episodeId1, changeReason = "Change of Circs 1"),
                        AssessmentEpisodeEntity(episodeId = episodeId2, changeReason = "Change of Circs 2"))
        )

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        val episodeDtos = assessmentsService.getAssessmentEpisodes(assessmentUuid)
        assertThat(episodeDtos).hasSize(2)
    }

    @Test
    fun `get episodes throws exception if assessment does not exist`() {

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

        assertThatThrownBy { assessmentsService.getAssessmentEpisodes(assessmentUuid) }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasMessage("Assessment $assessmentUuid not found")
    }

    @Test
    fun `should return latest episode for an assessment`() {
        val assessment = AssessmentEntity( assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeId = episodeId1, changeReason = "Change of Circs 1", endDate = LocalDateTime.now().minusDays(1)),
                        AssessmentEpisodeEntity(episodeId = episodeId2, changeReason = "Change of Circs 2"))
        )

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        val episodeDto = assessmentsService.getCurrentAssessmentEpisode(assessmentUuid)
        assertThat(episodeDto.episodeId).isEqualTo(episodeId2)
    }

    @Test
    fun `get current episode throws exception if assessment does not exist`() {

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

        assertThatThrownBy { assessmentsService.getCurrentAssessmentEpisode(assessmentUuid) }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasMessage("Assessment $assessmentUuid not found")
    }

    @Test
    fun `get current episode throws exception if no current episode exists`() {

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

        assertThatThrownBy { assessmentsService.getCurrentAssessmentEpisode(assessmentUuid) }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasMessage("Assessment $assessmentUuid not found")
    }

}