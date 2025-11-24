package store.view

import store.domain.product.Product
import store.domain.purchase.Cart
import store.domain.purchase.PurchaseHistory
import store.domain.purchase.Receipt
import store.controller.CustomerAction
import store.controller.AdminAction

class OutputView {
    fun printModeSelection() {
        println("\n╔════════════════════════════╗")
        println("║      W편의점 시스템        ║")
        println("╚════════════════════════════╝")
        println("1. 구매자 모드")
        println("2. 사장님 모드")
        print("선택: ")
    }

    fun printCustomerMenu() {
        println("\n╔════════════════════════════╗")
        println("║       구매자 메뉴          ║")
        println("╚════════════════════════════╝")
        CustomerAction.entries.forEachIndexed { index, action ->
            println("${index + 1}. ${action.displayName}")
        }
        print("선택: ")
    }

    fun printOwnerMenu() {
        println("\n╔════════════════════════════╗")
        println("║       관리자 메뉴          ║")
        println("╚════════════════════════════╝")
        AdminAction.entries.forEachIndexed { index, action ->
            println("${index + 1}. ${action.displayName}")
        }
        print("선택: ")
    }

    fun printWelcome() {
        println("\n안녕하세요. W편의점입니다.")
        println("현재 보유하고 있는 상품입니다.\n")
    }

    fun printProducts(products: List<Product>) {
        val groupedProducts = products.groupBy { it.name }

        groupedProducts.forEach { (name, productList) ->
            productList.forEach { product ->
                printProduct(product)
            }

            if (productList.size == 1 && productList[0].hasPromotion()) {
                println("- $name ${formatPrice(productList[0].price)} 재고 없음")
            }
        }
    }

    private fun printProduct(product: Product) {
        val stockInfo = if (product.quantity == 0) "재고 없음" else "${product.quantity}개"
        val promotionInfo = product.promotion?.let { " $it" } ?: ""
        println("- ${product.name} ${formatPrice(product.price)} $stockInfo$promotionInfo")
    }

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

    fun printPurchaseComplete() {
        println("\n✓ 구매가 완료되었습니다. 감사합니다!")
    }

    fun printReceipt(receipt: Receipt) {
        println("\n==============W 편의점================")
        println("상품명\t\t수량\t금액")

        receipt.items.forEach { item ->
            println("${item.productName}\t\t${item.quantity}\t${formatPrice(item.getTotalAmount())}")
        }

        val freeItems = receipt.getFreeItems()
        if (freeItems.isNotEmpty()) {
            println("=============증\t정===============")
            freeItems.forEach { (name, quantity) ->
                println("$name\t\t$quantity")
            }
        }

        println("====================================")
        println("총구매액\t\t${receipt.getTotalQuantity()}\t${formatPrice(receipt.getTotalAmount())}")
        println("행사할인\t\t\t-${formatPrice(receipt.getPromotionDiscount())}")
        println("멤버십할인\t\t\t-${formatPrice(receipt.membershipDiscount)}")
        println("내실돈\t\t\t ${formatPrice(receipt.getFinalAmount())}\n")
    }

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

    fun printOwnerInventory(products: List<Product>) {
        println("\n╔════════════════════════════════════════╗")
        println("║       재고 현황 (사장님 모드)         ║")
        println("╚════════════════════════════════════════╝")

        products.groupBy { it.category }.forEach { (category, prods) ->
            println("\n[${category.displayName}]")
            prods.forEach { product ->
                val status = when {
                    product.quantity == 0 -> "⚠️ 품절"
                    product.quantity < 5 -> "⚠️ 재고 부족"
                    else -> "✅ 정상"
                }
                val promoInfo = product.promotion?.let { " ($it)" } ?: ""
                println("  ${product.name} - ${product.quantity}개 (${formatPrice(product.price)}) $status$promoInfo")
            }
        }
        println()
    }

    fun printCategoryStatistics(products: List<Product>) {
        println("\n╔════════════════════════════════════════╗")
        println("║      카테고리별 재고 통계              ║")
        println("╚════════════════════════════════════════╝")

        val stats = products.groupBy { it.category }
            .mapValues { (_, prods) ->
                val totalQuantity = prods.sumOf { it.quantity }
                val totalValue = prods.sumOf { it.quantity * it.price }
                totalQuantity to totalValue
            }

        stats.forEach { (category, stat) ->
            println("${category.displayName}: ${stat.first}개 (재고가치: ${formatPrice(stat.second)})")
        }
        println()
    }

    fun printProductAdded(name: String) {
        println("\n✓ 상품 '$name'이(가) 추가되었습니다.")
    }

    fun printProductUpdated(name: String) {
        println("\n✓ 상품 '$name'이(가) 수정되었습니다.")
    }

    fun printPromotionAdded(name: String) {
        println("\n✓ 프로모션 '$name'이(가) 추가되었습니다.")
    }

    fun printSalesStatistics(histories: List<PurchaseHistory>) {
        println("\n╔════════════════════════════════════════╗")
        println("║           매출 통계                    ║")
        println("╚════════════════════════════════════════╝")

        if (histories.isEmpty()) {
            println("매출 데이터가 없습니다.")
            return
        }

        val totalSales = histories.sumOf { it.getTotalAmount() }
        val totalOrders = histories.size
        val totalItems = histories.sumOf { it.getTotalQuantity() }

        println("총 매출액: ${formatPrice(totalSales)}")
        println("총 주문 수: ${totalOrders}건")
        println("총 판매 상품 수: ${totalItems}개")
        println("평균 주문 금액: ${formatPrice(totalSales / totalOrders)}")

        val productSales = mutableMapOf<String, Int>()
        histories.forEach { history ->
            history.items.forEach { item ->
                productSales[item.productName] =
                    productSales.getOrDefault(item.productName, 0) + item.quantity
            }
        }

        println("\n인기 상품 TOP 5:")
        productSales.entries
            .sortedByDescending { it.value }
            .take(5)
            .forEachIndexed { index, entry ->
                println("  ${index + 1}. ${entry.key} - ${entry.value}개 판매")
            }
        println()
    }

    fun printLowStockWarning(products: List<Product>) {
        println("\n╔════════════════════════════════════════╗")
        println("║       재고 부족 상품 경고              ║")
        println("╚════════════════════════════════════════╝")

        if (products.isEmpty()) {
            println("✓ 재고 부족 상품이 없습니다.")
            return
        }

        products.forEach { product ->
            val status = if (product.quantity == 0) "품절" else "재고 부족 (${product.quantity}개)"
            println("⚠️ ${product.name} - $status")
        }
        println()
    }

    fun printError(message: String) {
        println(message)
    }

    private fun formatPrice(price: Int): String {
        return "%,d원".format(price)
    }
}