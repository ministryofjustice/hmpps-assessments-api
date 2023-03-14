package uk.gov.justice.digital.assessments.jpa.entities.assessments

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import uk.gov.justice.digital.assessments.api.answers.CarerCommitmentsAnswerDto
import uk.gov.justice.digital.assessments.api.answers.DisabilityAnswerDto
import uk.gov.justice.digital.assessments.api.answers.EmergencyContactDetailsAnswerDto
import uk.gov.justice.digital.assessments.api.answers.GPDetailsAnswerDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "assessed_episode", schema = "hmppsassessmentsapi")
@TypeDefs(
  TypeDef(name = "json", typeClass = JsonStringType::class),
  TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
)
data class AssessmentEpisodeEntity(

  @Id
  @Column(name = "episode_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val episodeId: Long? = null,

  @Column(name = "episode_uuid")
  val episodeUuid: UUID = UUID.randomUUID(),

  @ManyToOne
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "assessment_uuid")
  val assessment: AssessmentEntity,

  @Column(name = "assessment_schema_code")
  @Enumerated(EnumType.STRING)
  val assessmentType: AssessmentType,

  @Column(name = "oasys_set_pk")
  val oasysSetPk: Long? = null,

  @ManyToOne(cascade = [CascadeType.ALL])
  @JoinColumn(name = "author_uuid", referencedColumnName = "author_uuid")
  var author: AuthorEntity,

  @Column(name = "created_date")
  val createdDate: LocalDateTime = LocalDateTime.now(),

  @Column(name = "end_date")
  var endDate: LocalDateTime? = null,

  @Column(name = "change_reason")
  val changeReason: String? = null,

  @ManyToOne(cascade = [CascadeType.ALL])
  @JoinColumn(name = "offence_uuid", referencedColumnName = "offence_uuid")
  val offence: OffenceEntity? = null,

  @Type(type = "json")
  @Column(columnDefinition = "jsonb", name = "answers")
  var answers: Answers = mutableMapOf(),

  @Column(name = "last_edited_date")
  var lastEditedDate: LocalDateTime = LocalDateTime.now(),

  @Column(name = "closed_date")
  var closedDate: LocalDateTime? = null,
) {
  fun isComplete(): Boolean {
    return endDate != null
  }

  fun complete() {
    endDate = LocalDateTime.now()
  }

  fun isClosed(): Boolean {
    return closedDate != null
  }

  fun close() {
    closedDate = LocalDateTime.now()
  }

  fun addAnswer(questionCode: String, answers: List<Any>?) {
    if (answers != null)
      this.answers[questionCode] = this.answers[questionCode].orEmpty().plus(answers).toSet().toList()
  }

  fun updateFrom(caseDetails: CaseDetails) {

    this.addAnswer("first_name", listOfNotNull(caseDetails.name.forename) as List<Any>)
    this.addAnswer("first_name_aliases", caseDetails.aliases?.map { it.name.forename })
    this.addAnswer("family_name", listOfNotNull(caseDetails.name.surname) as List<Any>)
    this.addAnswer("family_name_aliases", caseDetails.aliases?.map { it.name.surname })
    this.addAnswer("dob", listOfNotNull(caseDetails.dateOfBirth))
    this.addAnswer("dob_aliases", caseDetails.aliases?.map { it.dateOfBirth })
    this.addAnswer("contact_address_building_name", listOfNotNull(caseDetails.mainAddress?.buildingName) as List<Any>)
    this.addAnswer("contact_address_house_number", listOfNotNull(caseDetails.mainAddress?.addressNumber) as List<Any>)
    this.addAnswer("contact_address_street_name", listOfNotNull(caseDetails.mainAddress?.streetName) as List<Any>)
    this.addAnswer("contact_address_district", listOfNotNull(caseDetails.mainAddress?.district) as List<Any>)
    this.addAnswer("contact_address_town_or_city", listOfNotNull(caseDetails.mainAddress?.town) as List<Any>)
    this.addAnswer("contact_address_county", listOfNotNull(caseDetails.mainAddress?.county) as List<Any>)
    this.addAnswer("contact_address_postcode", listOfNotNull(caseDetails.mainAddress?.postcode) as List<Any>)
    this.addAnswer("crn", listOfNotNull(caseDetails.crn) as List<Any>)
    this.addAnswer("pnc", listOfNotNull(caseDetails.pncNumber) as List<Any>)
    this.addAnswer("ethnicity", listOfNotNull(caseDetails.ethnicity) as List<Any>)
    this.addAnswer("gender", listOfNotNull(caseDetails.gender?.uppercase()) as List<Any>)
    this.addAnswer("gender_identity", listOfNotNull(mapGenderIdentity(caseDetails.genderIdentity)) as List<Any>)
    this.addAnswer("language", listOfNotNull(caseDetails.language?.primaryLanguage) as List<Any>)
    this.addAnswer("requires_interpreter", listOfNotNull(caseDetails.language?.requiresInterpreter.toString()) as List<Any>)
    this.addAnswer("contact_email_addresses", listOfNotNull(caseDetails.emailAddress) as List<Any>)
    this.addAnswer(
      "contact_mobile_phone_number",
      caseDetails.phoneNumbers?.filter { it.type == "MOBILE" }?.map { it.number }.orEmpty() as List<Any>
    )
    this.addAnswer(
      "contact_phone_number",
      caseDetails.phoneNumbers?.filter { it.type == "TELEPHONE" }?.map { it.number }.orEmpty() as List<Any>
    )

    val physicalDisabilityCodeTypes = listOf("D", "D02", "RM", "RC", "PC", "VI", "HD")
    this.addAnswer(
      "physical_disability",
      caseDetails.disabilities?.filter {
        it.type.code in physicalDisabilityCodeTypes
      }?.map { it.type.code }.orEmpty()
    )
    this.addAnswer(
      "physical_disability_details",
      caseDetails.disabilities?.filter {
        it.type.code in physicalDisabilityCodeTypes
      }?.map { it.type.description }.orEmpty()
    )
    this.addAnswer(
      "learning_disability",
      caseDetails.disabilities?.filter {
        it.type.code == "LA"
      }?.map { it.type.code }.orEmpty()
    )
    this.addAnswer(
      "learning_disability_details",
      caseDetails.disabilities?.filter {
        it.type.code == "LA"
      }?.map { it.type.description }.orEmpty()
    )
    this.addAnswer(
      "learning_difficulty",
      caseDetails.disabilities?.filter {
        it.type.code == "LD"
      }?.map { it.type.code }.orEmpty()
    )
    this.addAnswer(
      "learning_difficulty_details",
      caseDetails.disabilities?.filter {
        it.type.code == "LD"
      }?.map { it.type.description }.orEmpty()
    )
    val mentalHealthConditionCodeTypes = listOf("D", "D01", "M1")
    this.addAnswer(
      "mental_health_condition",
      caseDetails.disabilities?.filter {
        it.type.code in mentalHealthConditionCodeTypes
      }?.map { it.type.code }.orEmpty()
    )
    this.addAnswer(
      "mental_health_condition_details",
      caseDetails.disabilities?.filter {
        it.type.code in mentalHealthConditionCodeTypes
      }?.map { it.type.description }.orEmpty()
    )
    this.addAnswer(
      "active_disabilities",
      caseDetails.disabilities?.map { DisabilityAnswerDto.from(it) }.orEmpty()
    )

    mapActiveCarerCommitments(caseDetails, this)
    mapAllergies(caseDetails, this)
    mapCarerCommitment(caseDetails, this)
    mapLanguageCommunication(caseDetails, this)
    mapNumeracyConcerns(caseDetails, this)
    mapPregnancy(caseDetails, this)
    mapReadingLiteracy(caseDetails, this)
    mapReadingWriting(caseDetails, this)
    mapGPDetails(caseDetails, this)
    mapEmergencyContacts(caseDetails, this)
  }

  private fun mapGenderIdentity(genderIdentity: String?): String? {
    return genderIdentity?.uppercase()
      ?.replace(' ', '_')
      ?.replace('-', '_')
  }

  private fun mapReadingWriting(
    caseDetails: CaseDetails,
    episode: AssessmentEpisodeEntity
  ) {
    val readingWriting = caseDetails.personalCircumstances?.filter {
      it.type.code == "G"
    }
    val readingWritingDescription = yesNoFieldType(readingWriting?.map { it.subType?.description })
    episode.addAnswer("reading_writing_difficulties", readingWritingDescription)
    episode.addAnswer("reading_writing_difficulties_details", readingWriting?.mapNotNull { it.notes } as List<Any>)
  }

  private fun mapReadingLiteracy(
    caseDetails: CaseDetails,
    episode: AssessmentEpisodeEntity
  ) {
    val readingLiteracyConcerns = caseDetails.personalCircumstances?.filter {
      it.subType?.code == "G01"
    }
    val readingLiteracyConcernsDescription =
      yesNoFieldType(readingLiteracyConcerns?.map { it.subType?.description })
    episode.addAnswer("reading_literacy_concerns", readingLiteracyConcernsDescription)
    episode.addAnswer("reading_literacy_concerns_details", readingLiteracyConcerns?.mapNotNull { it.notes } as List<Any>)
  }

  private fun mapPregnancy(
    caseDetails: CaseDetails,
    episode: AssessmentEpisodeEntity
  ) {
    val pregnancy = caseDetails.personalCircumstances?.filter {
      it.type.code == "PM" && it.subType?.code == "D06"
    }

    val recentlyGivenBirth = caseDetails.personalCircumstances?.filter {
      it.type.code == "PM" && it.subType?.code == "D07"
    }

    episode.addAnswer("pregnancy_pregnant_details", emptyList())
    episode.addAnswer("pregnancy_recently_given_birth_details", emptyList())

    if (pregnancy?.isNotEmpty() == true) {
      episode.addAnswer("pregnancy", listOf("PREGNANT"))
      episode.addAnswer("pregnancy_pregnant_details", pregnancy.mapNotNull { it.notes } as List<Any>)
    } else if (recentlyGivenBirth?.isNotEmpty() == true) {
      episode.addAnswer("pregnancy", listOf("RECENTLY_GIVEN_BIRTH"))
      episode.addAnswer("pregnancy_recently_given_birth_details", recentlyGivenBirth.mapNotNull { it.notes } as List<Any>)
    } else {
      episode.addAnswer("pregnancy", listOf("NO"))
    }
  }

  private fun mapNumeracyConcerns(
    caseDetails: CaseDetails,
    episode: AssessmentEpisodeEntity
  ) {
    val numeracy = caseDetails.personalCircumstances?.filter {
      it.subType?.code == "G02"
    }

    val numeracyConcerns = yesNoFieldType(numeracy?.map { it.subType?.code })
    episode.addAnswer("numeracy_concerns", numeracyConcerns)
    episode.addAnswer("numeracy_concerns_details", numeracy?.mapNotNull { it.notes } as List<Any>)
  }

  private fun mapLanguageCommunication(
    caseDetails: CaseDetails,
    episode: AssessmentEpisodeEntity
  ) {
    val languageCommunication = caseDetails.personalCircumstances?.filter {
      it.subType?.code == "G03"
    }

    val languageCommunicationConcerns = yesNoFieldType(languageCommunication?.map { it.subType?.code })
    episode.addAnswer("language_communication_concerns", languageCommunicationConcerns)
    episode.addAnswer("language_communication_concerns_details", languageCommunication?.mapNotNull { it.notes } as List<Any>)
  }

  private fun mapCarerCommitment(
    caseDetails: CaseDetails,
    episode: AssessmentEpisodeEntity
  ) {
    val carerCommitments =
      caseDetails.personalCircumstances?.filter { it.type.code == "I" }
    val carerCommitmentsDescription = yesNoFieldType(carerCommitments?.mapNotNull { it.subType?.code })

    episode.addAnswer("caring_commitments", carerCommitmentsDescription)
    episode.addAnswer("caring_commitments_details", carerCommitments?.mapNotNull { it.notes } as List<Any>)
  }

  private fun mapAllergies(
    caseDetails: CaseDetails,
    episode: AssessmentEpisodeEntity
  ) {
    val allergies = caseDetails.personalCircumstances?.filter {
      it.type.code == "D" && it.subType?.code == "D03"
    }
    val allergyDescription = yesNoFieldType(allergies?.map { it.subType?.code })
    episode.addAnswer("allergies", allergyDescription)
    episode.addAnswer("allergies_details", allergies?.mapNotNull { it.notes } as List<Any>)
  }

  private fun mapActiveCarerCommitments(
    caseDetails: CaseDetails,
    episode: AssessmentEpisodeEntity
  ) {
    val activeCarerCommitments = caseDetails.personalCircumstances?.filter {
      it.type.code == "I"
    }
    episode.addAnswer("active_carer_commitments", CarerCommitmentsAnswerDto.from(activeCarerCommitments) as List<Any>)
  }

  private fun mapGPDetails(
    caseDetails: CaseDetails,
    episode: AssessmentEpisodeEntity
  ) {
    val gpDetails = caseDetails.personalContacts?.filter { it.relationshipType.code == "RT02" }
    episode.addAnswer("gp_details", GPDetailsAnswerDto.from(gpDetails) as List<Any>)
  }

  private fun mapEmergencyContacts(
    caseDetails: CaseDetails,
    episode: AssessmentEpisodeEntity
  ) {
    val emergencyContacts = caseDetails.personalContacts?.filter { it.relationshipType.code == "ME" }
    episode.addAnswer("emergency_contact_details", EmergencyContactDetailsAnswerDto.from(emergencyContacts) as List<Any>)
  }

  private fun yesNoFieldType(answerList: List<String?>?): List<String> {
    return if (answerList?.isNotEmpty() == true) { listOf("YES") } else { emptyList() }
  }
}
