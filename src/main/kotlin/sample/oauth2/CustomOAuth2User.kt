package sample.oauth2

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

    override fun toString(): String {
        return "CustomOAuth2User(userId=$userId, provider='$provider', providerId='$providerId', oAuth2User=$oAuth2User)"
    }
}
