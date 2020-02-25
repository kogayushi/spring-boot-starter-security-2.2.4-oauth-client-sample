package sample.user

import java.util.UUID

data class MyUserPrincipal(
    val userId: UUID,
    val provider: String,
    val providerId: String,
    val name: String,
    val email: String
)
