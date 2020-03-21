package sample.adapter.infrastructure.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import sample.adapter.infrastructure.spring.oauth2.CustomOAuth2UserService
import sample.adapter.infrastructure.spring.oauth2.CustomOidcUserService
import sample.domain.model.user.InMemoryMyUserPrincipalRepository

@Configuration
class SampleWebConfiguration : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .oauth2Login() // oauth2を有効にする
            .userInfoEndpoint()
                .oidcUserService(oidcUserService()) // openid connect用のOidcUserServiceを設定する
                .userService(oAuth2UserService()) // oauth2用のOAUth2UserServiceを設定する
    }

    @Bean
    fun oidcUserService(): OidcUserService = CustomOidcUserService(repository())

    @Bean
    fun oAuth2UserService(): OAuth2UserService<OAuth2UserRequest, OAuth2User> = CustomOAuth2UserService(repository())

    @Bean
    fun repository(): InMemoryMyUserPrincipalRepository = InMemoryMyUserPrincipalRepository()
}
