package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.groups.GroupSummaryDto
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupSummaryEntity
import java.util.UUID

@DisplayName("Group Summary DTO Tests")
class GroupSummaryDtoTest {
  @Test
  fun `build GroupSummary DTO`() {
    val groupUuid = UUID.randomUUID()
    val summary = object : GroupSummaryEntity {
      override val groupUuid = groupUuid.toString()
      override val groupCode = "Code"
      override val heading = "Heading"
      override val contentCount = 5L
      override val groupCount = 2L
      override val questionCount = 3L
    }

    val dto = GroupSummaryDto.from(summary)

    assertThat(dto.groupId).isEqualTo(groupUuid)
    assertThat(dto.groupCode).isEqualTo(summary.groupCode)
    assertThat(dto.title).isEqualTo(summary.heading)
    assertThat(dto.contentCount).isEqualTo(summary.contentCount)
    assertThat(dto.groupCount).isEqualTo(summary.groupCount)
    assertThat(dto.questionCount).isEqualTo(summary.questionCount)
  }
}
