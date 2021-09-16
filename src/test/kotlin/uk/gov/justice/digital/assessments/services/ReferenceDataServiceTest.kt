package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OASysMappingEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.OASysMappingRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentApiRestClient
import uk.gov.justice.digital.assessments.restclient.assessmentapi.RefElementDto
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDateTime
import java.util.UUID

private val assessmentApiRestClient: AssessmentApiRestClient = mockk()
private val assessmentRepository: AssessmentRepository = mockk()
private val oaSysMappingRepository: OASysMappingRepository = mockk()

private val referenceDataService =
  ReferenceDataService(assessmentApiRestClient, assessmentRepository, oaSysMappingRepository)

private val episodeUuid = UUID.randomUUID()
private val episode = AssessmentEpisodeEntity(
  episodeUuid = episodeUuid,
  oasysSetPk = 123456,
  assessmentSchemaCode = AssessmentSchemaCode.ROSH,
  createdDate = LocalDateTime.now(),
)
private val assessment = AssessmentEntity(episodes = mutableListOf(episode))
private val referenceDataElement = RefElementDto("code", "short description", "long description")
private val referenceData = mapOf("some_field" to listOf(referenceDataElement))

private val questionSchema = QuestionSchemaEntity(questionSchemaId = 1234, questionCode = "question_code_1")
private val questionMapping = OASysMappingEntity(
  mappingId = 1234,
  questionCode = "some_field",
  sectionCode = "some_section",
  questionSchema = questionSchema
)

private val parentQuestionSchema = QuestionSchemaEntity(questionSchemaId = 5678, questionCode = "question_code_2")
private val parentQuestionMapping = OASysMappingEntity(
  mappingId = 5678,
  questionCode = "parent_field",
  sectionCode = "some_section",
  questionSchema = parentQuestionSchema
)

@ExtendWith(MockKExtension::class)
@DisplayName("Reference Data Service Tests")
class ReferenceDataServiceTest {
  @Test
  fun `returns reference data`() {
    every {
      assessmentApiRestClient.getFilteredReferenceData(
        any(),
        any(),
        any(),
        any(),
        any(),
        any()
      )
    } returns referenceData

    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessment
    every { oaSysMappingRepository.findAllByQuestionSchema_QuestionSchemaUuidIn(any()) } returns listOf(
      questionMapping,
      parentQuestionMapping
    )

    val referenceData = referenceDataService.getFilteredReferenceData(
      assessmentUuid = UUID.randomUUID(),
      episodeUuid = episodeUuid,
      questionUuid = questionSchema.questionSchemaUuid,
      parentFields = mapOf(parentQuestionSchema.questionSchemaUuid to "some_value")
    )

    assertThat(referenceData).isEqualTo(referenceData)
  }

  @Test
  fun `passes exceptions thrown by the assessments client`() {
    every {
      assessmentApiRestClient.getFilteredReferenceData(
        any(),
        any(),
        any(),
        any(),
        any(),
        any()
      )
    } throws Exception("Something went wrong in the client")

    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessment
    every { oaSysMappingRepository.findAllByQuestionSchema_QuestionSchemaUuidIn(any()) } returns listOf(
      questionMapping,
      parentQuestionMapping
    )

    assertThatThrownBy {
      referenceDataService.getFilteredReferenceData(
        assessmentUuid = UUID.randomUUID(),
        episodeUuid = episodeUuid,
        questionUuid = questionSchema.questionSchemaUuid,
        parentFields = mapOf(parentQuestionSchema.questionSchemaUuid to "some_value")
      )
    }.hasMessage("Something went wrong in the client")
  }

  @Test
  fun `throws when unable to find the selected episode`() {
    every {
      assessmentApiRestClient.getFilteredReferenceData(
        any(),
        any(),
        any(),
        any(),
        any(),
        any()
      )
    } returns referenceData

    every { assessmentRepository.findByAssessmentUuid(any()) } returns AssessmentEntity(episodes = mutableListOf())
    every { oaSysMappingRepository.findAllByQuestionSchema_QuestionSchemaUuidIn(any()) } returns listOf(
      questionMapping,
      parentQuestionMapping
    )

    assertThatThrownBy {
      referenceDataService.getFilteredReferenceData(
        assessmentUuid = UUID.randomUUID(),
        episodeUuid = episodeUuid,
        questionUuid = questionSchema.questionSchemaUuid,
        parentFields = mapOf(parentQuestionSchema.questionSchemaUuid to "some_value")
      )
    }
      .isInstanceOf(EntityNotFoundException::class.java)
      .hasMessageContaining("Failed to find episode")
  }

  @Test
  fun `throws when unable to find the OASys mapping for the question`() {
    every {
      assessmentApiRestClient.getFilteredReferenceData(
        any(),
        any(),
        any(),
        any(),
        any(),
        any()
      )
    } returns referenceData

    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessment
    every { oaSysMappingRepository.findAllByQuestionSchema_QuestionSchemaUuidIn(any()) } returns listOf(
      parentQuestionMapping
    )

    assertThatThrownBy {
      referenceDataService.getFilteredReferenceData(
        assessmentUuid = UUID.randomUUID(),
        episodeUuid = episodeUuid,
        questionUuid = questionSchema.questionSchemaUuid,
        parentFields = mapOf(parentQuestionSchema.questionSchemaUuid to "some_value")
      )
    }
      .isInstanceOf(EntityNotFoundException::class.java)
      .hasMessageContaining("Failed to find OASys mappings for question schema")
  }

  @Test
  fun `throws when unable to find the OASys mapping for the parent question`() {
    every {
      assessmentApiRestClient.getFilteredReferenceData(
        any(),
        any(),
        any(),
        any(),
        any(),
        any()
      )
    } returns referenceData

    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessment
    every { oaSysMappingRepository.findAllByQuestionSchema_QuestionSchemaUuidIn(any()) } returns listOf(questionMapping)

    assertThatThrownBy {
      referenceDataService.getFilteredReferenceData(
        assessmentUuid = UUID.randomUUID(),
        episodeUuid = episodeUuid,
        questionUuid = questionSchema.questionSchemaUuid,
        parentFields = mapOf(parentQuestionSchema.questionSchemaUuid to "some_value")
      )
    }
      .isInstanceOf(EntityNotFoundException::class.java)
      .hasMessageContaining("Failed to find OASys mappings for parent field")
  }
}
