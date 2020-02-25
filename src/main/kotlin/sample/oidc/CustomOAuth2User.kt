package sample.oidc

import org.springframework.security.oauth2.core.user.OAuth2User
import sample.user.MyUserPrincipal
import java.util.UUID

open class CustomOAuth2User(
    private val userId: UUID,
    private val provider: String,
    private val providerId: String,
    private val oAuth2User: OAuth2User
) : OAuth2User by oAuth2User {

    fun toMyUserPrincipal(): MyUserPrincipal {
        return MyUserPrincipal(
            this.userId,
            this.provider,
            this.providerId,
            oAuth2User.name,
            oAuth2User.attributes["email"] as String
        )
    }
}
