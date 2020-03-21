package sample.adapter.infrastructure.spring.oauth2

import org.springframework.web.client.RestTemplate

// kotlinはデフォルトでfinalになるので、SpringのAOPを利用する必要がある(request scopeにしたり@Transactionalをつけたりする)場合は、openをつける必要がある。
// ただし、@Serviceや@Componentといったアノテーションを使ってDIコンテナに登録する場合は、
// org.jetbrains.kotlin.plugin.springが自動的にクラスとメソッドをopenに変えてくれるので不要だったりする。
open class OAuth2Client(
    private val oAuth2AuthorizedClientFacade: OAuth2AuthorizedClientFacade,
    private val oAuth2RestTemplate: RestTemplate
) {
    open fun userInfo(): String {
        return oAuth2RestTemplate.getForObject(oAuth2AuthorizedClientFacade.userInfoEndpoint, String::class.java)!!
    }
}
