package uk.gov.justice.digital.assessments.jpa.entities.assessments

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "offence", schema = "hmppsassessmentsapi")
data class OffenceEntity(
  @Id
  @Column(name = "offence_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val offenceId: Long? = null,

  @Column(name = "offence_uuid")
  val offenceUuid: UUID = UUID.randomUUID(),

  @Column(name = "source")
  val source: String? = null,

  @Column(name = "source_id")
  val sourceId: String? = null,

  @Column(name = "offence_code")
  val offenceCode: String? = null,

  @Column(name = "code_description")
  val codeDescription: String? = null,

  @Column(name = "offence_subcode")
  val offenceSubCode: String? = null,

  @Column(name = "subcode_description")
  val subCodeDescription: String? = null,

  @Column(name = "sentence_date")
  val sentenceDate: LocalDate?,

  @OneToMany(mappedBy = "offence", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
  val episodes: MutableList<AssessmentEpisodeEntity> = mutableListOf(),
) : Serializable
