package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDate
import java.util.UUID

class SubjectServiceTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val subjectService = SubjectService(assessmentRepository)

  @Test
  fun `get subject for assessment`() {
    val assessmentUuid = UUID.randomUUID()
    val subject = SubjectEntity(
      dateOfBirth = LocalDate.of(1989, 1, 1),
      crn = "X1345",
    )
    every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns AssessmentEntity(subject = subject)

    assertThat(subjectService.getSubjectForAssessment(assessmentUuid)).isEqualTo(subject)
  }

  @Test
  fun `get subject for assessment throws exception if assessment is not found`() {
    val assessmentUuid = UUID.randomUUID()

    every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

    assertThrows<EntityNotFoundException> { (subjectService.getSubjectForAssessment(assessmentUuid)) }
  }

  @Test
  fun `get subject for assessment throws exception if assessment doesn't have a subject`() {
    val assessmentUuid = UUID.randomUUID()

    every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns AssessmentEntity(
      subject = null,
    )

    assertThrows<EntityNotFoundException> { (subjectService.getSubjectForAssessment(assessmentUuid)) }
  }
}
