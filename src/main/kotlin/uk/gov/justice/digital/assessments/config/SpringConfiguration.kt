package uk.gov.justice.digital.assessments.config

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.cfg.DateTimeFeature
import uk.gov.justice.digital.assessments.utils.RequestData

@Configuration
@EnableCaching
class SpringConfiguration : WebMvcConfigurer {

  @Value("\${logging.uris.exclude.regex}")
  private val excludedLogUrls: String? = null

  // Applied to the JsonMapper Spring Boot builds, which serves HTTP requests and responses and is injected elsewhere
  @Bean
  fun jsonMapperCustomizer(): JsonMapperBuilderCustomizer = JsonMapperBuilderCustomizer {
    it
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
      .changeDefaultPropertyInclusion { JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.USE_DEFAULTS) }
  }

  @Bean
  fun createRequestData(): RequestData = RequestData(excludedLogUrls)

  override fun addInterceptors(registry: InterceptorRegistry) {
    registry.addInterceptor(createRequestData())
  }
}
