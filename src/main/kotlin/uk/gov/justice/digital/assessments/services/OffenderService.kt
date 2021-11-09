package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.utils.RequestData

@Service
class OffenderService(
  private val communityApiRestClient: CommunityApiRestClient,
  private val courtCaseClient: CourtCaseRestClient
) {

// TODO from ARN-618: Fix Offender service to work with court and delius

//  fun getOffenderAndOffence(crn: String, eventId: Long): OffenderDto {
//    val offender = getOffender(crn)
//    val offence = getOffence(crn, eventId)
//    val address = getOffenderAddress(crn)
//    return offender.copy(offence = offence, address = address)
//  }

  fun getOffender(crn: String): OffenderDto {
    log.info("Requesting offender details for crn: $crn")
    val communityOffenderDto = communityApiRestClient.getOffender(crn)
      ?: throw EntityNotFoundException("No offender found for crn: $crn")
    return OffenderDto.from(communityOffenderDto)
  }

  fun getOffenceFromConvictionIndex(crn: String, eventId: Long): OffenceDto {
    log.info("Requesting offences for crn: $crn")
    val convictions = communityApiRestClient.getConvictions(crn)
      ?: throw EntityNotFoundException("Could not get convictions for crn: $crn")
    val conviction = convictions.find { it.index == eventId }
      ?: throw EntityNotFoundException("Could not get conviction for crn: $crn, event ID: $eventId")
    return OffenceDto.from(conviction)
  }

  fun getOffenceFromConvictionId(crn: String, convictionId: Long): OffenceDto {
    log.info("Requesting offences for crn: $crn")
    val conviction = communityApiRestClient.getConviction(crn, convictionId)
      ?: throw EntityNotFoundException("Could not get convictions for crn: $crn")
    return OffenceDto.from(conviction)
  }

  fun validateUserAccess(crn: String) {
    communityApiRestClient.verifyUserAccess(crn, RequestData.getUserName())
  }

//  fun getOffenderAddress(crn: String): Address? {
//    log.info("Getting most recent court for crn: $crn")
//    val courtSubject = getCourtSubjectByCrn(crn)
//    return if (courtSubject == null) {
//      log.info("No address found for crn: $crn")
//      null
//    } else {
//      val (courtCode, caseNumber) = courtSubject
//      val courtCase = courtCaseClient.getCourtCase(courtCode, caseNumber)
//      Address.from(courtCase?.defendantAddress)
//    }
//  }
//
//  fun getCourtSubjectByCrn(crn: String): Pair<String, String>? {
//    val court = subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT").firstOrNull()
//    return if (court == null) {
//      log.info("No court data found for crn: $crn")
//      null
//    } else {
//      val (courtCode, caseNumber) = court.sourceId!!.split('|')
//      Pair(courtCode, caseNumber)
//    }
//  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
