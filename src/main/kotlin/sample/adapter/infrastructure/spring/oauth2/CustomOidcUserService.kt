package sample.adapter.infrastructure.spring.oauth2

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import sample.domain.model.user.InMemoryMyUserPrincipalRepository
import java.util.UUID

class CustomOidcUserService(private val repository: InMemoryMyUserPrincipalRepository) : OidcUserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        // 基底クラスの実装を利用してOAuth2Userを取得
        val oidcUser = super.loadUser(userRequest)

        // IdPの一意キーでユーザを検索する
        val existing = repository.resolveBy(oidcUser.subject)

        // 存在すればそのID、存在しなければ新規でIDを採番する
        val userId = existing?.userId ?: UUID.randomUUID()

        // 認証したユーザの情報を返す。このインスタンスがセッションに保存される。
        // 新規ユーザの場合、この時点ではDBには未登録。この後発行されるInteractiveAuthenticationSuccessEventのサブスクライバーが永続化する。
        return CustomOidcUser(userId, userRequest.clientRegistration.clientName, oidcUser.subject, oidcUser)
    }
}
