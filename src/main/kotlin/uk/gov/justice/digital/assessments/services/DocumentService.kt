package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import uk.gov.justice.digital.assessments.api.UploadedUpwDocumentDto
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.UUID

@Service
class DocumentService(
  val assessmentService: AssessmentService,
  val offenderService: OffenderService,
  val communityApiRestClient: CommunityApiRestClient
) {
  fun uploadUpwDocument(assessmentId: UUID, episodeId: UUID, fileData: MultipartFile): UploadedUpwDocumentDto? {
    val episode = assessmentService.getEpisode(assessmentId, episodeId)
    val convictionId = episode.offence?.sourceId?.toLong()
      ?: throw EntityNotFoundException("Could not retrieve sourceId for assessment: $assessmentId, episode: $episode")

    val crn = episode.assessment.subject?.crn
      ?: throw EntityNotFoundException("Could not retrieve crn for assessment: $assessmentId, episode: $episode")

    log.info("Uploading document for CRN $crn with conviction ID $convictionId")
    return communityApiRestClient.uploadDocumentToDelius(crn, convictionId, fileData)
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
