package uk.gov.justice.digital.assessments.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class CacheConfiguration {

  @Value("\${referenceData.cacheTtlDays}")
  var referenceDataCacheTtlDays: Long = 1

  @Bean
  fun defaultRedisCacheConfiguration(): RedisCacheConfiguration {
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

  @Bean
  fun cacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer? {
    return RedisCacheManagerBuilderCustomizer { builder: RedisCacheManagerBuilder ->
      run {
        val defaultConfigWithRefDataTtl = defaultRedisCacheConfiguration()
          .entryTtl(Duration.ofDays(referenceDataCacheTtlDays))

        arrayOf(
          "AssessmentSchemaService:predictorsForAssessment",
          "AssessmentSchemaService:assessmentSchema",
          "AssessmentSchemaService:questionsForSchemaCode",
          "AssessmentSchemaService:assessmentSchemaSummary",
          "QuestionService:questionSchema",
          "QuestionService:listGroups",
          "QuestionService:getGroupContents",
          "QuestionService:getGroupSections"
        ).forEach {
          builder
            .withCacheConfiguration(
              it,
              defaultConfigWithRefDataTtl
            )
        }
      }
    }
  }
}
