package uk.gov.justice.digital.assessments.redis.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.redis.UserDetailsRepository
import uk.gov.justice.digital.assessments.redis.entities.UserDetails
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

class UserDetailsRepositoryTest(@Autowired val userDetailsRepository: UserDetailsRepository) : IntegrationTest() {

  @Test
  fun `test add to redis cache and get by id`() {

    redisTemplate.opsForValue().set("user:1", UserDetails("SUPPORT1"))

    val userDetails = userDetailsRepository.findByUserId("1")

    assertThat(userDetails.oasysUserCode).isEqualTo("SUPPORT1")

    redisTemplate.delete(redisTemplate.keys("*"))
  }
}
