package sample.adapter.infrastructure.spring.oauth2

import org.springframework.security.oauth2.core.oidc.user.OidcUser
import java.io.Serializable
import java.util.UUID

class CustomOidcUser(
    userId: UUID,
    provider: String,
    providerId: String,
    oidcUser: OidcUser
) : CustomOAuth2User(userId, provider, providerId, oidcUser),
    OidcUser by oidcUser /* 元の振る舞いを変更する必要がないので、Delegationパターンを適用する。 */,
    Serializable {

    companion object {
        private const val serialVersionUID = -128L // セッションをredisにキャッシュするのでserialVersionUIDが必要
    }
}
