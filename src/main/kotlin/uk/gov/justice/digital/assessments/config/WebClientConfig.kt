package uk.gov.justice.digital.assessments.config

import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.web.context.annotation.RequestScope
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import uk.gov.justice.digital.assessments.restclient.AuthenticatingRestClient
import uk.gov.justice.digital.assessments.restclient.RestClient
import uk.gov.justice.digital.assessments.utils.UserContext
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
  fun courtCaseWebClient(
    @Qualifier(value = "authorizedClientManager") authorizedClientManager: OAuth2AuthorizedClientManager,
  ): AuthenticatingRestClient {
    val webClient = webClientFactory(
      authorizedClientManager,
      courtCaseBaseUrl,
    )

    return AuthenticatingRestClient(webClient, "court-case-client", disableAuthentication)
  }

  @Bean
  fun assessmentUpdateWebClient(
    @Qualifier(value = "authorizedClientManager") authorizedClientManager: OAuth2AuthorizedClientManager,
  ): AuthenticatingRestClient {
    val webClient = webClientFactory(
      authorizedClientManager,
      assessmentUpdateBaseUrl,
    )

    return AuthenticatingRestClient(webClient, "assessment-update-client", disableAuthentication)
  }

  @Bean
  fun assessmentApiWebClient(
    @Qualifier(value = "authorizedClientManager") authorizedClientManager: OAuth2AuthorizedClientManager,
  ): AuthenticatingRestClient {
    val webClient = webClientFactory(
      authorizedClientManager,
      assessmentApiBaseUrl,
    )

    return AuthenticatingRestClient(webClient, "assessment-api-client", disableAuthentication)
  }

  @Bean
  fun assessRisksAndNeedsApiWebClient(
    @Qualifier(value = "authorizedClientManager") authorizedClientManager: OAuth2AuthorizedClientManager,
  ): RestClient {
    val webClient = webClientFactory(
      authorizedClientManager,
      assessRisksAndNeedsBaseUrl,
    )

    return RestClient(webClient, "assess-risks-and-needs-api-client")
  }

  @Bean
  @RequestScope
  fun communityApiWebClient(
    clientRegistrationRepository: ClientRegistrationRepository,
    authorizedClientRepository: OAuth2AuthorizedClientRepository,
  ): AuthenticatingRestClient {
    val webClient = webClientFactory(
      userContextAwareAuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository),
      communityApiBaseUrl
    )

    return AuthenticatingRestClient(webClient, "community-api-client", disableAuthentication)
  }

  @Bean
  fun auditWebClient(
    @Qualifier(value = "authorizedClientManager") authorizedClientManager: OAuth2AuthorizedClientManager,
  ): AuthenticatingRestClient {
    val webClient = webClientFactory(
      authorizedClientManager,
      auditBaseUrl,
    )

    return AuthenticatingRestClient(webClient, "audit-client", disableAuthentication)
  }

  private fun webClientFactory(
    authorizedClientManager: OAuth2AuthorizedClientManager,
    baseUrl: String,
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
      .codecs { it.defaultCodecs().maxInMemorySize(bufferByteSize) }
      .baseUrl(baseUrl)
      .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .apply(oauth2Client.oauth2Configuration())
      .build()
  }

  @Bean
  fun authorizedClientManager(
    clientRegistrationRepository: ClientRegistrationRepository?,
    authorizedClientRepository: OAuth2AuthorizedClientRepository?
  ): OAuth2AuthorizedClientManager {

    val authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
      .clientCredentials()
      .build()

    val authorizedClientManager = DefaultOAuth2AuthorizedClientManager(
      clientRegistrationRepository,
      authorizedClientRepository
    )

    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)

    return authorizedClientManager
  }

  private fun userContextAwareAuthorizedClientManager(
    clientRegistrationRepository: ClientRegistrationRepository,
    authorizedClientRepository: OAuth2AuthorizedClientRepository,
  ): OAuth2AuthorizedClientManager {
    val defaultClientCredentialsTokenResponseClient = DefaultClientCredentialsTokenResponseClient()

    val authentication = UserContext.getAuthentication()

    defaultClientCredentialsTokenResponseClient.setRequestEntityConverter { grantRequest: OAuth2ClientCredentialsGrantRequest ->
      val converter = CustomOAuth2ClientCredentialsGrantRequestEntityConverter()
      val username = authentication.name
      converter.enhanceWithUsername(grantRequest, username)
    }

    val authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
      .clientCredentials { clientCredentialsGrantBuilder: OAuth2AuthorizedClientProviderBuilder.ClientCredentialsGrantBuilder ->
        clientCredentialsGrantBuilder.accessTokenResponseClient(defaultClientCredentialsTokenResponseClient)
      }
      .build()

    val authorizedClientManager = DefaultOAuth2AuthorizedClientManager(
      clientRegistrationRepository,
      authorizedClientRepository,
    )

    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)

    return authorizedClientManager
  }
}
