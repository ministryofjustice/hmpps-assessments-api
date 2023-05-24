package uk.gov.justice.digital.assessments.config

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.opentelemetry.api.trace.Span
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.text.ParseException
import java.util.Optional

@Configuration
class ClientTrackingConfiguration(private val clientTrackingInterceptor: ClientTrackingInterceptor) : WebMvcConfigurer {
  override fun addInterceptors(registry: InterceptorRegistry) {
    log.info("Adding application insights client tracking interceptor")
    registry.addInterceptor(clientTrackingInterceptor).addPathPatterns("/**")
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}

@Configuration
class ClientTrackingInterceptor : HandlerInterceptor {
  override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
    val addr = retrieveIpFromRemoteAddr(request)
    val currentSpan = Span.current()
    currentSpan.setAttribute("clientIpAddress", addr)

    val token = request.getHeader(HttpHeaders.AUTHORIZATION)
    val bearer = "Bearer "
    if (StringUtils.startsWithIgnoreCase(token, bearer)) {
      try {
        val jwtBody = getClaimsFromJWT(token)
        val user = Optional.ofNullable(jwtBody.getClaim("user_name"))
        user.map { it.toString() }.ifPresent {
          Span.current().setAttribute("username", it) // username in customDimensions
          Span.current().setAttribute("enduser.id", it) // user_Id at the top level of the request
        }
        Span.current().setAttribute("clientId", jwtBody.getClaim("client_id").toString())
      } catch (e: ParseException) {
        log.warn("problem decoding jwt public key for application insights", e)
      }
    }
    return true
  }

  @Throws(ParseException::class)
  private fun getClaimsFromJWT(token: String): JWTClaimsSet =
    SignedJWT.parse(token.replace("Bearer ", "")).jwtClaimsSet

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}

fun retrieveIpFromRemoteAddr(request: HttpServletRequest): String {
  val remoteAddr = request.remoteAddr
  val colonCount = remoteAddr.chars().filter { ch: Int -> ch == ':'.code }.count()
  return if (colonCount == 1L) StringUtils.split(remoteAddr, ":")[0] else remoteAddr
}
