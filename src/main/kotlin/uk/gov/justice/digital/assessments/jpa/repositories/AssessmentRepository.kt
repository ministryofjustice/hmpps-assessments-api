package uk.gov.justice.digital.assessments.jpa.repositories

import org.springframework.data.domain.Example
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity

@Repository
interface AssessmentRepository : JpaRepository<AssessmentEntity, String> {
    fun findBySupervisionId(supervisionId: String?): Optional<AssessmentEntity> {
        val existingAssessments = findAll(Example.of(AssessmentEntity(supervisionId = supervisionId)))
        if (existingAssessments.isEmpty())
            return Optional.empty();

        return Optional.of(existingAssessments.get(0))
    }
}
