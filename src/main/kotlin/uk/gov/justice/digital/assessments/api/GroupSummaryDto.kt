package uk.gov.justice.digital.assessments.api;

import uk.gov.justice.digital.assessments.jpa.entities.GroupSummaryEntity;

class GroupSummaryDto(

) {
  companion object {
    fun from(entity: GroupSummaryEntity): GroupSummaryDto {
      return GroupSummaryDto()
    }
  }
}
