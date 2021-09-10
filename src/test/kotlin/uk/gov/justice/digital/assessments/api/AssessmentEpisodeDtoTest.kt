package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import java.time.LocalDateTime
import java.util.UUID

class AssessmentEpisodeDtoTest {

  @Test
  fun `Builds valid Assessment Episode DTO`() {

    val assessmentId = 1L
    val assessmentEntity = AssessmentEntity(
      assessmentId,
      UUID.randomUUID(),
      LocalDateTime.of(2019, 8, 1, 8, 0),
      LocalDateTime.of(2020, 8, 1, 8, 0)
    )

    val episodeEntity = AssessmentEpisodeEntity(
      assessmentId,
      UUID.randomUUID(),
      assessmentEntity,
      AssessmentSchemaCode.ROSH,
      1L,
      "USER",
      LocalDateTime.of(2019, 8, 1, 8, 0),
      null,
      "Change of Circs",
      "CODE",
      "Code description",
      "SUBCODE",
      "Subcode description"
    )

    val episodeDto = AssessmentEpisodeDto.from(episodeEntity)

    assertThat(episodeDto.assessmentUuid).isEqualTo(assessmentEntity.assessmentUuid)
    assertThat(episodeDto.answers).isEmpty()
    assertThat(episodeDto.created).isEqualTo(episodeEntity.createdDate)
    assertThat(episodeDto.ended).isEqualTo(episodeEntity.endDate)
    assertThat(episodeDto.reasonForChange).isEqualTo(episodeEntity.changeReason)
    assertThat(episodeDto.episodeUuid).isEqualTo(episodeEntity.episodeUuid)
    assertThat(episodeDto.oasysAssessmentId).isEqualTo(episodeEntity.oasysSetPk)
    assertThat(episodeDto.offenceCode).isEqualTo(episodeEntity.offenceCode)
    assertThat(episodeDto.codeDescription).isEqualTo(episodeEntity.codeDescription)
    assertThat(episodeDto.offenceSubCode).isEqualTo(episodeEntity.offenceSubCode)
    assertThat(episodeDto.subCodeDescription).isEqualTo(episodeEntity.subCodeDescription)
  }
}
