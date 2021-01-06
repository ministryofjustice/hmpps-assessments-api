package uk.gov.justice.digital.assessments.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.channel.ChannelOption
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
import java.util.concurrent.TimeUnit

@Configuration
class WebClientConfig {
    @Value("\${court-case-api.base-url}")
    private lateinit var courtCaseBaseUrl: String

    @Value("\${assessment-update-api.base-url}")
    private lateinit var assessmentUpdateBaseUrl: String

    @Value("\${web.client.connect-timeout-ms}")
    private val connectTimeoutMs: Int? = null

    @Value("\${web.client.read-timeout-ms}")
    private val readTimeoutMs: Long = 0

    @Value("\${web.client.write-timeout-ms}")
    private val writeTimeoutMs: Long = 0

    @Value("\${web.client.byte-buffer-size}")
    val bufferByteSize: Int = Int.MAX_VALUE

    @Bean
    fun courtCaseWebClient(authorizedClientManager: OAuth2AuthorizedClientManager): WebClient {
        return webClientFactory(courtCaseBaseUrl, authorizedClientManager, bufferByteSize)
    }

    @Bean
    fun assessmentUpdateWebClient(authorizedClientManager: OAuth2AuthorizedClientManager): WebClient {
      return webClientFactory(assessmentUpdateBaseUrl, authorizedClientManager, bufferByteSize)
    }

    private fun webClientFactory(
            baseUrl: String,
            authorizedClientManager: OAuth2AuthorizedClientManager,
            bufferByteCount: Int
    ): WebClient {
        val oauth2Client = ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)

        val httpClient = HttpClient.create()
            .tcpConfiguration {
                it.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                    .doOnConnected {
                        it.addHandlerLast(ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                          .addHandlerLast(WriteTimeoutHandler(writeTimeoutMs, TimeUnit.MILLISECONDS))
                    }
                }

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