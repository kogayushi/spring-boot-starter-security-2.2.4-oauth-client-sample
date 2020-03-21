package sample.adapter.eventlistener

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.stereotype.Component
import sample.adapter.infrastructure.spring.oauth2.CustomOAuth2User
import sample.domain.model.user.InMemoryMyUserPrincipalRepository

@Component
class AuthenticationEventListener(private val repository: InMemoryMyUserPrincipalRepository) {
    companion object {
        val logger = LoggerFactory.getLogger(AuthenticationEventListener::class.java)
    }

    // InteractiveAuthenticationSuccessEventをlistenするのが周りくどいのであれば、
    // OidcUserServiceやOAuth2UserServiceを拡張したクラスで永続化するやり方もある
    @EventListener
    fun listen(event: InteractiveAuthenticationSuccessEvent) {
        logger.debug("event {}", event)
        // 今回の実装ではかならずCustomOAuth2UserかCustomOidcUserのはずなのでCustomOAuth2Userにcastしても安全
        val user = event.authentication.principal as CustomOAuth2User

        // アプリ独自定義のユーザに変換して、それを永続化する
        repository.save(user.toMyUserPrincipal())
    }
}
