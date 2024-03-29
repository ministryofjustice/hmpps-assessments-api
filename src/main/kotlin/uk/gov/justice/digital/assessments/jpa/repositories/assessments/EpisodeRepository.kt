package uk.gov.justice.digital.assessments.jpa.repositories.assessments

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import java.util.UUID

@Repository
interface EpisodeRepository : JpaRepository<AssessmentEpisodeEntity, Long> {
  fun findByEpisodeUuid(episodeUuid: UUID): AssessmentEpisodeEntity?
}
