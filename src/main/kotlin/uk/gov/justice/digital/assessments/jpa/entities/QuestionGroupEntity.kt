package uk.gov.justice.digital.assessments.jpa.entities

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "QUESTION_GROUP")
class QuestionGroupEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_group_id")
    val questionGroupId: Long,

    @Column(name ="question_group_uuid")
    val uuid : UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_schema_uuid", referencedColumnName = "question_schema_uuid")
    val questionSchema : QuestionSchemaEntity,

    @ManyToOne
    @JoinColumn(name = "group_uuid", referencedColumnName = "group_uuid")
    val group : GroupEntity,

    @Column(name = "group_name")
    val groupName : String? = null,

    @Column(name = "display_order")
    val displayOrder : String? = null,

    @Column(name = "mandatory")
    val mandatory : String? = null,

    @Column(name = "validation")
    val validation : String? = null,

    @Column(name = "group_start")
    val groupStart : LocalDateTime? = null,

    @Column(name = "group_end")
    val groupEnd : LocalDateTime? = null
)