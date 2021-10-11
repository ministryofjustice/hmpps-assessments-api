package uk.gov.justice.digital.assessments.utils.offenderStubResource

import uk.gov.justice.digital.assessments.api.OffenceDto
import java.time.LocalDate

data class OffenderAndOffenceStubDto(
  val crn: String? = null,
  val pnc: String?,
  val familyName: String?,
  val forename1: String?,
  val dateOfBirth: LocalDate?,
  val gender: String?,
  val areaCode: String,
  val offenceCode: String?,
  val codeDescription: String?,
  val offenceSubCode: String?,
  val subCodeDescription: String?,
  val sentenceDate: LocalDate?

) {
  companion object {

    fun from(offender: OffenderStubDto, offence: OffenceDto): OffenderAndOffenceStubDto {
      return OffenderAndOffenceStubDto(
        crn = offender.crn,
        pnc = offender.pnc,
        familyName = offender.familyName,
        forename1 = offender.forename1,
        dateOfBirth = offender.dateOfBirth,
        gender = offender.gender,
        areaCode = offender.areaCode,
        offenceCode = offence.offenceCode,
        offenceSubCode = offence.offenceSubCode,
        codeDescription = offence.codeDescription,
        subCodeDescription = offence.subCodeDescription,
        sentenceDate = offence.sentenceDate
      )
    }
  }
}
