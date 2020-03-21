package sample.adapter.infrastructure.spring.oauth2

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import javax.servlet.http.HttpServletRequest

// OAuth2AuthorizedClientをあちこちで利用するが呼び出すのがちょっと面倒なのでFacadeを用意しておく。
class OAuth2AuthorizedClientFacade(
    private val httpSessionOAuth2AuthorizedClientRepository: OAuth2AuthorizedClientRepository,
    private val request: HttpServletRequest
) {

    val userInfoEndpoint: String
        get() = authorizedClient().clientRegistration.providerDetails.userInfoEndpoint.uri

    val accessToken: String
        get() = authorizedClient().accessToken.tokenValue

    val registrationId: String
        get() = authorizedClient().clientRegistration.registrationId

    fun authorizedClient(): OAuth2AuthorizedClient {
        // 未ログインの状態で呼び出されることを考慮するなら、`as? OAuth2AuthenticationToken ?: throw XxxExceptin`として、未ログインを例外で通知する
        val oAuth2AuthenticationToken = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
        return httpSessionOAuth2AuthorizedClientRepository.loadAuthorizedClient(
            oAuth2AuthenticationToken.authorizedClientRegistrationId,
            oAuth2AuthenticationToken,
            request
        )
    }

}
