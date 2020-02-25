package sample.oidc

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import sample.user.InMemoryMyUserPrincipalRepository
import java.util.UUID

class CustomOAuth2UserService(private val repository: InMemoryMyUserPrincipalRepository) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oauth2User = super.loadUser(userRequest)

        val subject = when (userRequest.clientRegistration.clientName) {
            "github" -> (oauth2User.attributes["id"] as Int).toString()
            "facebook" -> oauth2User.attributes["id"] as String
            else -> throw IllegalArgumentException("there is no such identity provider. cannot login by ${userRequest.clientRegistration.clientName}")
        }

        val existing = repository.resolveBy(subject)
        val userId = existing?.userId ?: UUID.randomUUID()

        return CustomOAuth2User(userId, userRequest.clientRegistration.clientName, subject, oauth2User)
    }
}
