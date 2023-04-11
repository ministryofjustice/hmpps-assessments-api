package uk.gov.justice.digital.assessments.restclient

import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId
import org.springframework.util.MultiValueMap
import org.springframework.util.MultiValueMapAdapter
import org.springframework.web.reactive.function.client.WebClient

open class AuthenticatingRestClient(
  private val webClient: WebClient,
  private val oauthClient: String,
  private val disableAuth: Boolean,
) {
  fun get(path: String, queryParams: MultiValueMap<String, String>? = MultiValueMapAdapter(emptyMap())): WebClient.RequestHeadersSpec<*> {
    val request = webClient
      .get()
      .uri {
          uriBuilder ->
        uriBuilder.path(path)
          .queryParams(queryParams)
          .build()
      }
      .accept(MediaType.APPLICATION_JSON)
    return if (disableAuth) {
      request
    } else {
      request.attributes(clientRegistrationId(oauthClient))
    }
  }

  fun post(path: String, body: Any): WebClient.RequestHeadersSpec<*> {
    val request = webClient
      .post()
      .uri(path)
      .accept(MediaType.APPLICATION_JSON)
    val authed = if (disableAuth) {
      request
    } else {
      request.attributes(clientRegistrationId(oauthClient))
    }
    return authed.bodyValue(body)
  }

  fun put(path: String, body: Any): WebClient.RequestHeadersSpec<*> {
    val request = webClient
      .put()
      .uri(path)
      .accept(MediaType.APPLICATION_JSON)
    val authed = if (disableAuth) {
      request
    } else {
      request.attributes(clientRegistrationId(oauthClient))
    }
    return authed.bodyValue(body)
  }
}
