package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaGroupsEntity
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Schema Service Tests")
class AssessmentSchemaServiceTest {
  private val assessmentSchemaRepository: AssessmentSchemaRepository = mockk()
  private val questionService: QuestionService = mockk()

  private val assessmentSchemaService = AssessmentSchemaService(assessmentSchemaRepository, questionService)

  @Test
  fun `get group contents by assessment schema code`() {
    val assessmentSchemaCode = AssessmentSchemaCode.ROSH
    val groupUuid = UUID.randomUUID()
    val group = GroupEntity(
      1L,
      groupUuid,
      "simple-group",
      "Simple Group",
      "subheading",
      "help!",
      LocalDateTime.of(2019, 8, 1, 8, 0),
      null
    )

    val groupsEntity = AssessmentSchemaGroupsEntity(1, UUID.randomUUID(), group)
    every { assessmentSchemaRepository.findByAssessmentSchemaCode(assessmentSchemaCode) } returns AssessmentSchemaEntity(
      1,
      groupsEntity,
      assessmentSchemaCode,
      OasysAssessmentType.SOMETHING_IN_OASYS
    )
    val groupWithContentsDto = GroupWithContentsDto(groupUuid, "simple-group", contents = emptyList())
    every { questionService.getGroupContents(groupUuid) } returns groupWithContentsDto

    val assessmentSchema = assessmentSchemaService.getAssessmentSchema(assessmentSchemaCode)

    assertThat(assessmentSchema).isEqualTo(groupWithContentsDto)
  }

  @Test
  fun `get group contents by assessment schema code throws entity not found if schema doesn't exists`() {
    val assessmentSchemaCode = AssessmentSchemaCode.ROSH
    every { assessmentSchemaRepository.findByAssessmentSchemaCode(assessmentSchemaCode) } returns null

    assertThrows<EntityNotFoundException> { assessmentSchemaService.getAssessmentSchema(assessmentSchemaCode) }
  }

  @Test
  fun `get group sections by assessment schema code`() {
    val assessmentSchemaCode = AssessmentSchemaCode.ROSH
    val groupUuid = UUID.randomUUID()
    val groupCode = "simple-group"
    val group = GroupEntity(
      1L,
      groupUuid,
      groupCode,
      "Simple Group",
      "subheading",
      "help!",
      LocalDateTime.of(2019, 8, 1, 8, 0),
      null
    )

    val groupsEntity = AssessmentSchemaGroupsEntity(1, UUID.randomUUID(), group)
    every { assessmentSchemaRepository.findByAssessmentSchemaCode(assessmentSchemaCode) } returns AssessmentSchemaEntity(
      1,
      groupsEntity,
      assessmentSchemaCode,
      OasysAssessmentType.SOMETHING_IN_OASYS
    )
    val groupSectionsDto = GroupSectionsDto(groupUuid, groupCode, contents = emptyList())
    every { questionService.getGroupSections(groupCode) } returns groupSectionsDto

    val assessmentSchema = assessmentSchemaService.getAssessmentSchemaSummary(assessmentSchemaCode)

    assertThat(assessmentSchema).isEqualTo(groupSectionsDto)
  }

  @Test
  fun `get group sections by assessment schema code throws entity not found if schema doesn't exists`() {
    val assessmentSchemaCode = AssessmentSchemaCode.ROSH
    every { assessmentSchemaRepository.findByAssessmentSchemaCode(assessmentSchemaCode) } returns null

    assertThrows<EntityNotFoundException> { assessmentSchemaService.getAssessmentSchemaSummary(assessmentSchemaCode) }
  }
}