package sample.domain.model.user

import java.util.UUID

// これ自体はごくごく普通のdata class
data class MyUserPrincipal(
    val userId: UUID, // アプリで新規に採番した、自分たちで管理する一意キー
    val provider: String, // IdPの名前が入る。本番コードではたぶんenumにする。
    val providerId: String, // IpPの一意キー（subject)が入る
    val name: String,
    val email: String
)
