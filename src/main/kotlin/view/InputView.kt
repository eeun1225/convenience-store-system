package store.view

import store.util.InputValidator
import store.domain.product.ProductCategory

class InputView {

    fun readLine(): String = readln().trim()

    fun readYesOrNo(): Boolean {
        val input = readln().trim()
        return InputValidator.validateYesOrNo(input)
    }

    fun readSelection(): String = readln().trim()

    /**
     * 구매자 관련 입력
     */
    fun readPhoneNumber(): String {
        return readln().trim()
    }

    fun readPassword(): String {
        return readln().trim()
    }

    fun readPurchaseItems(): List<Pair<String, Int>> {
        val input = readln().trim()
        return store.util.Validator.validatePurchaseInput(input)
    }

    fun readHistoryId(): String {
        return readln().trim()
    }

    fun readModifyCartAction(): String = readln().trim()

    fun readCartQuantityUpdate(): Pair<String, Int> {
        val name = readln().trim()
        val quantityInput = readln().trim()
        val quantity = InputValidator.parsePositiveInt(quantityInput, "수량")
        return name to quantity
    }

    fun readProductNameToRemove(): String {
        return readln().trim()
    }


    /**
     * 관리자 관련 입력
     */
    fun readAdminNumber(): String {
        return readln().trim()
    }

    fun readNewProductInfo(): NewProductInfo {
        val name = readln().trim()

        val price = InputValidator.parsePositiveInt(readln().trim(), "가격")
        val quantity = InputValidator.parsePositiveInt(readln().trim(), "수량")

        val description = readln().trim().takeIf { it.isNotBlank() }

        val categoryIndex = InputValidator.parsePositiveInt(readln().trim(), "카테고리 번호") - 1
        val category = ProductCategory.getAllCategories().getOrNull(categoryIndex)
            ?: ProductCategory.ETC

        return NewProductInfo(name, price, quantity, description, category)
    }

    fun readProductUpdateInfo(): Triple<String, Int, Int> {
        val name = readln().trim()
        val newPrice = InputValidator.parsePositiveInt(readln().trim(), "가격")
        val newQuantity = InputValidator.parsePositiveInt(readln().trim(), "수량")
        return Triple(name, newPrice, newQuantity)
    }

    fun readNewPromotionInfo(): NewPromotionInfo {
        val name = readln().trim()
        val buy = InputValidator.parsePositiveInt(readln().trim(), "구매 수량")
        val get = InputValidator.parsePositiveInt(readln().trim(), "증정 수량")

        val start = readln().trim().also { InputValidator.validateDate(it, "시작일") }
        val end = readln().trim().also { InputValidator.validateDate(it, "종료일") }

        return NewPromotionInfo(name, buy, get, start, end)
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