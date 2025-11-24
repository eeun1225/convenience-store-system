package store.domain.user

enum class UserMode(val displayName: String) {
    CUSTOMER("구매자"),
    ADMIN("관리자");

    companion object {
        fun select(input: String): UserMode? {
            return when (input) {
                "1" -> CUSTOMER
                "2" -> ADMIN
                else -> null
            }
        }
    }
}