package sample.user

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

// DIPを前提としたアーキテクチャ（クリーンアーキテクチャなど）を採用する場合は
// interfaceにするところだがサンプルアプリなのでInMemoryな実装で良しとする
@Component // DBを使う実装の場合は@Repositoryにする
class InMemoryMyUserPrincipalRepository {
    companion object {
        private val logger = LoggerFactory.getLogger(InMemoryMyUserPrincipalRepository::class.java)
        private val inmemory = ConcurrentHashMap<UUID, MyUserPrincipal>()
    }

    fun resolveBy(userId: UUID): MyUserPrincipal? {
        val user = inmemory[userId]
        logger.info("{} is fetched by userId({})", user, userId)
        return user
    }

    fun resolveBy(providerId: String): MyUserPrincipal? {
        val user = inmemory.values.find { it.providerId == providerId }
        logger.info("{} is fetched by providerId({})", user, providerId)
        return user
    }

    fun save(user: MyUserPrincipal) {
        logger.info("{} is saved", user)
        inmemory[user.userId] = user
    }
}
