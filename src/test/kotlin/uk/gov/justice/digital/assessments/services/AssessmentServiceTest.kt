package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.DeliusEventType
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.OffenceEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.audit.AuditType
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Tests")
class AssessmentServiceTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val subjectRepository: SubjectRepository = mockk()
  private val authorService: AuthorService = mockk()
  private val questionService: QuestionService = mockk()
  private val episodeService: EpisodeService = mockk()
  private val offenderService: OffenderService = mockk()
  private val auditService: AuditService = mockk()
  private val telemetryService: TelemetryService = mockk()
  private val clock: Clock = Clock.fixed(Instant.now(), ZoneId.of("Europe/London"))

  private val assessmentsService = AssessmentService(
    assessmentRepository,
    subjectRepository,
    authorService,
    questionService,
    episodeService,
    offenderService,
    auditService,
    telemetryService,
    clock
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val assessmentType = AssessmentType.UPW

  private val episodeId1 = 1L
  private val episodeId2 = 2L
  private val episodeId3 = 3L

  private val questionSchemaUuid1 = UUID.randomUUID()
  private val questionSchemaUuid2 = UUID.randomUUID()
  private val questionSchemaUuid3 = UUID.randomUUID()
  private val questionCode1 = "Q1"
  private val questionCode2 = "Q2"
  private val questionCode3 = "question_code_3"
  private val answer1Uuid = UUID.randomUUID()
  private val answer2Uuid = UUID.randomUUID()
  private val answer3Uuid = UUID.randomUUID()

  private val crn = "DX12345A"
  private val eventId = 1L

  private val offenceCode = "Code"
  private val codeDescription = "Code description"
  private val offenceSubCode = "Sub-code"
  private val subCodeDescription = "Sub-code description"

  @BeforeEach
  internal fun setUp() {
    val offenceDto = OffenceDto(
      convictionId = 123,
      convictionIndex = eventId,
      offenceCode = "Code",
      codeDescription = "Code description",
      offenceSubCode = "Sub-code",
      subCodeDescription = "Sub-code description"
    )
    every { offenderService.getOffence(any(), crn, eventId) } returns offenceDto
    val communityOffenderDto = CommunityOffenderDto(dateOfBirth = LocalDate.of(1989, 1, 1).toString())
    every { offenderService.getCommunityOffender(crn) } returns communityOffenderDto
  }

  @Nested
  @DisplayName("episodes")
  inner class CreatingEpisode {
    @Test
    fun `create new episode`() {
      val assessment: AssessmentEntity = mockk()
      justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
      justRun {
        telemetryService.trackAssessmentEvent(
          TelemetryEventType.ASSESSMENT_CREATED,
          any(),
          any(),
          any(),
          any(),
          any()
        )
      }
      every { assessment.assessmentUuid } returns assessmentUuid
      every { assessment.episodes } returns mutableListOf()
      every { offenderService.validateUserAccess(crn) } returns mockk()
      every { assessment.assessmentId } returns 0
      val episodeUuid1 = UUID.randomUUID()
      val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
      every { authorService.getOrCreateAuthor() } returns author
      every { assessment.hasCurrentEpisode() } returns false
      every {
        assessment.newEpisode(
          "Change of Circs",
          assessmentType = assessmentType,
          offence = any(),
          author = author
        )
      } returns AssessmentEpisodeEntity(
        episodeId = episodeId1,
        episodeUuid = episodeUuid1,
        assessment = assessment,
        createdDate = LocalDateTime.now(),
        assessmentType = AssessmentType.UPW,
        offence = OffenceEntity(
          offenceCode = offenceCode,
          codeDescription = codeDescription,
          offenceSubCode = offenceSubCode,
          subCodeDescription = subCodeDescription,
          sentenceDate = LocalDate.of(2000, 1, 1)
        ),
        author = author,
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      every { assessment.subject } returns SubjectEntity(crn = crn, dateOfBirth = LocalDate.now())
      every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(
        offenceCode = offenceCode,
        codeDescription = codeDescription,
        offenceSubCode = offenceSubCode,
        subCodeDescription = subCodeDescription
      )
      every { episodeService.prePopulateEpisodeFromDelius(any(), any()) } returnsArgument 0
      every { episodeService.prePopulateFromPreviousEpisodes(any(), emptyList()) } returnsArgument 0

      val episodeDto = assessmentsService.createNewEpisode(
        assessmentUuid,
        eventId,
        "Change of Circs",
        assessmentType,
        DeliusEventType.EVENT_INDEX
      )

      assertThat(episodeDto.assessmentUuid).isEqualTo(assessmentUuid)
      assertThat(episodeDto.episodeUuid).isEqualTo(episodeUuid1)

      verify(exactly = 1) {
        auditService.createAuditEvent(
          AuditType.ARN_ASSESSMENT_CREATED,
          assessmentUuid,
          episodeDto.episodeUuid,
          crn,
          any(),
          any()
        )
      }
    }

    @Test
    fun `fetch all episodes for an assessment`() {
      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            changeReason = "Change of Circs 1",
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            changeReason = "Change of Circs 2",
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          )
        )
      )

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      val episodeDtos = assessmentsService.getAssessmentEpisodes(assessmentUuid)
      assertThat(episodeDtos).hasSize(2)
    }

    @Test
    fun `should return current episode for assessment given a crn`() {
      // Given
      val episodeUuid = UUID.randomUUID()
      every { subjectRepository.findByCrn(any()) } returns SubjectEntity(
        crn = crn,
        dateOfBirth = LocalDate.now(),
        assessments = listOf(
          AssessmentEntity(
            assessmentId = assessmentId,
            episodes = mutableListOf(
              AssessmentEpisodeEntity(
                episodeId = episodeId1,
                episodeUuid = episodeUuid,
                changeReason = "Change of Circs 1",
                createdDate = LocalDateTime.now(),
                assessmentType = AssessmentType.UPW,
                author = AuthorEntity(
                  userId = "1",
                  userName = "USER",
                  userAuthSource = "source",
                  userFullName = "full name"
                ),
                assessment = AssessmentEntity()
              )
            )
          )
        )
      )

      // When
      val currentEpisode = assessmentsService.getCurrentEpisode(crn)

      // Then
      assertThat(currentEpisode.episodeUuid).isEqualTo(episodeUuid)
    }

    @Test
    fun `throw exception if get episode by crn does not exist`() {
      // given
      every { subjectRepository.findByCrn(any()) } returns null

      // when & then
      assertThatThrownBy { assessmentsService.getCurrentEpisode(crn) }
        .isInstanceOf(EntityNotFoundException::class.java)
        .hasMessage("No current episode found for $crn")
    }

    @Test
    fun `throw exception if assessment does not exist`() {

      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

      assertThatThrownBy { assessmentsService.getAssessmentEpisodes(assessmentUuid) }
        .isInstanceOf(EntityNotFoundException::class.java)
        .hasMessage("Assessment $assessmentUuid not found")
    }

    @Test
    fun `get latest assessment episode`() {
      val episodeUuid2 = UUID.randomUUID()
      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            changeReason = "Change of Circs 1",
            createdDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().minusDays(1),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            episodeUuid = episodeUuid2,
            changeReason = "Change of Circs 2",
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          )
        )
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      val episodeDto = assessmentsService.getCurrentAssessmentEpisode(assessmentUuid)
      assertThat(episodeDto.episodeUuid).isEqualTo(episodeUuid2)
    }

    @Test
    fun `get current episode throws exception if assessment does not exist`() {
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

      assertThatThrownBy { assessmentsService.getCurrentAssessmentEpisode(assessmentUuid) }
        .isInstanceOf(EntityNotFoundException::class.java)
        .hasMessage("Assessment $assessmentUuid not found")
    }

    @Test
    fun `get current episode throws exception if no current episode exists`() {
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

      assertThatThrownBy { assessmentsService.getCurrentAssessmentEpisode(assessmentUuid) }
        .isInstanceOf(EntityNotFoundException::class.java)
        .hasMessage("Assessment $assessmentUuid not found")
    }
  }

  @Nested
  @DisplayName("coded answers")
  inner class CodedAnswers {
    @Test
    fun `fetch answers for all episodes`() {

      setupQuestionCodes()

      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            answers = mutableMapOf(questionCode1 to listOf("YES")),
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            answers = mutableMapOf(questionCode2 to listOf("NO")),
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          )
        )
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      val result = assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)

      assertThat(result.assessmentUuid).isEqualTo(assessmentUuid)
      assertThat(result.answers["Q1"]?.first()?.answerUuid).isEqualTo(answer1Uuid)
      assertThat(result.answers["Q2"]?.first()?.answerUuid).isEqualTo(answer3Uuid)
    }

    @Test
    fun `overwrite older episode answers with newer episode answers`() {
      setupQuestionCodes()

      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            endDate = LocalDateTime.of(2020, 10, 1, 9, 0, 0),
            answers = mutableMapOf(
              questionCode1 to listOf("YES")
            ),
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId3,
            endDate = LocalDateTime.of(2020, 10, 2, 10, 0, 0),
            answers = mutableMapOf(
              questionCode2 to listOf("MAYBE")
            ),
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            endDate = LocalDateTime.of(2020, 10, 2, 9, 0, 0),
            answers = mutableMapOf(
              questionCode2 to listOf("NO")
            ),
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          ),
        )
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      val result = assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)
      assertThat(result.answers["Q1"]?.first()?.answerUuid).isEqualTo(answer1Uuid)
      assertThat(result.answers["Q2"]?.first()?.answerUuid).isEqualTo(answer2Uuid)
    }

    @Test
    fun `overwrite older episode answers latest episode answers`() {
      setupQuestionCodes()
      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            endDate = LocalDateTime.of(2020, 10, 1, 9, 0, 0),
            answers = mutableMapOf(
              questionCode1 to listOf("YES")
            ),
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId3,
            endDate = null,
            answers = mutableMapOf(
              questionCode2 to listOf("NO")
            ),
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          ),
          AssessmentEpisodeEntity(
            episodeId = episodeId2,
            endDate = LocalDateTime.of(2020, 10, 2, 9, 0, 0),
            answers = mutableMapOf(
              questionCode2 to listOf("MAYBE")
            ),
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          ),
        )
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

      val result = assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)
      assertThat(result.answers["Q1"]?.first()?.answerUuid).isEqualTo(answer1Uuid)
      assertThat(result.answers["Q2"]?.first()?.answerUuid).isEqualTo(answer3Uuid)
    }

    @Test
    fun `only fetch coded answers`() {
      setupQuestionCodes()

      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            answers = mutableMapOf(
              questionCode1 to listOf("YES"),
              questionCode3 to listOf("free text")
            ),
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          )
        )
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      val result = assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)

      assertThat(result.assessmentUuid).isEqualTo(assessmentUuid)
      assertThat(result.answers["Q1"]?.first()?.answerUuid).isEqualTo(answer1Uuid)
      assertThat(result.answers).doesNotContainKey("Q2")
    }

    @Test
    fun `throw exception when answer code lookup fails`() {
      setupQuestionCodes()

      val assessment = AssessmentEntity(
        assessmentId = assessmentId,
        episodes = mutableListOf(
          AssessmentEpisodeEntity(
            episodeId = episodeId1,
            answers = mutableMapOf(
              questionCode1 to listOf("NO")
            ),
            createdDate = LocalDateTime.now(),
            assessmentType = AssessmentType.UPW,
            author = AuthorEntity(
              userId = "1",
              userName = "USER",
              userAuthSource = "source",
              userFullName = "full name"
            ),
            assessment = AssessmentEntity()
          )
        )
      )
      every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
      assertThatThrownBy { assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid) }
        .isInstanceOf(IllegalStateException::class.java)
        .hasMessage("Answer Code not found for question $questionSchemaUuid1 answer value NO")
    }
  }

  private fun setupQuestionCodes() {
    val dummy = AnswerGroupEntity(answerGroupId = 99)

    val yes =
      AnswerEntity(answerId = 1, answerUuid = answer1Uuid, value = "YES", answerGroup = dummy)
    val maybe =
      AnswerEntity(answerId = 2, answerUuid = answer2Uuid, value = "MAYBE", answerGroup = dummy)
    val no =
      AnswerEntity(answerId = 3, answerUuid = answer3Uuid, value = "NO", answerGroup = dummy)

    val group1 = AnswerGroupEntity(answerGroupId = 1, answerEntities = listOf(yes))
    val group2 = AnswerGroupEntity(answerGroupId = 2, answerEntities = listOf(maybe, no))

    every { questionService.getAllQuestions() } returns QuestionSchemaEntities(
      listOf(
        QuestionEntity(
          questionId = 1,
          questionUuid = questionSchemaUuid1,
          questionCode = questionCode1,
          answerGroup = group1
        ),
        QuestionEntity(
          questionId = 2,
          questionUuid = questionSchemaUuid2,
          questionCode = questionCode2,
          answerGroup = group2
        ),
        QuestionEntity(
          questionId = 3,
          questionUuid = questionSchemaUuid3,
          questionCode = questionCode3
        )
      )
    )
  }
}
