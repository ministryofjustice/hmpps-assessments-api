package uk.gov.justice.digital.assessments

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["uk.gov.justice.digital"], exclude = [FlywayAutoConfiguration::class])
class HmppsAssessmentApiApplication

fun main(args: Array<String>) {
  runApplication<HmppsAssessmentApiApplication>(*args)
}
