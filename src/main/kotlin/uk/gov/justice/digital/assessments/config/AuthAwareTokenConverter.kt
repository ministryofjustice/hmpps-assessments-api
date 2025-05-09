package uk.gov.justice.digital.assessments.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

class AuthAwareTokenConverter : Converter<Jwt, AbstractAuthenticationToken> {
  private val jwtGrantedAuthoritiesConverter: Converter<Jwt, Collection<GrantedAuthority>> =
    JwtGrantedAuthoritiesConverter()

  override fun convert(jwt: Jwt): AbstractAuthenticationToken {
    val claims = jwt.claims
    val principal = findPrincipal(claims)
    val authorities = extractAuthorities(jwt)
    return AuthAwareAuthenticationToken(jwt, principal, authorities)
  }

  private fun isUserGrantType(claims: Map<String, Any?>) = claims.containsKey("grant_type") && claims["grant_type"] == "authorization_code"

  private fun findPrincipal(claims: Map<String, Any?>): Principal = if (isUserGrantType(claims)) {
    Principal(
      claims["user_name"] as String,
      claims["user_id"] as String,
      claims["name"] as String,
      claims["auth_source"] as String,
      false,
    )
  } else {
    Principal(
      userName = claims["sub"] as String,
      userId = claims["client_id"] as String,
      isClientGrantType = true,
    )
  }

  private fun extractAuthorities(jwt: Jwt): Collection<GrantedAuthority> {
    val authorities = mutableListOf<GrantedAuthority>().apply { addAll(jwtGrantedAuthoritiesConverter.convert(jwt)!!) }
    if (jwt.claims.containsKey("authorities")) {
      @Suppress("UNCHECKED_CAST")
      val claimAuthorities = (jwt.claims["authorities"] as Collection<String>).toList()
      authorities.addAll(claimAuthorities.map(::SimpleGrantedAuthority))
    }
    return authorities.toSet()
  }
}

class AuthAwareAuthenticationToken(
  jwt: Jwt,
  private val aPrincipal: Principal,
  authorities: Collection<GrantedAuthority>,
) : JwtAuthenticationToken(jwt, authorities) {
  override fun getPrincipal(): Principal = aPrincipal
}

class Principal(
  val userName: String,
  val userId: String? = null,
  val name: String? = null,
  val authSource: String? = null,
  val isClientGrantType: Boolean = false,
)
