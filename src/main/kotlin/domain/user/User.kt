package store.domain.user

import java.time.LocalDateTime

sealed class User(
    open val id: String,
    open val createdAt: LocalDateTime = LocalDateTime.now()
) {
    data class Customer(
        override val id: String,
        val name: String,
        val phoneNumber: String? = null,
        val password: String? = null,
        val isMember: Boolean = false,
        override val createdAt: LocalDateTime = LocalDateTime.now()
    ) : User(id, createdAt)

    data class Admin(
        override val id: String,
        val number: String,
        val password: String,
        override val createdAt: LocalDateTime = LocalDateTime.now()
    ) : User(id, createdAt)
}