package uk.gov.justice.digital.assessments.jpa.entities

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
    @JoinColumn(name = "content_uuid", referencedColumnName = "question_schema_uuid")
    val questionSchema : QuestionSchemaEntity,

    @Column(name = "content_type")
    val contentType: String,

    @ManyToOne
    @JoinColumn(name = "group_uuid", referencedColumnName = "group_uuid")
    val group : GroupEntity,

    @Column(name = "display_order")
    val displayOrder : String? = null,

    @Column(name = "mandatory")
    val mandatory : String? = null,

    @Column(name = "validation")
    val validation : String? = null,
)