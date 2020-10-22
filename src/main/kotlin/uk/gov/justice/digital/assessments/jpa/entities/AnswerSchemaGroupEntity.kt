package uk.gov.justice.digital.assessments.jpa.entities

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ANSWER_SCHEMA_GROUP")
class AnswerSchemaGroupEntity (
        @Id
        @Column (name = "answer_schema_group_id")
        val answerSchemaId: Long,

        @Column(name ="answer_schema_group_uuid")
        val answerSchemaGroupUuid : UUID = UUID.randomUUID(),

        @Column (name = "answer_schema_group_code")
        val answerSchemaGroupCode: String? = null,

        @Column (name = "group_start")
        val groupStart: LocalDateTime? = null,

        @Column (name = "group_end")
        val groupEnd: LocalDateTime? = null,
)