package uk.gov.justice.digital.needs.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenApiConfiguration(buildProperties: BuildProperties) {
  private val version: String = buildProperties.version

  @Bean
  fun customOpenAPI(buildProperties: BuildProperties): OpenAPI? = OpenAPI()
    .info(
      Info().title("HMPPS Offender Needs API").version(version).description(
        "API for managing risk and needs calculations"
      )
        .contact(Contact().name("HMPPS Digital Studio").email("feedback@digital.justice.gov.uk"))
    )
}