package uk.gov.justice.digital.assessments.config

import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import uk.gov.justice.digital.assessments.restclient.AuthenticatingRestClient
import uk.gov.justice.digital.assessments.restclient.RestClient
import java.time.Duration
import java.util.concurrent.TimeUnit

@Configuration
class WebClientConfig {
  @Value("\${court-case-api.base-url}")
  private lateinit var courtCaseBaseUrl: String

  @Value("\${assessment-update-api.base-url}")
  private lateinit var assessmentUpdateBaseUrl: String

  @Value("\${assessment-api.base-url}")
  private lateinit var assessmentApiBaseUrl: String

  @Value("\${assess-risks-and-needs-api.base-url}")
  private lateinit var assessRisksAndNeedsBaseUrl: String

  @Value("\${community-api.base-url}")
  private lateinit var communityApiBaseUrl: String

  @Value("\${audit.base-url}")
  private lateinit var auditBaseUrl: String

  @Value("\${feature.flags.disable-auth:false}")
  private val disableAuthentication = false

  @Value("\${web.client.connect-timeout-ms}")
  private val connectTimeoutMs: Long = 100000

  @Value("\${web.client.read-timeout-ms}")
  private val readTimeoutMs: Long = 0

  @Value("\${web.client.write-timeout-ms}")
  private val writeTimeoutMs: Long = 0

  @Value("\${web.client.byte-buffer-size}")
  val bufferByteSize: Int = Int.MAX_VALUE

  @Bean
  fun courtCaseWebClient(authorizedClientManager: OAuth2AuthorizedClientManager): AuthenticatingRestClient {
    return AuthenticatingRestClient(
      webClientFactory(courtCaseBaseUrl, authorizedClientManager, bufferByteSize),
      "court-case-client",
      disableAuthentication
    )
  }

  @Bean
  fun assessmentUpdateWebClient(authorizedClientManager: OAuth2AuthorizedClientManager): AuthenticatingRestClient {
    return AuthenticatingRestClient(
      webClientFactory(assessmentUpdateBaseUrl, authorizedClientManager, bufferByteSize),
      "assessment-update-client",
      disableAuthentication
    )
  }

  @Bean
  fun assessmentApiWebClient(authorizedClientManager: OAuth2AuthorizedClientManager): AuthenticatingRestClient {
    return AuthenticatingRestClient(
      webClientFactory(assessmentApiBaseUrl, authorizedClientManager, bufferByteSize),
      "assessment-api-client",
      disableAuthentication
    )
  }

  @Bean
  fun assessRisksAndNeedsApiWebClient(authorizedClientManager: OAuth2AuthorizedClientManager): RestClient {
    return RestClient(
      webClientFactory(assessRisksAndNeedsBaseUrl, authorizedClientManager, bufferByteSize),
      "assess-risks-and-needs-api-client"
    )
  }

  @Bean
  fun communityApiWebClient(authorizedClientManager: OAuth2AuthorizedClientManager): AuthenticatingRestClient {
    return AuthenticatingRestClient(
      webClientFactory(communityApiBaseUrl, authorizedClientManager, bufferByteSize),
      "community-api-client",
      disableAuthentication
    )
  }

  @Bean
  fun auditWebClient(authorizedClientManager: OAuth2AuthorizedClientManager): AuthenticatingRestClient {
    return AuthenticatingRestClient(
      webClientFactory(auditBaseUrl, authorizedClientManager, bufferByteSize),
      "audit-client",
      disableAuthentication
    )
  }

  private fun webClientFactory(
    baseUrl: String,
    authorizedClientManager: OAuth2AuthorizedClientManager,
    bufferByteCount: Int
  ): WebClient {
    val oauth2Client = ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)

    val httpClient = HttpClient.create()
      .doOnConnected {
        it
          .addHandlerLast(ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
          .addHandlerLast(WriteTimeoutHandler(writeTimeoutMs, TimeUnit.MILLISECONDS))
      }
      .responseTimeout(Duration.ofSeconds(connectTimeoutMs))

    return WebClient
      .builder()
      .clientConnector(ReactorClientHttpConnector(httpClient))
      .codecs { it.defaultCodecs().maxInMemorySize(bufferByteCount) }
      .baseUrl(baseUrl)
      .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .apply(oauth2Client.oauth2Configuration())
      .build()
  }
}
