package sample.oidc

import org.springframework.security.oauth2.core.user.OAuth2User
import sample.user.MyUserPrincipal

open class CustomOAuth2User(
    private val provider: String,
    private val oAuth2User: OAuth2User
) : OAuth2User by oAuth2User {
    fun toMyUserPrincipal(): MyUserPrincipal =
        MyUserPrincipal(this.provider, oAuth2User.name, oAuth2User.attributes["email"] as String)
}
