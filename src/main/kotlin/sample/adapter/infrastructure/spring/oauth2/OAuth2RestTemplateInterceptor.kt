package sample.adapter.infrastructure.spring.oauth2

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.RefreshTokenOAuth2AuthorizedClientProvider
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository
import javax.servlet.http.HttpSession

class OAuth2RestTemplateInterceptor(
    // access tokenやOAuth2AuthorizedClientの取得処理はfacade経由で行う
    private val oAuth2AuthorizedClientFacade: OAuth2AuthorizedClientFacade,
    private val session: HttpSession
) : ClientHttpRequestInterceptor {

    private val refreshTokenOAuth2AuthorizedClientProvider = RefreshTokenOAuth2AuthorizedClientProvider()

    companion object {
        private val log = LoggerFactory.getLogger(OAuth2RestTemplateInterceptor::class.java)

        // HttpSessionOAuth2AuthorizedClientRepositoryの実装でこの値でセッションに保存しているのを見てハックした。
        // FWの内部構造に依存している（つまりFWと密結合）となっており、本来は望ましくないが他に良い設計が思いつかなかった。
        private val SESSION_ATTRIBUTE_NAME = "${HttpSessionOAuth2AuthorizedClientRepository::class.java.name}.AUTHORIZED_CLIENTS"
    }

    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {

        // Identity Providerにはたまに"Bearer"を小文字(つまり"bearer")しか受け入れない実装が存在するので、そういうときはこう書く。
        // request.headers.set(HttpHeaders.AUTHORIZATION, "bearer ${oAuth2AuthorizedClientFacade.accessToken}")
        // 補足）
        //    RFCに準拠するならばcase sensitiveに実装すべきなため、送受信双方ともヘッダ名を"Authorization"で値のプレフィックスを"Bearer"としなければいけない。
        //    RFC 6750の https://tools.ietf.org/html/rfc6750#section-1.1 では、特に断りがない場合はプロトコルは原則case sensitiveと書かれているし、
        //    さらに https://tools.ietf.org/html/rfc6750#section-2.1 では、文中において、わざわざダブルクォーテーションをつけてcase sensitiveであることを強調しているし、例も示している。
        //    しかし、補足への補足になるが、RFCに完全に準拠するよりも「送信は厳格に､受信は寛大に」の原則に従って、
        //    送信側は常に"Bearer"とし、入力側は大文字小文字を区別せずに"Bearer"でも"bearer"でも受け入れるのが望ましいと考え方もある。
        request.headers.setBearerAuth(oAuth2AuthorizedClientFacade.accessToken)

        val response = execution.execute(request, body)

        // レスポンスステータスが401の場合、access tokenをrefreshする(refresh token flowを実行する)必要がある
        if (response.statusCode == HttpStatus.UNAUTHORIZED) {
            log.debug("identity provider returned unauthorized response. payload =>.", response.body)

            log.debug("trying to execute refresh token flow")
            val oAuth2AuthenticationToken = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
            val context: OAuth2AuthorizationContext = OAuth2AuthorizationContext.withAuthorizedClient(oAuth2AuthorizedClientFacade.authorizedClient())
                .principal(oAuth2AuthenticationToken)
                .build()
            // RefreshTokenOAuth2AuthorizedClientProvider#authorizeがrefresh token flowを実行してくれるので、交換後のaccess tokenとrefresh tokenを受け取る
            val refreshed = refreshTokenOAuth2AuthorizedClientProvider.authorize(context)
            if (refreshed == null) {
                log.debug("don't have refresh token")
                return response
            }
            log.debug("succeeded refreshing token flow.")

            // 交換したaccess tokenとrefresh tokenをセッションに保存する
            val authorizedClients = session.getAttribute(SESSION_ATTRIBUTE_NAME) as MutableMap<String, OAuth2AuthorizedClient>
            authorizedClients[oAuth2AuthorizedClientFacade.registrationId] = refreshed
            session.setAttribute(SESSION_ATTRIBUTE_NAME, authorizedClients)
            log.debug("stored new access token and refresh token in session.")

            // 新しいaccess tokenをヘッダーに詰め直して、再リクエストする
            request.headers.setBearerAuth(refreshed.accessToken.tokenValue)
            return execution.execute(request, body)
        }

        log.debug("refresh token flow is not executed for this time.")
        return response
    }

}
