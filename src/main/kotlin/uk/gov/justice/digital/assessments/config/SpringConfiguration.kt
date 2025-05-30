package uk.gov.justice.digital.assessments.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import uk.gov.justice.digital.assessments.utils.RequestData

@Configuration
@EnableCaching
class SpringConfiguration : WebMvcConfigurer {

  @Value("\${logging.uris.exclude.regex}")
  private val excludedLogUrls: String? = null

  @Bean(name = ["globalObjectMapper"])
  @Primary
  fun objectMapper(): ObjectMapper = ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
    .registerModule(Jdk8Module())
    .registerModule(JavaTimeModule())
    .registerKotlinModule()

  @Bean
  fun createRequestData(): RequestData = RequestData(excludedLogUrls)

  override fun addInterceptors(registry: InterceptorRegistry) {
    registry.addInterceptor(createRequestData())
  }
}
