package sample.oidc

import org.springframework.security.oauth2.core.oidc.user.OidcUser
import sample.oidc.CustomOAuth2User

class CustomOidcUser(
    providerId: String, private val oidcUser: OidcUser
) : CustomOAuth2User(providerId, oidcUser), OidcUser by oidcUser
