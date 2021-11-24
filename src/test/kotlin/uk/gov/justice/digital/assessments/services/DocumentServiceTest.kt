package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.assessments.api.UploadedUpwDocumentDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.OffenceEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.ExternalService
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import java.time.LocalDate
import java.util.UUID

class DocumentServiceTest {

  private val assessmentService: AssessmentService = mockk()
  private val offenderService: OffenderService = mockk()
  private val communityApiRestClient: CommunityApiRestClient = mockk()
  private val documentService: DocumentService = DocumentService(assessmentService, offenderService, communityApiRestClient)

  @Test
  fun `uploads a document to community-api`() {
    val episodeId = UUID.randomUUID()
    val assessmentId = UUID.randomUUID()
    val crn = "X1234A"
    val convictionId = 987L

    val fileData = multiPartFile()

    every { assessmentService.getEpisode(assessmentId, episodeId) } returns assessmentEpisode(crn, convictionId.toString())
    every { communityApiRestClient.uploadDocumentToDelius(crn, convictionId, fileData) } returns UploadedUpwDocumentDto()

    documentService.uploadUpwDocument(assessmentId, episodeId, fileData)
  }

  @Test
  fun `throws when unable to find an assessment episode`() {
    val episodeId = UUID.randomUUID()
    val assessmentId = UUID.randomUUID()

    val fileData = multiPartFile()

    every { assessmentService.getEpisode(assessmentId, episodeId) } throws EntityNotFoundException("No episode")

    assertThrows<EntityNotFoundException> {
      documentService.uploadUpwDocument(assessmentId, episodeId, fileData)
    }
  }

  @Test
  fun `throws when the source ID does not exist for an offence`() {
    val episodeId = UUID.randomUUID()
    val assessmentId = UUID.randomUUID()
    val crn = "X1234A"

    val fileData = multiPartFile()

    every { assessmentService.getEpisode(assessmentId, episodeId) } returns assessmentEpisode(crn)

    assertThrows<EntityNotFoundException> {
      documentService.uploadUpwDocument(assessmentId, episodeId, fileData)
    }
  }

  @Test
  fun `throws when unable to upload the PDF document`() {
    val episodeId = UUID.randomUUID()
    val assessmentId = UUID.randomUUID()
    val crn = "X1234A"
    val convictionId = 987L

    val fileData = multiPartFile()

    every { assessmentService.getEpisode(assessmentId, episodeId) } returns assessmentEpisode(crn, convictionId.toString())
    every { communityApiRestClient.uploadDocumentToDelius(crn, convictionId, fileData) } throws ExternalApiUnknownException("Something went wrong", HttpMethod.POST, "/foo/bar", ExternalService.COMMUNITY_API)

    assertThrows<ExternalApiUnknownException> {
      documentService.uploadUpwDocument(assessmentId, episodeId, fileData)
    }
  }

  private fun multiPartFile(): MultipartFile {
    return MockMultipartFile(
      "file",
      "filename",
      MediaType.TEXT_PLAIN_VALUE,
      "Test information contained in a document".toByteArray()
    )
  }

  fun assessmentEpisode(crn: String, sourceId: String? = null): AssessmentEpisodeEntity {
    return AssessmentEpisodeEntity(
      assessment = AssessmentEntity(
        subject = SubjectEntity(
          crn = crn,
          dateOfBirth = LocalDate.now()
        )
      ),
      assessmentSchemaCode = AssessmentSchemaCode.UPW,
      author = AuthorEntity(
        userId = "userId",
        userName = "userName"
      ),
      offence = OffenceEntity(
        source = "delius",
        sourceId = sourceId,
        sentenceDate = LocalDate.now()
      )
    )
  }
}
