package uk.gov.justice.digital.assessments.jpa.entities

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ANSWER_SCHEMA")
class AnswerSchemaEntity (

        @Id
        @Column (name = "answer_schema_id")
        val answerSchemaId: Long,

        @Column(name ="answer_schema_uuid")
        val answerSchemaUuid : UUID = UUID.randomUUID(),

        @Column (name = "answer_schema_code")
        val answerSchemaCode: String? = null,

        @ManyToOne
        @JoinColumn (name = "question_schema_uuid", referencedColumnName = "question_schema_uuid")
        val questionSchema: QuestionSchemaEntity? = null,

        @Column (name = "answer_start")
        val answerStart: LocalDateTime? = null,

        @Column (name = "answer_end")
        val answerEnd: LocalDateTime? = null,

        @Column (name = "value")
        val value: String? = null,

        @Column (name = "text")
        val text: String? = null
)