package uk.gov.justice.digital.assessments.utils

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.web.servlet.HandlerInterceptor
import uk.gov.justice.digital.assessments.config.AuthAwareAuthenticationToken
import uk.gov.justice.digital.assessments.services.exceptions.MdcPropertyException
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class RequestData(excludeUris: String?) : HandlerInterceptor {

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")
  private val excludeUriRegex: Pattern = Pattern.compile(excludeUris)

  override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
    request.setAttribute("startTime", LocalDateTime.now().toString())
    MDC.clear()
    MDC.put(USER_NAME_HEADER, initialiseUserName(request))
    MDC.put(USER_ID_HEADER, initialiseUserId(request))
    MDC.put(USER_AUTH_SOURCE_HEADER, initialiseAuthSource(request))
    MDC.put(USER_FULL_NAME_HEADER, initialiseFullName(request))
    MDC.put(HAS_USER_TOKEN, initialiseIsClientGrantType(request))

    if (excludeUriRegex.matcher(request.requestURI).matches()) {
      MDC.put(SKIP_LOGGING, "true")
    }
    if (log.isTraceEnabled && isLoggingAllowed) {
      log.trace("Request: ${request.method} ${request.requestURI}")
    }
    return true
  }

  override fun afterCompletion(
    request: HttpServletRequest,
    response: HttpServletResponse,
    handler: Any,
    ex: Exception?,
  ) {
    val status = response.status
    val start = LocalDateTime.parse(
      request.getAttribute("startTime").toString(),
    )
    val duration = Duration.between(start, LocalDateTime.now()).toMillis()

    if (log.isTraceEnabled && isLoggingAllowed) {
      log.trace("Response: ${request.method} ${request.requestURI} - Status $status - Start ${start.format(formatter)}, Duration $duration ms")
    }

    MDC.put(REQUEST_DURATION, duration.toString())
    MDC.put(RESPONSE_STATUS, status.toString())
    MDC.clear()
  }

  private fun initialiseUserName(request: HttpServletRequest): String? {
    val userName: String? =
      if (request.userPrincipal != null) (request.userPrincipal as AuthAwareAuthenticationToken).principal.userName else null
    return if (userName.isNullOrEmpty()) null else userName
  }

  private fun initialiseUserId(request: HttpServletRequest): String? {
    val userId: String? =
      if (request.userPrincipal != null) (request.userPrincipal as AuthAwareAuthenticationToken).principal.userId else null
    return if (userId.isNullOrEmpty()) null else userId
  }

  private fun initialiseFullName(request: HttpServletRequest): String? {
    val userName: String? =
      if (request.userPrincipal != null) (request.userPrincipal as AuthAwareAuthenticationToken).principal.name else null
    return if (userName.isNullOrEmpty()) null else userName
  }

  private fun initialiseAuthSource(request: HttpServletRequest): String? {
    val authSource: String? =
      if (request.userPrincipal != null) (request.userPrincipal as AuthAwareAuthenticationToken).principal.authSource else null
    return if (authSource.isNullOrEmpty()) null else authSource
  }

  private fun initialiseIsClientGrantType(request: HttpServletRequest): String? = if (request.userPrincipal != null) (request.userPrincipal as AuthAwareAuthenticationToken).principal.isClientGrantType.toString() else null

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    const val SKIP_LOGGING = "skipLogging"
    const val REQUEST_DURATION = "duration"
    const val RESPONSE_STATUS = "status"
    const val USER_NAME_HEADER = "userName"
    const val USER_AUTH_SOURCE_HEADER = "authSource"
    const val USER_FULL_NAME_HEADER = "userFullName"
    const val USER_ID_HEADER = "userId"
    const val HAS_USER_TOKEN = "hasUserToken"
    val isLoggingAllowed: Boolean = "true" != MDC.get(SKIP_LOGGING)

    fun getUserId(): String = MDC.get(USER_ID_HEADER) ?: throw MdcPropertyException("User Id is mandatory to access this method")

    fun getUserName(): String = MDC.get(USER_NAME_HEADER) ?: throw MdcPropertyException("User Name is mandatory to access this method")

    fun getUserFullName(): String? = MDC.get(USER_FULL_NAME_HEADER)

    fun getUserAuthSource(): String? = MDC.get(USER_AUTH_SOURCE_HEADER)

    fun isClientGrantType(): Boolean = MDC.get(HAS_USER_TOKEN).toBoolean()
  }
}
