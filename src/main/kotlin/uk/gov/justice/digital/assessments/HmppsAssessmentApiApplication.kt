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


   val json = """{ "947a306b-435c-4ad0-b6de-3da4c2272c9a": {
    "freeTextAnswer": "some free text",
    "answers": {
        "19e65a18-dec8-4c2e-9be9-3c586a6679c0": "Some free text",
        "33677951-893f-440a-98a9-215508b144d6": "Some more free text"
    }
}
}"""

    val mapper = ObjectMapper()
            .registerModules(Jdk8Module(), JavaTimeModule(), KotlinModule())
    val output: Map<UUID, AnswerDto> = mapper.readValue(json)


    runApplication<HmppsAssessmentApiApplication>(*args)
}
