package store.domain.user

object UserValidator {
    private val PHONE_NUMBER_PATTERN = Regex("""^\d{3}-\d{4}-\d{4}$""")

    private const val MIN_PASSWORD_LENGTH = 8

    fun validatePhoneNumber(phoneNumber: String) {
        require(PHONE_NUMBER_PATTERN.matches(phoneNumber)) {
            "휴대폰 번호 형식이 올바르지 않습니다. (예: 010-1234-5678)"
        }
    }

    fun validatePassword(password: String) {
        require(password.length >= MIN_PASSWORD_LENGTH) {
            "비밀번호는 ${MIN_PASSWORD_LENGTH}자 이상이어야 합니다."
        }
        require(password.any { it.isDigit() }) {
            "비밀번호에 숫자가 포함되어야 합니다."
        }
        require(password.any { it.isLetter() }) {
            "비밀번호에 영문자가 포함되어야 합니다."
        }
        require(password.any { !it.isLetterOrDigit() }) {
            "비밀번호에 특수문자가 포함되어야 합니다."
        }
    }

    fun validatePasswordMatch(password: String, confirmPassword: String) {
        require(password == confirmPassword) {
            "비밀번호가 일치하지 않습니다."
        }
    }
}