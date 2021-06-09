package uk.gov.justice.digital.assessments.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import uk.gov.justice.digital.assessments.redis.entities.UserDetails
import uk.gov.justice.digital.assessments.utils.RequestData

@Configuration
class SpringConfiguration : WebMvcConfigurer {

  @Value("\${logging.uris.exclude.regex}")
  private val excludedLogUrls: String? = null

  @Bean(name = ["globalObjectMapper"])
  @Primary
  fun objectMapper(): ObjectMapper {
    return ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .setSerializationInclusion(JsonInclude.Include.NON_NULL)
      .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
      .registerModules(Jdk8Module(), JavaTimeModule(), KotlinModule())
  }

  @Bean
  fun createRequestData(): RequestData {
    return RequestData(excludedLogUrls)
  }

  @Bean
  fun redisConnectionFactory(): JedisConnectionFactory {
    val redisStandaloneConfiguration = RedisStandaloneConfiguration("localhost", 6379)
    return JedisConnectionFactory(redisStandaloneConfiguration)
  }

  @Bean
  fun redisTemplate(): RedisTemplate<String, UserDetails>? {
    val template = RedisTemplate<String, UserDetails>()
    template.connectionFactory = redisConnectionFactory()
    template.keySerializer = GenericToStringSerializer(String::class.java)
    val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(UserDetails::class.java)
    jackson2JsonRedisSerializer.setObjectMapper(objectMapper())
    template.valueSerializer = jackson2JsonRedisSerializer
    return template
  }

  override fun addInterceptors(registry: InterceptorRegistry) {
    registry.addInterceptor(createRequestData())
  }
}
