package store.view

import store.domain.product.Product
import store.domain.purchase.Cart
import store.domain.purchase.PurchaseHistory
import store.domain.purchase.Receipt
import store.controller.CustomerAction
import store.controller.AdminAction

class OutputView {
    private val receiptPrinter = ReceiptPrinter()
    private val statisticsPrinter = StatisticsPrinter()

    // 메뉴 출력
    fun printModeSelection() {
        println("\n╔══════════════════════════╗")
        println("║        편의점 시스템         ║")
        println("╚══════════════════════════╝")
        println("1. 구매자 모드")
        println("2. 관리자 모드")
        print("선택: ")
    }

    fun printCustomerMenu() {
        printMenuHeader("구매자 메뉴")
        CustomerAction.entries.forEachIndexed { index, action ->
            println("${index + 1}. ${action.displayName}")
        }
        print("선택: ")
    }

    fun printOwnerMenu() {
        printMenuHeader("관리자 메뉴")
        AdminAction.entries.forEachIndexed { index, action ->
            println("${index + 1}. ${action.displayName}")
        }
        print("선택: ")
    }

    private fun printMenuHeader(title: String) {
        println("\n╔════════════════════════════╗")
        println("║       $title          ║")
        println("╚════════════════════════════╝")
    }

    // 상품 출력
    fun printWelcome() {
        println("\n안녕하세요. 편의점입니다.")
        println("현재 보유하고 있는 상품입니다.\n")
    }

    fun printProducts(products: List<Product>) {
        ProductDisplayFormatter.formatProductList(products).forEach { println(it) }
    }

    // 장바구니 출력
    fun printCart(cart: Cart) {
        println("\n╔════════════════════════════╗")
        println("║         장바구니           ║")
        println("╚════════════════════════════╝")

        if (cart.isEmpty()) {
            println("장바구니가 비어있습니다.")
            return
        }

        cart.getAllItems().forEach { item ->
            println("${item.product.name} x ${item.quantity}개 = ${formatPrice(item.getTotalPrice())}")
        }
        println("────────────────────────────")
        println("총 ${cart.getTotalItemCount()}개 상품")
        println("합계: ${formatPrice(cart.getTotalPrice())}")
        println()
    }

    fun printEmptyCart() {
        println("\n장바구니가 비어있습니다.")
    }

    fun printCartUpdated(cart: Cart) {
        println("\n✓ 장바구니가 업데이트되었습니다.")
        printCart(cart)
    }

    fun printCartCleared() {
        println("\n✓ 장바구니를 비웠습니다.")
    }

    // 구매 관련
    fun printPurchaseComplete() {
        println("\n✓ 구매가 완료되었습니다. 감사합니다!")
    }

    fun printReceipt(receipt: Receipt) {
        receiptPrinter.print(receipt)
    }

    // 구매 이력
    fun printPurchaseHistories(histories: List<PurchaseHistory>) {
        println("\n╔════════════════════════════════════════╗")
        println("║           구매 이력                    ║")
        println("╚════════════════════════════════════════╝")

        histories.forEach { history ->
            println("\n[${history.purchasedAt}]")
            println("주문번호: ${history.id.take(8)}")
            println("상품: ${history.getProductNames().joinToString(", ")}")
            println("수량: ${history.getTotalQuantity()}개")
            println("금액: ${formatPrice(history.getTotalAmount())}")
            println("─────────────────────────────────────")
        }
    }

    fun printNoPurchaseHistory() {
        println("\n구매 이력이 없습니다.")
    }

    fun printDetailHistory(history: PurchaseHistory) {
        println("\n╔════════════════════════════════════════╗")
        println("║         구매 이력 상세                 ║")
        println("╚════════════════════════════════════════╝")
        println("주문번호: ${history.id}")
        println("구매일시: ${history.purchasedAt}")
        println("\n구매 상품:")
        history.items.forEach { item ->
            println("  - ${item.productName} x ${item.quantity}개 (${formatPrice(item.price)})")
        }
        printReceipt(history.receipt)
    }

    // 관리자 기능
    fun printOwnerInventory(products: List<Product>) {
        println("\n╔════════════════════════════════════════╗")
        println("║       재고 현황 (사장님 모드)         ║")
        println("╚════════════════════════════════════════╝")

        products.groupBy { it.category }.forEach { (category, prods) ->
            println("\n[${category.displayName}]")
            prods.forEach { product ->
                println(ProductDisplayFormatter.formatInventoryProduct(product))
            }
        }
        println()
    }

    fun printCategoryStatistics(products: List<Product>) {
        statisticsPrinter.printCategoryStatistics(products)
    }

    fun printSalesStatistics(histories: List<PurchaseHistory>) {
        statisticsPrinter.printSalesStatistics(histories)
    }

    fun printLowStockWarning(products: List<Product>) {
        statisticsPrinter.printLowStockWarning(products)
    }

    // 성공 메시지
    fun printProductAdded(name: String) {
        println("\n✓ 상품 '$name'이(가) 추가되었습니다.")
    }

    fun printProductUpdated(name: String) {
        println("\n✓ 상품 '$name'이(가) 수정되었습니다.")
    }

    fun printPromotionAdded(name: String) {
        println("\n✓ 프로모션 '$name'이(가) 추가되었습니다.")
    }

    // 에러 메시지
    fun printError(message: String) {
        println(message)
    }

    private fun formatPrice(price: Int): String {
        return "%,d원".format(price)
    }
}