package uk.gov.justice.digital.assessments.jpa.entities.assessments

import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "author", schema = "hmppsassessmentsapi")
data class AuthorEntity(
  @Id
  @Column(name = "author_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val authorId: Long? = null,

  // TODO this should be added in the JWT claim by hmpss-auth - https://dsdmoj.atlassian.net/browse/DT-2583
  @Column(name = "author_uuid")
  val authorUuid: UUID = UUID.randomUUID(),

  @Column(name = "user_id")
  var userId: String,

  @Column(name = "user_name")
  var userName: String,

  @Column(name = "user_source")
  var userAuthSource: String? = null,

  @Column(name = "user_full_name")
  var userFullName: String? = null,
) : Serializable
