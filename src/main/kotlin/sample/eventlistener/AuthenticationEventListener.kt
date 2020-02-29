package sample.eventlistener

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.stereotype.Component
import sample.oauth2.CustomOAuth2User
import sample.user.InMemoryMyUserPrincipalRepository

@Component
class AuthenticationEventListener(private val repository: InMemoryMyUserPrincipalRepository) {
    companion object {
        val logger = LoggerFactory.getLogger(AuthenticationEventListener::class.java)
    }
    // InteractiveAuthenticationSuccessEventをlistenするのが周りくどいのであれば、
    // OidcUserServiceやOAuth2UserServiceを拡張したクラスで永続化するやり方もある
    // その場合（私は）なぜか、@Transactionalを忘れがちなので、忘れずにつけること。
    // @Tranctional
    @EventListener
    fun listen(event: InteractiveAuthenticationSuccessEvent) {
        logger.debug("event {}", event)
        val user = event.authentication.principal as CustomOAuth2User
        repository.save(user.toMyUserPrincipal())
    }
}
