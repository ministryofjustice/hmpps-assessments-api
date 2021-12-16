package uk.gov.justice.digital.assessments.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class CacheConfiguration {
  @Bean
  fun redisCacheConfiguration(): RedisCacheConfiguration {
    return RedisCacheConfiguration.defaultCacheConfig()
      .disableCachingNullValues()
      .serializeKeysWith(SerializationPair.fromSerializer(StringRedisSerializer()))
      .serializeValuesWith(SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer(objectMapper())))
  }

  private fun objectMapper(): ObjectMapper {
    return ObjectMapper()
      .registerModules(Jdk8Module(), JavaTimeModule(), KotlinModule())
      .apply {
        activateDefaultTyping(polymorphicTypeValidator, ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY)
      }
  }
}
