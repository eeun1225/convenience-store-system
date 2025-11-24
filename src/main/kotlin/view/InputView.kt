package store.view

import store.util.InputValidator
import store.domain.product.ProductCategory

class InputView {

    // 기본 입력
    fun readLine(): String = readln().trim()

    fun readYesOrNo(): Boolean {
        val input = readln().trim()
        return InputValidator.validateYesOrNo(input)
    }

    fun readSelection(): String = readln().trim()

    // 사용자 정보 입력
    fun readName(): String {
        return readNonBlankInput("이름")
    }

    fun readPhoneNumber(): String = readln().trim()

    fun readPassword(): String = readln().trim()

    fun readPasswordConfirm(): String = readln().trim()

    // 구매 관련
    fun readPurchaseItems(): List<Pair<String, Int>> {
        val input = readln().trim()
        return InputValidator.validatePurchaseInput(input)
    }

    fun readHistoryId(): String = readln().trim()

    fun readModifyCartAction(): String = readln().trim()

    fun readCartQuantityUpdate(): Pair<String, Int> {
        val name = readPrompt("상품명을 입력하세요: ")
        val quantityInput = readPrompt("변경할 수량을 입력하세요: ")
        val quantity = InputValidator.parsePositiveInt(quantityInput, "수량")
        return name to quantity
    }

    fun readProductNameToRemove(): String {
        return readPrompt("제거할 상품명을 입력하세요: ")
    }

    // 관리자 관련
    fun readAdminNumber(): String = readln().trim()

    fun readNewProductInfo(): NewProductInfo {
        val name = readPrompt("상품명: ")
        val price = readPositiveInt("가격: ", "가격")
        val quantity = readPositiveInt("수량: ", "수량")
        val description = readOptionalInput("설명 (선택사항): ")
        val category = readCategory()

        return NewProductInfo(name, price, quantity, description, category)
    }

    fun readProductUpdateInfo(): Triple<String, Int, Int> {
        val name = readPrompt("수정할 상품명: ")
        val newPrice = readPositiveInt("새 가격: ", "가격")
        val newQuantity = readPositiveInt("새 수량: ", "수량")

        return Triple(name, newPrice, newQuantity)
    }

    fun readNewPromotionInfo(): NewPromotionInfo {
        val name = readPrompt("프로모션명: ")
        val buy = readPositiveInt("구매 수량 (buy): ", "구매 수량")
        val get = readPositiveInt("증정 수량 (get): ", "증정 수량")
        val start = readValidatedDate("시작일 (YYYY-MM-DD): ", "시작일")
        val end = readValidatedDate("종료일 (YYYY-MM-DD): ", "종료일")

        return NewPromotionInfo(name, buy, get, start, end)
    }

    // 헬퍼 메서드
    private fun readNonBlankInput(fieldName: String): String {
        return readln().trim().also {
            require(it.isNotBlank()) { "$fieldName 은(는) 비어있을 수 없습니다." }
        }
    }

    private fun readPrompt(prompt: String): String {
        print(prompt)
        return readln().trim()
    }

    private fun readOptionalInput(prompt: String): String? {
        print(prompt)
        return readln().trim().takeIf { it.isNotBlank() }
    }

    private fun readPositiveInt(prompt: String, fieldName: String): Int {
        print(prompt)
        return InputValidator.parsePositiveInt(readln().trim(), fieldName)
    }

    private fun readValidatedDate(prompt: String, fieldName: String): String {
        print(prompt)
        return readln().trim().also {
            InputValidator.validateDate(it, fieldName)
        }
    }

    private fun readCategory(): ProductCategory {
        print("카테고리 선택: ")
        val categoryIndex = InputValidator.parsePositiveInt(readln().trim(), "카테고리 번호") - 1
        return ProductCategory.getAllCategories().getOrNull(categoryIndex)
            ?: ProductCategory.ETC
    }
}

data class NewProductInfo(
    val name: String,
    val price: Int,
    val quantity: Int,
    val description: String?,
    val category: ProductCategory
)

data class NewPromotionInfo(
    val name: String,
    val buy: Int,
    val get: Int,
    val startDate: String,
    val endDate: String
)