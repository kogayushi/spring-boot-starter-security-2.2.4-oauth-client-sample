package sample.oidc

import org.springframework.security.oauth2.core.oidc.user.OidcUser
import java.util.UUID

class CustomOidcUser(
    private val userId: UUID,
    private val provider: String,
    private val providerId: String,
    private val oidcUser: OidcUser
) : CustomOAuth2User(userId, provider, providerId, oidcUser), OidcUser by oidcUser
