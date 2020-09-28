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
import org.springframework.data.repository.findByIdOrNull
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Tests")
class AssessmentServiceTest {

    private val assessmentRepository: AssessmentRepository = mockk()
    private val assessmentsService = AssessmentService(assessmentRepository)


    @Test
    fun `should save new assessment`() {
        every { assessmentRepository.findBySupervisionId(any()) } returns null
        every { assessmentRepository.save(any<AssessmentEntity>()) } returns AssessmentEntity(assessmentId = 1)

        val assessmentDto = assessmentsService.createNewAssessment("SupervisionId")
        verify(exactly = 1) { assessmentRepository.save(any<AssessmentEntity>()) }
    }

    @Test
    fun `should return existing assessment if one exists`() {
        every { assessmentRepository.findBySupervisionId(any<String>()) } returns AssessmentEntity(assessmentId = 1)
        every { assessmentRepository.save(any<AssessmentEntity>()) } returns AssessmentEntity(assessmentId = 2)

        val assessmentDto = assessmentsService.createNewAssessment("SupervisionId")
        assertThat(assessmentDto.assessmentId).isEqualTo(1)

        verify(exactly = 0) { assessmentRepository.save(any<AssessmentEntity>()) }
    }

    @Test
    fun `should create new episode`() {
        val assessment : AssessmentEntity = mockk()
        every { assessment.assessmentId } returns 1
        every { assessment.newEpisode("Change of Circs") } returns AssessmentEpisodeEntity(episodeId =  1, assessment = assessment)
        every { assessmentRepository.findByIdOrNull(1) } returns assessment

        val episodeDto = assessmentsService.createNewEpisode(1, "Change of Circs")

        assertThat(episodeDto?.assessmentId).isEqualTo(1)
        assertThat(episodeDto?.episodeId).isEqualTo(1)
    }

    @Test
    fun `should return all episodes for an assessment`() {
        val assessment : AssessmentEntity = AssessmentEntity( assessmentId = 1,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeId = 1, changeReason = "Change of Circs 1"),
                        AssessmentEpisodeEntity(episodeId = 2, changeReason = "Change of Circs 2"))
        )

        every { assessmentRepository.findByIdOrNull(1) } returns assessment

        val episodeDtos = assessmentsService.getAssessmentEpisodes(1)
        assertThat(episodeDtos).hasSize(2)
    }

    @Test
    fun `get episodes throws exception if assessment does not exist`() {

        every { assessmentRepository.findByIdOrNull(1) } returns null

        assertThatThrownBy { assessmentsService.getAssessmentEpisodes(1) }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasMessage("Assessment 1 not found")
    }

    @Test
    fun `should return latest episode for an assessment`() {
        val assessment : AssessmentEntity = AssessmentEntity( assessmentId = 1,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeId = 1, changeReason = "Change of Circs 1", endDate = LocalDateTime.now().minusDays(1)),
                        AssessmentEpisodeEntity(episodeId = 2, changeReason = "Change of Circs 2"))
        )

        every { assessmentRepository.findByIdOrNull(1) } returns assessment

        val episodeDto = assessmentsService.getCurrentAssessmentEpisode(1)
        assertThat(episodeDto.episodeId).isEqualTo(2)
    }

    @Test
    fun `get current episode throws exception if assessment does not exist`() {

        every { assessmentRepository.findByIdOrNull(1) } returns null

        assertThatThrownBy { assessmentsService.getCurrentAssessmentEpisode(1) }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasMessage("Assessment 1 not found")
    }

    @Test
    fun `get current episode throws exception if no current episode exists`() {

        every { assessmentRepository.findByIdOrNull(1) } returns null

        assertThatThrownBy { assessmentsService.getCurrentAssessmentEpisode(1) }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasMessage("Assessment 1 not found")
    }

}