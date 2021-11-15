package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.UploadedUpwDocumentDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.OffenceEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
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
    val eventId = 456L
    val convictionId = 987L

    val fileData = multiPartFile()

    every { assessmentService.getEpisode(assessmentId, episodeId) } returns assessmentEpisode(crn, eventId.toString())
    every { offenderService.getOffenceFromConvictionIndex(crn, eventId) } returns OffenceDto(convictionId = convictionId)
    every { communityApiRestClient.uploadDocumentToDelius(crn, convictionId, fileData) } returns UploadedUpwDocumentDto()

    documentService.uploadUpwDocument(assessmentId, episodeId, fileData)
  }

  private fun multiPartFile(): MultipartFile {
    return MockMultipartFile(
      "file",
      "filename",
      MediaType.TEXT_PLAIN_VALUE,
      "Test information contained in a document".toByteArray()
    )
  }

  fun assessmentEpisode(crn: String, sourceId: String): AssessmentEpisodeEntity {
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
