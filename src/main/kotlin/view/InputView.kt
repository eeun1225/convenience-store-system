package store.view

import store.domain.product.ProductCategory
import store.util.InputValidator

class InputView {

    fun readSelection(): String = readln().trim()

    fun askLoginOrGuest(): String {
        println("\n1. 회원 로그인")
        println("2. 비회원으로 계속")
        print("선택: ")
        return readln().trim()
    }

    fun readLineWithMessage(message: String): String {
        print(message)
        return readln().trim()
    }

    fun readPhoneNumber(): String {
        return readLineWithMessage("휴대폰 번호를 입력해주세요 (예: 010-1234-5678): ")
    }

    fun readPassword(): String {
        return readLineWithMessage("비밀번호를 입력해주세요 (예: pw1234@!): ")
    }

    fun readAdminNumber(): String {
        return readLineWithMessage("관리자 번호를 입력해주세요: ")
    }

    fun askModifyCart(): Boolean {
        return readYesOrNo("장바구니를 수정하시겠습니까? (Y/N)")
    }

    fun readCartModifyAction(): String {
        println("\n=== 장바구니 수정 ===")
        println("1. 수량 변경")
        println("2. 상품 제거")
        println("3. 장바구니 비우기")
        print("선택: ")
        return readln().trim()
    }

    fun readCartQuantityUpdate(): Pair<String, Int> {
        val name = readLineWithMessage("상품명: ")
        val quantityInput = readLineWithMessage("변경할 수량: ")
        val quantity = InputValidator.parsePositiveInt(quantityInput, "수량")

        return name to quantity
    }

    fun readProductNameToRemove(): String {
        return readLineWithMessage("제거할 상품명을 입력하세요: ")
    }

    fun askConfirmCheckout(): Boolean {
        return readYesOrNo("결제를 진행하시겠습니까? (Y/N)")
    }

    fun askViewDetailHistory(): Boolean {
        return readYesOrNo("구매 이력 상세보기를 하시겠습니까? (Y/N)")
    }

    fun readHistoryId(): String {
        return readLineWithMessage("조회할 주문번호를 입력하세요: ")
    }

    fun askViewStatistics(): Boolean {
        return readYesOrNo("카테고리별 통계를 보시겠습니까? (Y/N)")
    }

    fun readNewProductInfo(): NewProductInfo {
        val name = readLineWithMessage("상품명: ")

        val priceInput = readLineWithMessage("가격: ")
        val price = InputValidator.parsePositiveInt(priceInput, "가격")

        val quantityInput = readLineWithMessage("수량: ")
        val quantity = InputValidator.parsePositiveInt(quantityInput, "수량")

        val description = readLineWithMessage("설명 (선택사항): ").takeIf { it.isNotBlank() }

        println("\n카테고리를 선택하세요:")
        ProductCategory.getAllCategories().forEachIndexed { index, category ->
            println("${index + 1}. ${category.displayName}")
        }

        val categoryIndexInput = readLineWithMessage("선택: ")
        val categoryIndex = InputValidator.parseInt(categoryIndexInput, "카테고리 번호") - 1

        val category = ProductCategory.getAllCategories().getOrNull(categoryIndex)
            ?: ProductCategory.ETC

        return NewProductInfo(name, price, quantity, description, category)
    }

    fun readProductUpdateInfo(): Triple<String, Int, Int> {
        val name = readLineWithMessage("수정할 상품명: ")

        val newPrice = InputValidator.parsePositiveInt(
            readLineWithMessage("새 가격: "),
            "가격"
        )

        val newQuantity = InputValidator.parsePositiveInt(
            readLineWithMessage("새 수량: "),
            "수량"
        )

        return Triple(name, newPrice, newQuantity)
    }

    fun readNewPromotionInfo(): NewPromotionInfo {
        val name = readLineWithMessage("프로모션명: ")

        val buy = InputValidator.parsePositiveInt(
            readLineWithMessage("구매 수량 (buy): "),
            "구매 수량"
        )

        val get = InputValidator.parsePositiveInt(
            readLineWithMessage("증정 수량 (get): "),
            "증정 수량"
        )

        val start = readLineWithMessage("시작일 (YYYY-MM-DD): ").also {
            InputValidator.validateDate(it, "시작일")
        }

        val end = readLineWithMessage("종료일 (YYYY-MM-DD): ").also {
            InputValidator.validateDate(it, "종료일")
        }

        return NewPromotionInfo(name, buy, get, start, end)
    }

    fun askContinueOwnerMode(): Boolean {
        return readYesOrNo("계속 관리하시겠습니까? (Y/N)")
    }

    fun readPurchaseItems(): List<Pair<String, Int>> {
        println("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])")
        val input = readln().trim()
        return store.util.Validator.validatePurchaseInput(input)
    }

    fun readYesOrNo(message: String): Boolean {
        println(message)
        val input = readln().trim()
        return InputValidator.validateYesOrNo(input)
    }

    fun askAddFreeItem(productName: String): Boolean {
        return readYesOrNo("현재 ${productName}은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)")
    }

    fun askBuyWithoutPromotion(productName: String, quantity: Int): Boolean {
        return readYesOrNo("현재 ${productName} ${quantity}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)")
    }

    fun askMembershipDiscount(): Boolean {
        return readYesOrNo("멤버십 할인을 받으시겠습니까? (Y/N)")
    }

    fun askContinueShopping(): Boolean {
        return readYesOrNo("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)")
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