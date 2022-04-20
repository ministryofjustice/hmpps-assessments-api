package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Answers
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import uk.gov.justice.digital.assessments.restclient.audit.AuditType
import uk.gov.justice.digital.assessments.services.dto.AssessmentEpisodeUpdateErrors
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import uk.gov.justice.digital.assessments.testutils.Verify
import java.time.LocalDateTime
import java.util.UUID

class AssessmentUpdateServiceTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val episodeRepository: EpisodeRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val riskPredictorsService: RiskPredictorsService = mockk()
  private val assessmentSchemaService: AssessmentSchemaService = mockk()
  private val oasysAssessmentUpdateService: OasysAssessmentUpdateService = mockk()
  private val assessmentService: AssessmentService = mockk()
  private val authorService: AuthorService = mockk()
  private val auditService: AuditService = mockk()
  private val telemetryService: TelemetryService = mockk()

  private val assessmentUpdateService = AssessmentUpdateService(
    assessmentRepository,
    episodeRepository,
    questionService,
    riskPredictorsService,
    oasysAssessmentUpdateService,
    assessmentService,
    authorService,
    auditService,
    telemetryService
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val episodeId2 = 2L

  private val episodeUuid = UUID.randomUUID()

  private val existingQuestionCode = "existing_question_code"

  @BeforeEach
  fun setup() {
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.ROSH) } returns OasysAssessmentType.SHORT_FORM_PSR
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.RSR) } returns OasysAssessmentType.SOMETHING_IN_OASYS
    every { assessmentService.shouldPushToOasys(AssessmentSchemaCode.ROSH) } returns true
  }

  @Nested
  @DisplayName("update episode")
  inner class UpdateAnswers {
    @Test
    fun `add new answers to existing question for an episode`() {
      val answers = mutableMapOf(
        existingQuestionCode to listOf("free text")
      )
      val assessment = assessmentEntity(answers)

      val newQuestionCode = "new_question_code"
      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mutableMapOf(
          newQuestionCode to listOf("trousers")
        )
      )
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessment.episodes.first(),
          updatedAnswers.answers,
        )
      } returns AssessmentEpisodeUpdateErrors()
      every { assessmentRepository.save(any()) } returns null
      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author

      val episodeDto = assessmentUpdateService.updateEpisode(assessment.episodes.first(), updatedAnswers)

      assertThat(episodeDto.answers).hasSize(2)
      Verify.singleAnswer(
        episodeDto.answers[existingQuestionCode]!!,
        "free text"
      )

      Verify.singleAnswer(
        episodeDto.answers[newQuestionCode]!!,
        "trousers"
      )
    }

    @Test
    fun `change an existing answer for an episode`() {
      val answers = mutableMapOf(
        existingQuestionCode to listOf("free text")
      )
      val assessment = assessmentEntity(answers)

      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mutableMapOf(
          existingQuestionCode to listOf("new free text")
        )
      )
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessment.episodes.first(),
          updatedAnswers.answers,
        )
      } returns AssessmentEpisodeUpdateErrors()
      every { assessmentRepository.save(any()) } returns null

      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author

      val episodeDto = assessmentUpdateService.updateEpisode(assessment.episodes.first(), updatedAnswers)

      assertThat(episodeDto.answers).hasSize(1)
      Verify.singleAnswer(
        episodeDto.answers[existingQuestionCode]!!,
        "new free text"
      )
    }

    @Test
    fun `audit updating answers without author change`() {
      val answers = mutableMapOf(
        existingQuestionCode to listOf("free text")
      )
      val assessment = assessmentEntity(answers)
      val newQuestionCode = "new_question_code"
      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mutableMapOf(
          newQuestionCode to listOf("trousers")
        )
      )
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(any(), any())
      } returns AssessmentEpisodeUpdateErrors()
      every { assessmentRepository.save(any()) } returns null
      val author = AuthorEntity(authorUuid = UUID.randomUUID(), userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author

      val episode = assessmentUpdateService.updateEpisode(assessment.episodes.first(), updatedAnswers)
      verify(exactly = 1) {
        auditService.createAuditEvent(
          AuditType.ARN_ASSESSMENT_UPDATED,
          episode.assessmentUuid,
          episode.episodeUuid,
          any(),
          author,
          any()
        )
      }
    }

    @Test
    fun `audit updating answers with author change`() {
      val answers = mutableMapOf(
        existingQuestionCode to listOf("free text")
      )
      val assessment = assessmentEntity(answers)
      val newQuestionCode = "new_question_code"
      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mutableMapOf(
          newQuestionCode to listOf("trousers")
        )
      )
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(any(), any())
      } returns AssessmentEpisodeUpdateErrors()
      every { assessmentRepository.save(any()) } returns null
      val author = AuthorEntity(authorUuid = UUID.randomUUID(), userId = "2", userName = "USER2", userAuthSource = "source", userFullName = "full name 2")
      every { authorService.getOrCreateAuthor() } returns author

      val episode = assessmentUpdateService.updateEpisode(assessment.episodes.first(), updatedAnswers)

      verify(exactly = 1) {
        auditService.createAuditEvent(
          AuditType.ARN_ASSESSMENT_UPDATED,
          episode.assessmentUuid,
          episode.episodeUuid,
          any(),
          author,
          null
        )
      }

      verify(exactly = 1) {
        auditService.createAuditEvent(
          AuditType.ARN_ASSESSMENT_REASSIGNED,
          episode.assessmentUuid,
          episode.episodeUuid,
          any(),
          author,
          mapOf("assignedFrom" to "USER", "assignedTo" to author.userName)
        )
      }
    }

    @Test
    fun `remove answers for an existing question for an episode`() {
      val answers = mutableMapOf(
        existingQuestionCode to listOf("free text", "fruit loops", "biscuits")
      )
      val assessment = assessmentEntity(answers)

      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mutableMapOf(
          existingQuestionCode to listOf("fruit loops", "custard")
        )
      )
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      every {
        oasysAssessmentUpdateService.updateOASysAssessment(
          assessment.episodes.first(),
          updatedAnswers.answers,
        )
      } returns AssessmentEpisodeUpdateErrors()
      every { assessmentRepository.save(any()) } returns null

      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author

      val episodeDto = assessmentUpdateService.updateEpisode(assessment.episodes.first(), updatedAnswers)

      assertThat(episodeDto.answers).hasSize(1)
      Verify.multiAnswers(
        episodeDto.answers[existingQuestionCode]!!,
        "fruit loops",
        "custard"
      )
    }

    @Test
    fun `should complete Oasys assessment when schema code is ROSH`() {
      // Given
      val episodeEntity = createAssessmentEpisodeEntity(AssessmentSchemaCode.ROSH)

      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author
      every { assessmentService.shouldPushToOasys(AssessmentSchemaCode.ROSH) } returns true
      every { episodeRepository.save(any()) } returns episodeEntity
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      every { riskPredictorsService.getPredictorResults(episodeEntity, true) } returns emptyList()
      every { oasysAssessmentUpdateService.completeOASysAssessment(episodeEntity, any()) } returns AssessmentEpisodeUpdateErrors()

      // When
      assessmentUpdateService.completeEpisode(episodeEntity)

      // Then
      verify { oasysAssessmentUpdateService.completeOASysAssessment(any(), any()) }
    }

    @Test
    fun `should not attempt to update Oasys assessment when schema code is UPW`() {
      // Given
      val episodeEntity = createAssessmentEpisodeEntity(AssessmentSchemaCode.UPW)

      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author
      every { assessmentService.shouldPushToOasys(AssessmentSchemaCode.UPW) } returns false
      every { episodeRepository.save(any()) } returns episodeEntity
      every { assessmentRepository.save(any()) } returns episodeEntity.assessment
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }

      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mutableMapOf(
          existingQuestionCode to listOf("fruit loops", "custard")
        )
      )

      // When
      assessmentUpdateService.updateEpisode(episodeEntity, updatedAnswers)

      // Then
      verify(exactly = 0) { oasysAssessmentUpdateService.updateOASysAssessment(any(), any()) }
    }

    @Test
    fun `should update Oasys assessment when schema code is ROSH`() {
      // Given
      val episodeEntity = createAssessmentEpisodeEntity(AssessmentSchemaCode.ROSH)

      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author
      every { assessmentService.shouldPushToOasys(AssessmentSchemaCode.ROSH) } returns true
      every { episodeRepository.save(any()) } returns episodeEntity
      every { assessmentRepository.save(any()) } returns episodeEntity.assessment
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      every { oasysAssessmentUpdateService.updateOASysAssessment(any(), any()) } returns AssessmentEpisodeUpdateErrors()

      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mutableMapOf(
          existingQuestionCode to listOf("fruit loops", "custard")
        )
      )

      // When
      assessmentUpdateService.updateEpisode(episodeEntity, updatedAnswers)

      // Then
      verify() { oasysAssessmentUpdateService.updateOASysAssessment(any(), any()) }
    }

    @Test
    fun `should not update Oasys assessment when schema code is UPW`() {
      // Given
      val episodeEntity = createAssessmentEpisodeEntity(AssessmentSchemaCode.UPW)

      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author
      every { assessmentService.shouldPushToOasys(AssessmentSchemaCode.UPW) } returns false
      every { episodeRepository.save(any()) } returns episodeEntity
      every { assessmentRepository.save(any()) } returns episodeEntity.assessment
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }

      val updatedAnswers = UpdateAssessmentEpisodeDto(
        mutableMapOf(
          existingQuestionCode to listOf("fruit loops", "custard")
        )
      )

      // When
      assessmentUpdateService.updateEpisode(episodeEntity, updatedAnswers)

      // Then
      verify(exactly = 0) { oasysAssessmentUpdateService.updateOASysAssessment(any(), any()) }
    }

    @Test
    fun `should not attempt to complete Oasys assessment when schema code is UPW`() {
      // Given
      val episodeEntity = createAssessmentEpisodeEntity(AssessmentSchemaCode.UPW)

      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author
      every { assessmentService.shouldPushToOasys(AssessmentSchemaCode.UPW) } returns false
      every { episodeRepository.save(any()) } returns episodeEntity
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      every { riskPredictorsService.getPredictorResults(episodeEntity, true) } returns emptyList()

      // When
      assessmentUpdateService.completeEpisode(episodeEntity)

      // Then
      verify(exactly = 0) { oasysAssessmentUpdateService.completeOASysAssessment(any(), any()) }
    }

    @Test
    fun `do not update a completed episode`() {
      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeUuid = episodeUuid,
            episodeId = episodeId2,
            endDate = LocalDateTime.now().minusDays(1),
            changeReason = "Change of Circs 2",
            answers = mutableMapOf(
              existingQuestionCode to listOf("free text")
            ),
            assessment = AssessmentEntity(assessmentUuid = assessmentUuid),
            createdDate = LocalDateTime.now(),
            assessmentSchemaCode = AssessmentSchemaCode.ROSH,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
          )
        )
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      assertThatThrownBy {
        assessmentUpdateService.updateEpisode(
          assessment.episodes.first(),
          UpdateAssessmentEpisodeDto(answers = mutableMapOf()),
        )
      }
        .isInstanceOf(UpdateClosedEpisodeException::class.java)
        .hasMessage("Cannot update a closed or completed Episode $episodeUuid for assessment $assessmentUuid")
    }
  }

  private fun createAssessmentEpisodeEntity(schemaCode: AssessmentSchemaCode): AssessmentEpisodeEntity {
    val answers = mutableMapOf(
      existingQuestionCode to listOf("free text")
    )
    val episodeEntity = AssessmentEpisodeEntity(
      episodeUuid = episodeUuid,
      episodeId = episodeId2,
      changeReason = "Change of Circs 2",
      answers = answers,
      createdDate = LocalDateTime.now(),
      assessmentSchemaCode = schemaCode,
      author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      assessment = AssessmentEntity()
    )
    return episodeEntity
  }

  private fun assessmentEntity(answers: Answers): AssessmentEntity {
    return AssessmentEntity(
      assessmentId = assessmentId,
      episodes = mutableListOf(
        AssessmentEpisodeEntity(
          episodeUuid = episodeUuid,
          episodeId = episodeId2,
          changeReason = "Change of Circs 2",
          answers = answers,
          createdDate = LocalDateTime.now(),
          assessmentSchemaCode = AssessmentSchemaCode.ROSH,
          author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
          assessment = AssessmentEntity()
        ),
      )
    )
  }
}
