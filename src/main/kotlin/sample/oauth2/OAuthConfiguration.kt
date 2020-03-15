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
import org.springframework.web.context.annotation.RequestScope
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Configuration
class OAuthConfiguration(
    private val builder: RestTemplateBuilder,
    private val request: HttpServletRequest,
    private val session: HttpSession
) {

    // userInfoEndpointを動的にinjectするためにJavaConfigurationでDIコンテナに登録している
    @RequestScope
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

    // resttemplateはメンテナンスモードだったりはする
    // https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#webmvc-resttemplate
    // しかし、webclientを利用するにはwebfluxを依存に追加する必要があるが、 webclientのためだけに依存に追加したくないので、
    // 今回はOauth2Clientでresttemplateの呼び出しをラップしておく。
    // ラップしておくことで将来置き換える必要が生じてもコストを小さく済むはず。
    @Bean
    fun oAuth2RestTemplate(): RestTemplate =
        builder.additionalInterceptors(oAuth2RestTemplateInterceptor())
            .additionalInterceptors(additionalHeaderInterceptor()) // 特定のCloud Providerを利用している場合などに、追加でヘッダーを共有される場合がある。
            .build()

    @Bean
    fun oAuth2RestTemplateInterceptor(): ClientHttpRequestInterceptor =
        OAuth2RestTemplateInterceptor(
            httpSessionOAuth2AuthorizedClientRepository(),
            session,
            request
        )

    @Bean
    fun additionalHeaderInterceptor(): ClientHttpRequestInterceptor = ClientHttpRequestInterceptor { request, body, execution ->
        request.headers.set("additional-header-name", "additional-header-value") // e.g. x-xxx-client-id
        execution.execute(request, body)
    }

    @Bean
    fun httpSessionOAuth2AuthorizedClientRepository(): OAuth2AuthorizedClientRepository = HttpSessionOAuth2AuthorizedClientRepository()
}

