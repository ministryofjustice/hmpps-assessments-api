package uk.gov.justice.digital.assessments.utils

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserContext {
  companion object {
    private val authToken: ThreadLocal<String> = ThreadLocal()
    private val authentication: ThreadLocal<Authentication> = ThreadLocal()

    fun getAuthToken(): String { return authToken.get() }
    fun setAuthToken(t: String) { authToken.set(t) }
    fun getAuthentication(): Authentication { return SecurityContextHolder.getContext().authentication }
    fun setAuthentication(a: Authentication) { authentication.set(a) }
  }
}
