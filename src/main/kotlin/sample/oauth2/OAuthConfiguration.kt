package sample.oauth2

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.annotation.SessionScope
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Configuration
class OAuthConfiguration(
    private val builder: RestTemplateBuilder,
    private val request: HttpServletRequest,
    private val session: HttpSession
) {

    // userInfoEndpointを動的にinjectするためにJavaConfigurationでDIコンテナに登録している
    @SessionScope
    @Bean
    fun oAuth2Client(): OAuth2Client {
        val oAuth2AuthenticationToken = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
        val authorizedClient: OAuth2AuthorizedClient = httpSessionOAuth2AuthorizedClientRepository().loadAuthorizedClient(
            oAuth2AuthenticationToken.authorizedClientRegistrationId,
            oAuth2AuthenticationToken,
            request
        )
        val userInfoEndpoint = authorizedClient.clientRegistration.providerDetails.userInfoEndpoint
        return OAuth2Client(userInfoEndpoint.uri, oAuth2RestTemplate())
    }

    @Bean
    fun oAuth2RestTemplate(): RestTemplate =
        builder.additionalInterceptors(oAuth2RestTemplateInterceptor()).build()

    @Bean
    fun oAuth2RestTemplateInterceptor(): ClientHttpRequestInterceptor =
        OAuth2RestTemplateInterceptor(
            httpSessionOAuth2AuthorizedClientRepository(),
            session,
            request
        )

    @Bean
    fun httpSessionOAuth2AuthorizedClientRepository(): OAuth2AuthorizedClientRepository = HttpSessionOAuth2AuthorizedClientRepository()

}

