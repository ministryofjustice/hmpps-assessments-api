package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientException
import uk.gov.justice.digital.assessments.api.Address
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException

@Service
class OffenderService(
  private val communityApiRestClient: CommunityApiRestClient,
  private val subjectRepository: SubjectRepository,
  private val courtCaseClient: CourtCaseRestClient
) {

  fun getOffenderAndOffence(crn: String, convictionId: Long): OffenderDto {
    val offender = getOffender(crn)
    val offence = getOffence(crn, convictionId)
    val address = getOffenderAddress(crn)
    return offender.copy(offence = offence, address = address)
  }

  fun getOffender(crn: String): OffenderDto {
    try {
      log.info("Requesting offender details for crn: $crn")
      val communityOffenderDto = communityApiRestClient.getOffender(crn)
        ?: throw EntityNotFoundException("No offender found for crn: $crn")
      return OffenderDto.from(communityOffenderDto)
    } catch (e: WebClientException) {
      println(e.message)
      throw EntityNotFoundException("No offender found for crn: $crn")
    }
  }

  fun getOffence(crn: String, convictionId: Long): OffenceDto {
    try {
      log.info("Requesting main offence details for crn: $crn, conviction id: $convictionId")
      val conviction = communityApiRestClient.getConviction(crn, convictionId)
        ?: throw EntityNotFoundException("No conviction found for crn: $crn, conviction id: $convictionId")
      return OffenceDto.from(conviction)
    } catch (e: WebClientException) {
      println(e.message)
      throw EntityNotFoundException("No conviction found for crn: $crn, conviction id: $convictionId")
    }
  }

  fun getOffenderAddress(crn: String): Address? {
    log.info("Getting most recent court for crn: $crn")
    val courtSubject = getCourtSubjectByCrn(crn)
    return if (courtSubject == null) {
      log.info("No address found for crn: $crn")
      null
    }
    else {
      val (courtCode, caseNumber) = courtSubject
      val courtCase = courtCaseClient.getCourtCase(courtCode, caseNumber)
      Address.from(courtCase?.defendantAddress)
    }
  }

  fun getCourtSubjectByCrn(crn: String): Pair<String, String>? {
    val court = subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT").firstOrNull()
    return if (court == null) {
      log.info("No court data found for crn: $crn")
      null
    }
    else {
      val (courtCode, caseNumber) = court.sourceId!!.split('|')
      Pair(courtCode, caseNumber)
    }
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
