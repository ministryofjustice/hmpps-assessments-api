package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.OffenceEntity
import java.time.LocalDate
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

    val offenceEntity = OffenceEntity(
      source = "DELIUS",
      sourceId = "1",
      offenceCode = "CODE",
      codeDescription = "Code description",
      offenceSubCode = "SUBCODE",
      subCodeDescription = "Subcode description",
      sentenceDate = LocalDate.of(2000, 1, 1)
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
      offenceEntity
    )

    val episodeDto = AssessmentEpisodeDto.from(episodeEntity)

    assertThat(episodeDto.assessmentUuid).isEqualTo(assessmentEntity.assessmentUuid)
    assertThat(episodeDto.answers).isEmpty()
    assertThat(episodeDto.created).isEqualTo(episodeEntity.createdDate)
    assertThat(episodeDto.ended).isEqualTo(episodeEntity.endDate)
    assertThat(episodeDto.reasonForChange).isEqualTo(episodeEntity.changeReason)
    assertThat(episodeDto.episodeUuid).isEqualTo(episodeEntity.episodeUuid)
    assertThat(episodeDto.oasysAssessmentId).isEqualTo(episodeEntity.oasysSetPk)
    assertThat(episodeDto.offence.offenceCode).isEqualTo(offenceEntity.offenceCode)
    assertThat(episodeDto.offence.codeDescription).isEqualTo(offenceEntity.codeDescription)
    assertThat(episodeDto.offence.offenceSubCode).isEqualTo(offenceEntity.offenceSubCode)
    assertThat(episodeDto.offence.subCodeDescription).isEqualTo(offenceEntity.subCodeDescription)
  }
}
