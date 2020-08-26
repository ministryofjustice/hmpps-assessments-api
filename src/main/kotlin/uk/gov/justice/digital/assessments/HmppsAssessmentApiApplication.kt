package uk.gov.justice.digital.assessments

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HmppsAssessmentApiApplication

fun main(args: Array<String>) {
    runApplication<HmppsAssessmentApiApplication>(*args)
}
