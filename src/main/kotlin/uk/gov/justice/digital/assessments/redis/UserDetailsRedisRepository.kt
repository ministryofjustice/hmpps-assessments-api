package uk.gov.justice.digital.assessments.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.redis.entities.UserDetails
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException

@Repository
class UserDetailsRedisRepository(val redisTemplate: RedisTemplate<String, UserDetails>) : UserDetailsRepository {
  override fun findByUserId(userId: String): UserDetails {
    val userDetails = redisTemplate?.opsForValue()?.get("user:$userId")
    return userDetails  ?: throw EntityNotFoundException("User Details not found in Redis cache for userId:$userId")
  }
}