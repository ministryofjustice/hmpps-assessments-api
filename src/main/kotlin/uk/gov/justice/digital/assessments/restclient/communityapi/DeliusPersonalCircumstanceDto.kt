package uk.gov.justice.digital.assessments.restclient.communityapi

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import uk.gov.justice.digital.assessments.api.CarerCommitmentsAnswerDto
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeliusPersonalCircumstanceDto(
  val personalCircumstanceType: PersonalCircumstanceType,
  val personalCircumstanceSubType: PersonalCircumstanceType,
  val notes: String? = null,
  val evidenced: Boolean,
  val isActive: Boolean,
)

data class PersonalCircumstanceType(
  val code: String,
  val description: String,
)

data class DeliusPersonalCircumstancesDto(
  val personalCircumstances: List<DeliusPersonalCircumstanceDto> = emptyList()
) {
  companion object {
    fun from(personalCircumstancesDto: DeliusPersonalCircumstancesDto, episode: AssessmentEpisodeEntity) {
      mapActiveCarerCommitments(personalCircumstancesDto, episode)
      mapAllergies(personalCircumstancesDto, episode)
      mapCarerCommitment(personalCircumstancesDto, episode)
      mapLanguageCommunication(personalCircumstancesDto, episode)
      mapNumeracyConcerns(personalCircumstancesDto, episode)
      mapPregnancy(personalCircumstancesDto, episode)
      mapReadingLiteracy(personalCircumstancesDto, episode)
      mapReadingWriting(personalCircumstancesDto, episode)
    }

    private fun mapReadingWriting(
      personalCircumstancesDto: DeliusPersonalCircumstancesDto,
      episode: AssessmentEpisodeEntity
    ) {
      val readingWriting = personalCircumstancesDto.personalCircumstances.filter {
        it.personalCircumstanceType.code == "G" && it.isActive
      }
      val readingWritingDescription = yesNoFieldType(readingWriting.map { it.personalCircumstanceSubType.description })
      episode.addAnswer("reading_writing_difficulties", readingWritingDescription)
      episode.addAnswer("reading_writing_difficulties_details", readingWriting.map { it.notes as Any })
    }

    private fun mapReadingLiteracy(
      personalCircumstancesDto: DeliusPersonalCircumstancesDto,
      episode: AssessmentEpisodeEntity
    ) {
      val readingLiteracyConcerns = personalCircumstancesDto.personalCircumstances.filter {
        it.personalCircumstanceSubType.code == "G01" && it.isActive
      }
      val readingLiteracyConcernsDescription =
        yesNoFieldType(readingLiteracyConcerns.map { it.personalCircumstanceSubType.description })
      episode.addAnswer("reading_literacy_concerns", readingLiteracyConcernsDescription)
      episode.addAnswer("reading_literacy_concerns_details", readingLiteracyConcerns.map { it.notes as Any })
    }

    private fun mapPregnancy(
      personalCircumstancesDto: DeliusPersonalCircumstancesDto,
      episode: AssessmentEpisodeEntity
    ) {
      val pregnancy = personalCircumstancesDto.personalCircumstances.filter {
        it.personalCircumstanceType.code == "PM" && it.personalCircumstanceSubType.code == "D06"
      }

      val recentlyGivenBirth = personalCircumstancesDto.personalCircumstances.filter {
        it.personalCircumstanceType.code == "PM" && it.personalCircumstanceSubType.code == "D07"
      }

      episode.addAnswer("pregnancy_pregnant_details", emptyList())
      episode.addAnswer("pregnancy_recently_given_birth_details", emptyList())

      if (pregnancy.isNotEmpty()) {
        episode.addAnswer("pregnancy", listOf("PREGNANT"))
        episode.addAnswer("pregnancy_pregnant_details", pregnancy.map { it.notes as Any })
      } else if (recentlyGivenBirth.isNotEmpty()) {
        episode.addAnswer("pregnancy", listOf("RECENTLY_GIVEN_BIRTH"))
        episode.addAnswer("pregnancy_recently_given_birth_details", recentlyGivenBirth.map { it.notes as Any })
      } else {
        episode.addAnswer("pregnancy", listOf("NO"))
      }
    }

    private fun mapNumeracyConcerns(
      personalCircumstancesDto: DeliusPersonalCircumstancesDto,
      episode: AssessmentEpisodeEntity
    ) {
      val numeracy = personalCircumstancesDto.personalCircumstances.filter {
        it.personalCircumstanceSubType.code == "G02" && it.isActive
      }

      val numeracyConcerns = yesNoFieldType(numeracy.map { it.personalCircumstanceSubType.code })
      episode.addAnswer("numeracy_concerns", numeracyConcerns)
      episode.addAnswer("numeracy_concerns_details", numeracy.map { it.notes as Any })
    }

    private fun mapLanguageCommunication(
      personalCircumstancesDto: DeliusPersonalCircumstancesDto,
      episode: AssessmentEpisodeEntity
    ) {
      val languageCommunication = personalCircumstancesDto.personalCircumstances.filter {
        it.personalCircumstanceSubType.code == "G03" && it.isActive
      }

      val languageCommunicationConcerns = yesNoFieldType(languageCommunication.map { it.personalCircumstanceSubType.code })
      episode.addAnswer("language_communication_concerns", languageCommunicationConcerns)
      episode.addAnswer("language_communication_concerns_details", languageCommunication.map { it.notes as Any })
    }

    private fun mapCarerCommitment(
      personalCircumstancesDto: DeliusPersonalCircumstancesDto,
      episode: AssessmentEpisodeEntity
    ) {
      val carerCommitments =
        personalCircumstancesDto.personalCircumstances.filter { it.personalCircumstanceType.code == "I" }
      val carerCommitmentsDescription = yesNoFieldType(carerCommitments.map { it.personalCircumstanceSubType.code })

      episode.addAnswer("caring_commitments", carerCommitmentsDescription)
      episode.addAnswer("caring_commitments_details", carerCommitments.map { it.notes as Any })
    }

    private fun mapAllergies(
      personalCircumstancesDto: DeliusPersonalCircumstancesDto,
      episode: AssessmentEpisodeEntity
    ) {
      val allergies = personalCircumstancesDto.personalCircumstances.filter {
        it.personalCircumstanceType.code == "D" && it.personalCircumstanceSubType.code == "D03"
      }
      val allergyDescription = yesNoFieldType(allergies.map { it.personalCircumstanceSubType.code })
      episode.addAnswer("allergies", allergyDescription)
      episode.addAnswer("allergies_details", allergies.map { it.notes as Any })
    }

    private fun mapActiveCarerCommitments(
      personalCircumstancesDto: DeliusPersonalCircumstancesDto,
      episode: AssessmentEpisodeEntity
    ) {
      val activeCarerCommitments = personalCircumstancesDto.personalCircumstances.filter {
        it.personalCircumstanceType.code == "I" && it.isActive
      }
      episode.addAnswer("active_carer_commitments", CarerCommitmentsAnswerDto.from(activeCarerCommitments) as List<Any>)
    }

    private fun yesNoFieldType(answerList: List<Any>): List<String> {
      return if (answerList.isNotEmpty()) { listOf("YES") } else { emptyList() }
    }
  }
}
