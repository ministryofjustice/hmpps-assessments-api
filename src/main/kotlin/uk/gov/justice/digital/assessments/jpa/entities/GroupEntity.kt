package uk.gov.justice.digital.assessments.jpa.entities

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "grouping")
class GroupEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "group_id")
    val groupId: Long,

    @Column(name ="group_uuid")
    val groupUuid : UUID = UUID.randomUUID(),

    @Column (name = "heading")
    val heading: String? = null,

    @Column (name = "subheading")
    val subheading: String? = null,

    @Column (name = "help_text")
    val helpText: String? = null

) : Serializable