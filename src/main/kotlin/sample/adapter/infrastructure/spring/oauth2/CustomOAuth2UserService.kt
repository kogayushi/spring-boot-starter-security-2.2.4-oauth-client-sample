package sample.adapter.infrastructure.spring.oauth2

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import sample.domain.model.user.InMemoryMyUserPrincipalRepository
import java.util.UUID

class CustomOAuth2UserService(private val repository: InMemoryMyUserPrincipalRepository) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        // 基底クラスの実装を利用してOAuth2Userを取得
        val oauth2User = super.loadUser(userRequest)

        // 各IdPのsubjectを文字列として取得する
        val subject = when (userRequest.clientRegistration.clientName) {
            "github" -> oauth2User.attributes["id"].toString()
            "facebook" -> oauth2User.attributes["id"].toString()
            else -> throw IllegalArgumentException("there is no such identity provider. cannot login by ${userRequest.clientRegistration.clientName}")
        }

        // IdPの一意キーでユーザを検索する
        val existing = repository.resolveBy(subject)
        // 存在すればそのID、存在しなければ新規でIDを採番する
        val userId = existing?.userId ?: UUID.randomUUID()

        // 認証したユーザの情報を返す。このインスタンスがセッションに保存される。
        // 新規ユーザの場合、この時点ではDBには未登録。この後発行されるInteractiveAuthenticationSuccessEventのサブスクライバーが永続化する。
        return CustomOAuth2User(userId, userRequest.clientRegistration.clientName, subject, oauth2User)
    }
}
