package uk.gov.justice.digital.assessments

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import uk.gov.justice.digital.assessments.api.AnswerDto
import java.util.*

@SpringBootApplication
class HmppsAssessmentApiApplication

fun main(args: Array<String>) {
    runApplication<HmppsAssessmentApiApplication>(*args)
}
