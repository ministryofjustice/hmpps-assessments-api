package uk.gov.justice.digital.assessments.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
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
import uk.gov.justice.digital.assessments.config.CacheConstants.ASSESSMENT_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.ASSESSMENT_SUMMARY_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.LIST_QUESTION_GROUPS_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTIONS_FOR_ASSESSMENT_TYPE_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTION_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTION_GROUP_CONTENTS_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTION_GROUP_SECTIONS_CACHE_KEY
import java.time.Duration

@Configuration
class CacheConfiguration {

  @Value("\${cache.ttlDays.referenceData}")
  var referenceDataCacheTtlDays: Long = 1

  @Value("\${cache.ttlMinutes.default}")
  var defaultCacheTtlMinutes: Long = 5

  @Bean
  fun defaultRedisCacheConfiguration(): RedisCacheConfiguration {
    return getDefaultCacheConfiguration()
      .entryTtl(Duration.ofMinutes(defaultCacheTtlMinutes))
  }

  private fun getDefaultCacheConfiguration(): RedisCacheConfiguration {
    return RedisCacheConfiguration.defaultCacheConfig()
      .disableCachingNullValues()
      .serializeKeysWith(SerializationPair.fromSerializer(StringRedisSerializer()))
      .serializeValuesWith(SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer(objectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false))))
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
      val defaultConfigWithRefDataTtl = getDefaultCacheConfiguration()
        .entryTtl(Duration.ofDays(referenceDataCacheTtlDays))

      arrayOf(
        ASSESSMENT_CACHE_KEY,
        QUESTIONS_FOR_ASSESSMENT_TYPE_CACHE_KEY,
        ASSESSMENT_SUMMARY_CACHE_KEY,
        QUESTION_CACHE_KEY,
        LIST_QUESTION_GROUPS_CACHE_KEY,
        QUESTION_GROUP_CONTENTS_CACHE_KEY,
        QUESTION_GROUP_SECTIONS_CACHE_KEY
      ).forEach {
        builder.withCacheConfiguration(it, defaultConfigWithRefDataTtl)
      }
    }
  }
}
