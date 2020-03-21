package sample.adapter.infrastructure.spring.oauth2

import org.springframework.security.oauth2.core.user.OAuth2User
import sample.domain.model.user.MyUserPrincipal
import java.io.Serializable
import java.util.UUID

// OidcUserの拡張クラスに継承させるため、openにしておく。kotlinはデフォルトでfinalのため、継承可能とするにはopenが必要。
open class CustomOAuth2User(
    private val userId: UUID, // 自分たちのアプリの一意キー
    private val provider: String, // 連携したIdPの名前
    private val providerId: String, // 連携したIdPの一意キー
    private val oAuth2User: OAuth2User // デフォルト実装のインスタンス保持用プロパティ
) : OAuth2User by oAuth2User /* 元の振る舞いを変更する必要がないので、Delegationパターンを適用する。kotlinはby説でネイティブにサポートしている https://kotlinlang.org/docs/reference/delegation.html */,
    Serializable /* セッションをredisにキャッシュするのでSerializableが必要 */ {

    companion object {
        private const val serialVersionUID = -123L // セッションをredisにキャッシュするのでserialVersionUIDが必要
    }

    // FWに依存させないために、アプリ専用のユーザ情報へ変換するためのファクトリメソッドを用意する
    fun toMyUserPrincipal(): MyUserPrincipal = MyUserPrincipal(
        this.userId,
        this.provider,
        this.providerId,
        oAuth2User.name,
        oAuth2User.attributes["email"] as String
    )

    // OAuth2User#toStringの出力内容が不要という場合は、こんな感じで独自実装する必要がある。
    override fun toString(): String {
        return "CustomOAuth2User(userId=$userId, provider='$provider', providerId='$providerId', oAuth2User=$oAuth2User)"
    }
}
