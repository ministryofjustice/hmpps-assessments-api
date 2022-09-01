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
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AssessmentGroupsEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.AssessmentRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Schema Service Tests")
class AssessmentReferenceDataServiceTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val questionService: QuestionService = mockk()

  private val assessmentReferenceDataService = AssessmentReferenceDataService(assessmentRepository, questionService)

  @Test
  fun `get group contents by assessment schema code`() {
    val assessmentType = AssessmentType.ROSH
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

    val groupsEntity = AssessmentGroupsEntity(1, UUID.randomUUID(), group)
    every { assessmentRepository.findByAssessmentType(assessmentType) } returns AssessmentEntity(
      1,
      groupsEntity,
      assessmentType
    )
    val groupWithContentsDto = GroupWithContentsDto(groupUuid, "simple-group", contents = emptyList())
    every { questionService.getGroupContents(groupUuid) } returns groupWithContentsDto

    val assessmentSchema = assessmentReferenceDataService.getAssessmentForAssessmentType(assessmentType)

    assertThat(assessmentSchema).isEqualTo(groupWithContentsDto)
  }

  @Test
  fun `get group contents by assessment schema code throws entity not found if schema doesn't exists`() {
    val assessmentType = AssessmentType.ROSH
    every { assessmentRepository.findByAssessmentType(assessmentType) } returns null

    assertThrows<EntityNotFoundException> { assessmentReferenceDataService.getAssessmentForAssessmentType(assessmentType) }
  }

  @Test
  fun `get group sections by assessment schema code`() {
    val assessmentType = AssessmentType.ROSH
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

    val groupsEntity = AssessmentGroupsEntity(1, UUID.randomUUID(), group)
    every { assessmentRepository.findByAssessmentType(assessmentType) } returns AssessmentEntity(
      1,
      groupsEntity,
      assessmentType
    )
    val groupSectionsDto = GroupSectionsDto(groupUuid, groupCode, contents = emptyList())
    every { questionService.getGroupSections(groupCode) } returns groupSectionsDto

    val assessmentSchema = assessmentReferenceDataService.getAssessmentSummary(assessmentType)

    assertThat(assessmentSchema).isEqualTo(groupSectionsDto)
  }

  @Test
  fun `get group sections by assessment schema code throws entity not found if schema doesn't exists`() {
    val assessmentType = AssessmentType.ROSH
    every { assessmentRepository.findByAssessmentType(assessmentType) } returns null

    assertThrows<EntityNotFoundException> { assessmentReferenceDataService.getAssessmentSummary(assessmentType) }
  }
}
