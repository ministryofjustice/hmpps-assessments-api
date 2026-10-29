package uk.gov.justice.digital.assessments.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import tools.jackson.module.kotlin.KotlinModule
import uk.gov.justice.digital.assessments.config.CacheConstants.ASSESSMENT_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.ASSESSMENT_SUMMARY_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.LIST_QUESTION_GROUPS_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTIONS_FOR_ASSESSMENT_TYPE_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTION_CACHE_KEY
import uk.gov.justice.digital.assessments.config.CacheConstants.QUESTION_GROUP_SECTIONS_CACHE_KEY
import java.time.Duration

@Configuration
class CacheConfiguration {

  @Value("\${cache.ttlDays.referenceData}")
  var referenceDataCacheTtlDays: Long = 1

  @Value("\${cache.ttlMinutes.default}")
  var defaultCacheTtlMinutes: Long = 5

  // Cached values are read back as Any, so type info is written for every value. This matches the
  // Jackson 2 setup it replaces, which also accepted any type when reading.
  private fun valueSerializer(): GenericJacksonJsonRedisSerializer = GenericJacksonJsonRedisSerializer.builder()
    .enableUnsafeDefaultTyping()
    .customize { it.addModule(KotlinModule.Builder().build()).disable(FAIL_ON_UNKNOWN_PROPERTIES) }
    .build()

  // Entries written before the move to Jackson 3 use a different format, so the "v2" prefix makes
  // the new build ignore them; the old entries expire on their normal TTL.
  private fun getDefaultCacheConfiguration(): RedisCacheConfiguration = RedisCacheConfiguration
    .defaultCacheConfig()
    .prefixCacheNameWith("v2:")
    .serializeKeysWith(SerializationPair.fromSerializer(StringRedisSerializer()))
    .serializeValuesWith(SerializationPair.fromSerializer(valueSerializer()))

  @Bean
  fun defaultRedisCacheConfiguration(): RedisCacheConfiguration = getDefaultCacheConfiguration()
    .entryTtl(Duration.ofMinutes(defaultCacheTtlMinutes))

  @Bean
  fun cacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer? = RedisCacheManagerBuilderCustomizer { builder: RedisCacheManagerBuilder ->
    val defaultConfigWithRefDataTtl = getDefaultCacheConfiguration()
      .entryTtl(Duration.ofDays(referenceDataCacheTtlDays))

    arrayOf(
      ASSESSMENT_CACHE_KEY,
      QUESTIONS_FOR_ASSESSMENT_TYPE_CACHE_KEY,
      ASSESSMENT_SUMMARY_CACHE_KEY,
      QUESTION_CACHE_KEY,
      LIST_QUESTION_GROUPS_CACHE_KEY,
      QUESTION_GROUP_SECTIONS_CACHE_KEY,
    ).forEach {
      builder.withCacheConfiguration(it, defaultConfigWithRefDataTtl)
    }
  }
}
