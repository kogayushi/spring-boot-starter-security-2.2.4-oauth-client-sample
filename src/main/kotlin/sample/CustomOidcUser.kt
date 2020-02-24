package sample

import org.springframework.security.oauth2.core.oidc.user.OidcUser

class CustomOidcUser(
    providerId: String, private val oidcUser: OidcUser
) : CustomOAuth2User(providerId, oidcUser), OidcUser by oidcUser
