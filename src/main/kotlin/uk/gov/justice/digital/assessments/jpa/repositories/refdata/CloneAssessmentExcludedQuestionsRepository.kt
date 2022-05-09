package uk.gov.justice.digital.assessments.jpa.repositories.refdata

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.refdata.CloneAssessmentExcludedQuestionsEntity

@Repository
interface CloneAssessmentExcludedQuestionsRepository : JpaRepository<CloneAssessmentExcludedQuestionsEntity, String> {

  fun findAllByAssessmentType(assessmentType: AssessmentType): Collection<CloneAssessmentExcludedQuestionsEntity>
}
