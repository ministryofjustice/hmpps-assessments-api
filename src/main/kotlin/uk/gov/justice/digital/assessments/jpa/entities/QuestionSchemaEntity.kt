package uk.gov.justice.digital.assessments.jpa.entities

import java.io.Serializable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity(name = "question")
@Table(name = "QUESTION_SCHEMA")
class QuestionSchemaEntity(

        @Id
        @Column(name = "question_schema_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val questionSchemaId: Long,

        @Column(name ="question_schema_uuid")
        val questionSchemaUuid : UUID = UUID.randomUUID(),

        @Column(name = "question_code")
        val questionCode: String? = null,

        @Column(name = "OASYS_question_code")
        val oasysQuestionCode: String? = null,

        @Column(name = "question_start")
        val questionStartDate: LocalDateTime? = null,

        @Column(name = "question_end")
        val questionEndDate: LocalDateTime? = null,

        @Column(name = "answer_type")
        val answerType: String? = null,

        @Column(name = "question_text")
        val questionText: String? = null,

        @Column(name = "question_help_text")
        val questionHelpText: String? = null,

        //@OneToMany(mappedBy = "questionSchema", fetch = FetchType.EAGER)
        @Transient
        val answerSchemaEntities: Collection<AnswerSchemaEntity>

) : Serializable