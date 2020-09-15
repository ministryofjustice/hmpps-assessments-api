package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import java.time.LocalDateTime

@DisplayName("Assessment DTO Tests")
class AssessmentDtoTest {

    @Test
    fun `Builds valid Assessment DTO`() {

        val assessmentEntity = AssessmentEntity(1,
                "SupervisionId",
                LocalDateTime.of(2019,8,1, 8,0),
                LocalDateTime.of(2020,8,1, 8,0))

        val assessmentDto = AssessmentDto.from(assessmentEntity)

        assertThat(assessmentDto.assessmentId).isEqualTo(assessmentEntity.assessmentId)
        assertThat(assessmentDto.supervisionId).isEqualTo(assessmentEntity.supervisionId)
        assertThat(assessmentDto.createdDate).isEqualTo(assessmentEntity.createdDate)
        assertThat(assessmentDto.completedDate).isEqualTo(assessmentEntity.completedDate)
    }

}