package uk.gov.justice.digital.needs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HmppsNeedsApiApplication

fun main(args: Array<String>) {
    runApplication<HmppsNeedsApiApplication>(*args)
}
