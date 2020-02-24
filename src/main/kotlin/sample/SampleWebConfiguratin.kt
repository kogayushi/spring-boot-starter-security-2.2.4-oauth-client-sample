package sample

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User

@Configuration
class SampleWebConfiguratin : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
            .userInfoEndpoint().oidcUserService(oidcUserService())
            .userService(oAuth2UserService())
    }

    @Bean
    fun oidcUserService(): OidcUserService = CustomOidcUserService()

    @Bean
    fun oAuth2UserService(): OAuth2UserService<OAuth2UserRequest, OAuth2User> = CustomOAuth2UserService()
}
