package uk.gov.justice.digital.assessments.jpa.repositories.assessments

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity

@Repository
interface AuthorRepository : JpaRepository<AuthorEntity, Long> {

  fun findByUserIdAndUserAuthSource(userId: String, userAuthSource: String): AuthorEntity?
}
