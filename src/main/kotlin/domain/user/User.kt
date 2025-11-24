package store.domain.user

import java.time.LocalDateTime
import java.util.UUID

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
    ) : User(id, createdAt) {

        companion object {
            fun createMember(
                name: String,
                phoneNumber: String,
                password: String
            ): Customer {
                UserValidator.validatePhoneNumber(phoneNumber)
                UserValidator.validatePassword(password)

                return Customer(
                    id = generateId(),
                    name = name,
                    phoneNumber = phoneNumber,
                    password = password,
                    isMember = true
                )
            }

            fun createGuest(): Customer {
                return Customer(
                    id = generateId(),
                    name = "비회원",
                    isMember = false
                )
            }

            private fun generateId(): String = UUID.randomUUID().toString()
        }

        fun matchesPassword(inputPassword: String): Boolean {
            return this.password == inputPassword
        }
    }

    data class Admin(
        override val id: String,
        val number: String,
        val password: String,
        override val createdAt: LocalDateTime = LocalDateTime.now()
    ) : User(id, createdAt)
}