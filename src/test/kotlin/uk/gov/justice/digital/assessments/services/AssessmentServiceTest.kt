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
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.OffenceEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.DeliusIntegrationRestClient
import uk.gov.justice.digital.assessments.restclient.audit.AuditType
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Address
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Name
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.PersonalContact
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.RelationshipType
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
  private val episodeService: EpisodeService = mockk()
  private val offenderService: OffenderService = mockk()
  private val auditService: AuditService = mockk()
  private val telemetryService: TelemetryService = mockk()
  private val deliusIntegrationRestClient: DeliusIntegrationRestClient = mockk()
  private val clock: Clock = Clock.fixed(Instant.now(), ZoneId.of("Europe/London"))
  private val episodeRepository: EpisodeRepository = mockk()

  private val assessmentsService = AssessmentService(
    assessmentRepository,
    subjectRepository,
    authorService,
    episodeService,
    offenderService,
    auditService,
    telemetryService,
    clock,
    episodeRepository,
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val assessmentType = AssessmentType.UPW

  private val episodeId1 = 1L
  private val episodeId2 = 2L

  private val crn = "DX12345A"
  private val eventId = 1L

  private val offenceCode = "Code"
  private val codeDescription = "Code description"
  private val offenceSubCode = "Sub-code"
  private val subCodeDescription = "Sub-code description"

  @BeforeEach
  internal fun setUp() {
    every { offenderService.getDeliusCaseDetails(crn, eventId) } returns caseDetails()
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
      every { episodeService.prePopulateEpisodeFromDelius(any(), any()) } returnsArgument 0
      every { episodeService.prePopulateFromPreviousEpisodes(any(), emptyList()) } returnsArgument 0

      every { deliusIntegrationRestClient.getCaseDetails(crn, eventId) } returns caseDetails()
      val episodeDto = assessmentsService.createNewEpisode(
        assessmentUuid,
        eventId,
        "Change of Circs",
        assessmentType,
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

  private fun caseDetails(): CaseDetails {
    return CaseDetails(
      crn = "crn",
      name = Name(
        forename = "forename",
        middleName = "middlename",
        surname = "surname"
      ),
      dateOfBirth = LocalDate.of(1989, 1, 1),
      genderIdentity = "PREFER TO SELF DESCRIBE",

      mainAddress = Address(
        buildingName = "HMPPS Digital Studio",
        addressNumber = "32",
        district = "Sheffield City Centre",
        county = "South Yorkshire",
        postcode = "S3 7BS",
        town = "Sheffield"
      ),
      personalContacts = listOf(
        PersonalContact(
          relationship = "GP",
          relationshipType = RelationshipType(
            code = "RT02",
            description = "Primary GP"
          ),
          name = Name(
            forename = "Charles",
            surname = "Europe"
          ),
          mobileNumber = "07123456789",
          address = Address(
            addressNumber = "32",
            streetName = "Scotland Street",
            district = "Sheffield",
            town = "Sheffield",
            county = "South Yorkshire",
            postcode = "S3 7DQ"
          )
        ),
        PersonalContact(
          relationship = "Emergency Contact",
          relationshipType = RelationshipType(
            code = "ME",
            description = "Father"
          ),
          name = Name(
            forename = "UPW",
            surname = "Testing"
          ),
          telephoneNumber = "020 2000 0000",
          address = Address(
            buildingName = "Petty France",
            addressNumber = "102",
            streetName = "Central London",
            district = "London",
            town = "London",
            county = "London",
            postcode = "SW1H 9AJ"
          )
        )
      )
    )
  }
}
