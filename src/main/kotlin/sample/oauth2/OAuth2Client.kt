package sample.oauth2

import org.springframework.web.client.RestTemplate

open class OAuth2Client(
    private val userInfoEndpoint: String,
    private val oAuth2RestTemplate: RestTemplate
) {

    open fun userInfo(): String {
        return oAuth2RestTemplate.getForObject(userInfoEndpoint, String::class.java)!!
    }
}
