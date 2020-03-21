package sample.adapter.infrastructure.spring.oauth2

import org.springframework.web.client.RestTemplate
import java.io.Serializable

// kotlinはデフォルトでfinalになるので、SpringのAOPを利用する必要がある(request scopeにしたり@Transactionalをつけたりする)場合は、openをつける必要がある。
// ただし、@Serviceや@Componentといったアノテーションを使ってDIコンテナに登録する場合は、
// org.jetbrains.kotlin.plugin.springが自動的にクラスとメソッドをopenに変えてくれるので不要だったりする。
open class OAuth2Client(
    private val userInfoEndpoint: String,
    private val oAuth2RestTemplate: RestTemplate
) : Serializable {
    companion object {
        private const val serialVersionUID = -19341L
    }

    open fun userInfo(): String {
        return oAuth2RestTemplate.getForObject(userInfoEndpoint, String::class.java)!!
    }

}
