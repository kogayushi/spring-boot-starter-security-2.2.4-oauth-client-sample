package sample.oauth2

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.RefreshTokenOAuth2AuthorizedClientProvider
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository
import javax.servlet.http.HttpSession

class OAuth2RestTemplateInterceptor(
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val session: HttpSession
) : ClientHttpRequestInterceptor {

    private val refreshTokenOAuth2AuthorizedClientProvider = RefreshTokenOAuth2AuthorizedClientProvider()

    companion object {
        private val log = LoggerFactory.getLogger(OAuth2RestTemplateInterceptor::class.java)
        // HttpSessionOAuth2AuthorizedClientRepositoryの実装でこの値でセッションに保存しているため踏襲している
        private val SESSION_ATTRIBUTE_NAME = "${HttpSessionOAuth2AuthorizedClientRepository::class.java.name}.AUTHORIZED_CLIENTS"
    }

    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        val oAuth2AuthenticationToken = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken

        val authorizedClient: OAuth2AuthorizedClient = authorizedClientService.loadAuthorizedClient(
            oAuth2AuthenticationToken.authorizedClientRegistrationId,
            oAuth2AuthenticationToken.name
        )
        request.headers.setBearerAuth(authorizedClient.accessToken.tokenValue)

        val response = execution.execute(request, body)

        if (response.statusCode == HttpStatus.UNAUTHORIZED) {
            log.debug("identity provider returned unauthorized response. payload =>.", response.body)
            val context: OAuth2AuthorizationContext = OAuth2AuthorizationContext.withAuthorizedClient(authorizedClient)
                .principal(oAuth2AuthenticationToken)
                .build()
            val refreshed = refreshTokenOAuth2AuthorizedClientProvider.authorize(context)
            if (refreshed == null) {
                log.debug("don't have refresh token")
                return response
            }

            log.debug("succeeded refreshing token.")
            val authorizedClients = session.getAttribute(SESSION_ATTRIBUTE_NAME) as MutableMap<String, OAuth2AuthorizedClient>
            authorizedClients[authorizedClient.clientRegistration.registrationId] = refreshed
            session.setAttribute(SESSION_ATTRIBUTE_NAME, authorizedClients)
            log.debug("stored new access token adn refresh token in session.")

            return execution.execute(request, body)
        }

        log.debug("access token might be still valid")
        return response

    }

}
