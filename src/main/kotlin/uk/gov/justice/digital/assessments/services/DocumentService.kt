package uk.gov.justice.digital.assessments.services

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
    val eventId = episode.offence?.sourceId?.toLong()
      ?: throw EntityNotFoundException("Could not retrieve sourceId for assessment: $assessmentId, episode: $episode")

    val crn = episode.assessment.subject?.crn
      ?: throw EntityNotFoundException("Could not retrieve crn for assessment: $assessmentId, episode: $episode")
    val offenceWithConvictionId = offenderService.getOffenceFromConvictionIndex(crn, eventId)

    return communityApiRestClient.uploadDocumentToDelius(crn, offenceWithConvictionId.convictionId!!, fileData)
  }
}
