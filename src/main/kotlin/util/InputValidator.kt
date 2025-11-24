package store.util

object InputValidator {
    private val PURCHASE_PATTERN = Regex("""\[\s*([^-\[\]]+)\s*-(\d+)\s*]""")
    private val DATE_PATTERN = Regex("""\d{4}-\d{2}-\d{2}""")
    private val YES_NO_PATTERN = Regex("""[YN]""", RegexOption.IGNORE_CASE)

    fun validateYesOrNo(input: String): Boolean {
        val trimmed = input.trim().uppercase()
        require(YES_NO_PATTERN.matches(trimmed)) {
            "잘못된 입력입니다. 다시 입력해 주세요."
        }
        return trimmed == "Y"
    }

    fun parsePositiveInt(input: String, fieldName: String): Int {
        val number = input.toIntOrNull()
            ?: throw IllegalArgumentException("$fieldName 은(는) 숫자여야 합니다.")
        require(number > 0) { "$fieldName 은(는) 0보다 커야 합니다." }
        return number
    }

    fun validatePurchaseInput(input: String): List<Pair<String, Int>> {
        require(input.isNotBlank()) {
            "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."
        }

        val matches = PURCHASE_PATTERN.findAll(input).toList()
        require(matches.isNotEmpty()) {
            "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."
        }

        validatePurchaseFormat(input, matches)

        return matches.map { match ->
            val name = match.groupValues[1].trim()
            val quantity = match.groupValues[2].toIntOrNull()
                ?: throw IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.")

            validateProductName(name)
            validateQuantity(quantity)

            name to quantity
        }
    }

    private fun validatePurchaseFormat(input: String, matches: List<MatchResult>) {
        val cleanedInput = input.replace(" ", "")
        val cleanedMatches = matches.joinToString("") { it.value }

        require(cleanedInput.replace(",", "") == cleanedMatches) {
            "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."
        }
    }

    fun validateProductName(name: String) {
        require(name.isNotBlank()) {
            "[ERROR] 상품명은 빈 값일 수 없습니다."
        }
        require(!name.contains("[") && !name.contains("]")) {
            "[ERROR] 상품명에 특수문자가 포함될 수 없습니다."
        }
    }

    fun validateQuantity(quantity: Int) {
        require(quantity > 0) {
            "[ERROR] 수량은 1개 이상이어야 합니다."
        }
    }

    fun validateStock(requestQuantity: Int, availableStock: Int, productName: String) {
        require(requestQuantity <= availableStock) {
            "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요."
        }
    }

    fun validateProductExists(exists: Boolean, productName: String) {
        require(exists) {
            "[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요."
        }
    }

    fun validatePrice(price: Int) {
        require(price > 0) {
            "[ERROR] 가격은 0보다 커야 합니다."
        }
    }

    fun validatePromotionValues(buy: Int, get: Int) {
        require(buy > 0) {
            "[ERROR] 프로모션 구매 수량은 0보다 커야 합니다."
        }
        require(get > 0) {
            "[ERROR] 프로모션 증정 수량은 0보다 커야 합니다."
        }
    }

    fun validateDate(date: String, fieldName: String = "날짜") {
        require(DATE_PATTERN.matches(date)) {
            "$fieldName 형식이 올바르지 않습니다. (YYYY-MM-DD)"
        }

        val parts = date.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()

        require(year in 1900..2100) { "연도가 유효하지 않습니다: $year" }
        require(month in 1..12) { "월이 유효하지 않습니다: $month" }
        require(day in 1..31) { "일이 유효하지 않습니다: $day" }
    }
}