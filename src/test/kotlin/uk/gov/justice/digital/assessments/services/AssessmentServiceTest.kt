package uk.gov.justice.digital.assessments.services


import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Tests")
class AssessmentServiceTest {

    private val assessmentRepository: AssessmentRepository = mockk()
    private val assessmentsService = AssessmentService(assessmentRepository)


    @Test
    fun `should save new assessment`() {
        every { assessmentRepository.findBySupervisionId(any<String>()) } returns Optional.empty()
        every { assessmentRepository.save(any<AssessmentEntity>()) } returns AssessmentEntity(assessmentId = 1)

        val assessmentDto = assessmentsService.createNewAssessment("SupervisionId")
        verify(exactly = 1) { assessmentRepository.save(any<AssessmentEntity>()) }

    }

}