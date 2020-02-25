package sample.eventlistener

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.stereotype.Component

@Component
class AuthenticationEventListener {
    val logger = LoggerFactory.getLogger(AuthenticationEventListener::class.java)

    // InteractiveAuthenticationSuccessEventをlistenするのが周りくどいのであれば、
    // OidcUserServiceやOAuth2UserServiceを拡張したクラスで永続化するやり方もある
    // その場合（私は）なぜか、@Transactionalを忘れがちなので、忘れずにつけること。
    // @Tranctional
    @EventListener
    fun listen(event: InteractiveAuthenticationSuccessEvent) {
        event.authentication.principal
        logger.debug("event {}", event)
        // TODO ユーザ情報の永続化
    }
}
