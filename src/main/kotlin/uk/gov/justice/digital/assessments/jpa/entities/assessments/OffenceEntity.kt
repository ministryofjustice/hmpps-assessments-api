package uk.gov.justice.digital.assessments.jpa.entities.assessments

import java.io.Serializable
import java.time.LocalDate
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

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
