package uk.gov.justice.digital.assessments.jpa.repositories.refdata

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionDependencyEntity

@Repository
interface QuestionDependencyRepository : JpaRepository<QuestionDependencyEntity, String>
