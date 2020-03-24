package sample.adapter.infrastructure.spring.oauth2

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.annotation.RequestScope
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Configuration
class OAuthConfiguration(
    private val builder: RestTemplateBuilder,
    private val session: HttpSession,
    private val request: HttpServletRequest
) {

    @RequestScope // 本当はsession scopeにしたいが、injectionしているインスタンスにserializableじゃないものが含まれているのでできない。
    @Bean
    fun oAuth2Client(): OAuth2Client = OAuth2Client(oAuth2AuthorizedClientFacade(), oAuth2RestTemplate())

    // resttemplateはメンテナンスモードだったりはする => https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#webmvc-resttemplate
    // しかし、webclientを利用するにはwebfluxを依存に追加する必要があるが、 webclientのためだけに追加したくない。
    // そのため、今回はOauth2Clientでresttemplateの呼び出しをラップしておく。
    // ラップしておくことで置き換えるコストを小さくできるはず。
    @RequestScope
    @Bean
    fun oAuth2RestTemplate(): RestTemplate =
        builder
            .additionalInterceptors(oAuth2RestTemplateInterceptor())
            .additionalInterceptors(additionalHeaderInterceptor()) // 特定のCloud Providerを利用している場合などに、ヘッダーの追加設定が必要な場合がある。
            .build()

    // thread safeな実装になっているので、シングルトンで問題ない（@RequestSession不要）。
    @Bean
    fun oAuth2RestTemplateInterceptor(): ClientHttpRequestInterceptor =
        OAuth2RestTemplateInterceptor(oAuth2AuthorizedClientFacade(), session)

    @Bean
    fun additionalHeaderInterceptor(): ClientHttpRequestInterceptor =
        ClientHttpRequestInterceptor { request, body, execution ->
            request.headers.set("additional-header-name", "additional-header-value") // e.g. x-xxx-client-id
            execution.execute(request, body)
        }

    @Bean
    fun oAuth2AuthorizedClientFacade(): OAuth2AuthorizedClientFacade =
        OAuth2AuthorizedClientFacade(httpSessionOAuth2AuthorizedClientRepository(), request)

    @Bean
    fun httpSessionOAuth2AuthorizedClientRepository(): OAuth2AuthorizedClientRepository =
        HttpSessionOAuth2AuthorizedClientRepository()
}
