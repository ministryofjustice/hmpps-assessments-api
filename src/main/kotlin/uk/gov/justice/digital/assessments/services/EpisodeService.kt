package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity

@Service
class EpisodeService {
    fun prepopulate(episode: AssessmentEpisodeEntity): AssessmentEpisodeEntity {
        return episode
    }
}