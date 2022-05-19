package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.refdata.ReferenceDataTargetMappingEntity
import java.util.UUID

data class ReferenceDataTargetDto(

  @Schema(description = "", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val questionSchemaUuid: UUID?,

  @Schema(description = "Question Schema primary key", example = "1234")
  val isRequired: Boolean?,

) {

  companion object {

    fun from(referenceDataTargets: Collection<ReferenceDataTargetMappingEntity>?): List<ReferenceDataTargetDto> {
      return referenceDataTargets?.map { from(it) }?.toList().orEmpty()
    }

    fun from(referenceDataTargetMappingEntity: ReferenceDataTargetMappingEntity): ReferenceDataTargetDto {
      return ReferenceDataTargetDto(
        referenceDataTargetMappingEntity.parentQuestion.questionUuid,
        referenceDataTargetMappingEntity.isRequired,
      )
    }
  }
}
