package sample.oidc

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import sample.user.InMemoryMyUserPrincipalRepository
import java.util.UUID

class CustomOidcUserService(private val repository: InMemoryMyUserPrincipalRepository) : OidcUserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        val oidcUser = super.loadUser(userRequest)
        val existing = repository.resolveBy(oidcUser.subject)

        val userId = existing?.userId ?: UUID.randomUUID()

        return CustomOidcUser(userId, userRequest.clientRegistration.clientName, oidcUser.subject, oidcUser)
    }
}
