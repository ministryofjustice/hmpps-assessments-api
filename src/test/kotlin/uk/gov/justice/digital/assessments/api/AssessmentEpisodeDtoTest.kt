package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import java.time.LocalDateTime

@DisplayName("Assessment DTO Tests")
class AssessmentEpisodeDtoTest {

    @Test
    fun `Builds valid Assessment Episode DTO`() {

        val assessmentEntity = AssessmentEntity(1,
                "SupervisionId",
                LocalDateTime.of(2019,8,1, 8,0),
                LocalDateTime.of(2020,8,1, 8,0))

        val episodeEntity = AssessmentEpisodeEntity(1, assessmentEntity, "USER",
                LocalDateTime.of(2019,8,1, 8,0), null,
                "Change of Circs")

        val episodeDto = AssessmentEpisodeDto.from(episodeEntity)

        Assertions.assertThat(episodeDto?.assessmentId).isEqualTo(assessmentEntity.assessmentId)
        Assertions.assertThat(episodeDto?.answers).isEmpty()
        Assertions.assertThat(episodeDto?.created).isEqualTo(episodeEntity.createdDate)
        Assertions.assertThat(episodeDto?.ended).isEqualTo(episodeEntity.endDate)
        Assertions.assertThat(episodeDto?.reasonForChange).isEqualTo(episodeEntity.changeReason)
    }

}