package uk.gov.justice.digital.assessments.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import java.time.LocalDateTime

class AssessmentUtilsTest {
  private val authorEntity = AuthorEntity(
    userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"
  )

  @Test
  fun `should clone placement preference questions if gender_identity is not male`() {
    val episode =
      AssessmentEpisodeEntity(
        episodeId = 1,
        assessmentType = AssessmentType.UPW,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(
          "gender_identity" to listOf("FEMALE"),
          "placement_preference" to listOf("Should not be removed"),
          "placement_preferences" to listOf("Should not be removed"),
          "placement_preference_complete" to listOf("Should not be removed"),
          "some_other_question" to listOf("Should not be removed"),
        )
      )

    AssessmentUtils.removeOrphanedAnswers(episode)

    assertThat(episode.answers["placement_preference"]).isEqualTo(listOf("Should not be removed"))
    assertThat(episode.answers["placement_preferences"]).isEqualTo(listOf("Should not be removed"))
    assertThat(episode.answers["placement_preference_complete"]).isEqualTo(listOf("Should not be removed"))
    assertThat(episode.answers["some_other_question"]).isEqualTo(listOf("Should not be removed"))
  }

  @Test
  fun `should not clone placement preference questions if gender_identity is male`() {
    val episode =
      AssessmentEpisodeEntity(
        episodeId = 1,
        assessmentType = AssessmentType.UPW,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(
          "gender_identity" to listOf("MALE"),
          "placement_preference" to listOf("Should be removed"),
          "placement_preferences" to listOf("Should be removed"),
          "placement_preference_complete" to listOf("Should be removed"),
          "some_other_question" to listOf("Should not be removed"),
        )
      )

    AssessmentUtils.removeOrphanedAnswers(episode)

    assertThat(episode.answers["placement_preference"]).isNull()
    assertThat(episode.answers["placement_preferences"]).isNull()
    assertThat(episode.answers["placement_preference_complete"]).isNull()
    assertThat(episode.answers["some_other_question"]).isEqualTo(listOf("Should not be removed"))
  }
}
