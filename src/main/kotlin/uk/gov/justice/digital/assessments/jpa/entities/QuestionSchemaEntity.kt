package uk.gov.justice.digital.assessments.jpa.entities

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "QUESTION_SCHEMA")
data class QuestionSchemaEntity (

    @Id
    @Column(name = "question_schema_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val questionSchemaId: Long,

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
    val questionHelpText: String? = null
)