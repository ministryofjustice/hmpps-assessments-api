package uk.gov.justice.digital.assessments.redis

import uk.gov.justice.digital.assessments.redis.entities.UserDetails

interface UserDetailsRepository {
  fun findByUserId(userId: String): UserDetails
}