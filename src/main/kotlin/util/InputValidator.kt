package store.util

object InputValidator {

    fun parseInt(input: String, fieldName: String): Int {
        return input.toIntOrNull()
            ?: throw IllegalArgumentException("[ERROR] 올바르지 않은 $fieldName 입니다.")
    }

    fun parsePositiveInt(input: String, fieldName: String): Int {
        val value = parseInt(input, fieldName)
        if (value <= 0) throw IllegalArgumentException("[ERROR] $fieldName 은(는) 1 이상이어야 합니다.")
        return value
    }

    fun validateYesOrNo(input: String): Boolean {
        return when (input.trim().uppercase()) {
            "Y" -> true
            "N" -> false
            else -> throw IllegalArgumentException("[ERROR] Y 또는 N만 입력 가능합니다.")
        }
    }

    fun validateDate(date: String, fieldName: String) {
        val regex = Regex("""\d{4}-\d{2}-\d{2}""")
        if (!regex.matches(date.trim())) {
            throw IllegalArgumentException("[ERROR] 올바르지 않은 $fieldName 형식입니다. (YYYY-MM-DD)")
        }
    }
}