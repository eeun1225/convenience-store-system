package store.view

import store.domain.product.Product
import store.domain.product.ProductCategory
import store.domain.purchase.PurchaseHistory

class StatisticsPrinter {

    fun printCategoryStatistics(products: List<Product>) {
        println("\n╔════════════════════════════════════════╗")
        println("║      카테고리별 재고 통계              ║")
        println("╚════════════════════════════════════════╝")

        val stats = calculateCategoryStats(products)
        stats.forEach { (category, stat) ->
            println("${category.displayName}: ${stat.first}개 (재고가치: ${formatPrice(stat.second)})")
        }
        println()
    }

    fun printSalesStatistics(histories: List<PurchaseHistory>) {
        println("\n╔════════════════════════════════════════╗")
        println("║           매출 통계                    ║")
        println("╚════════════════════════════════════════╝")

        if (histories.isEmpty()) {
            println("매출 데이터가 없습니다.")
            return
        }

        printBasicSalesInfo(histories)
        printTopProducts(histories)
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

    private fun calculateCategoryStats(products: List<Product>): Map<ProductCategory, Pair<Int, Int>> {
        return products.groupBy { it.category }
            .mapValues { (_, prods) ->
                val totalQuantity = prods.sumOf { it.quantity }
                val totalValue = prods.sumOf { it.quantity * it.price }
                totalQuantity to totalValue
            }
    }

    private fun printBasicSalesInfo(histories: List<PurchaseHistory>) {
        val totalSales = histories.sumOf { it.getTotalAmount() }
        val totalOrders = histories.size
        val totalItems = histories.sumOf { it.getTotalQuantity() }

        println("총 매출액: ${formatPrice(totalSales)}")
        println("총 주문 수: ${totalOrders}건")
        println("총 판매 상품 수: ${totalItems}개")
        println("평균 주문 금액: ${formatPrice(totalSales / totalOrders)}")
    }

    private fun printTopProducts(histories: List<PurchaseHistory>) {
        val productSales = calculateProductSales(histories)

        println("\n인기 상품 TOP 5:")
        productSales.entries
            .sortedByDescending { it.value }
            .take(5)
            .forEachIndexed { index, entry ->
                println("  ${index + 1}. ${entry.key} - ${entry.value}개 판매")
            }
    }

    private fun calculateProductSales(histories: List<PurchaseHistory>): Map<String, Int> {
        val productSales = mutableMapOf<String, Int>()
        histories.forEach { history ->
            history.items.forEach { item ->
                productSales[item.productName] =
                    productSales.getOrDefault(item.productName, 0) + item.quantity
            }
        }
        return productSales
    }

    private fun formatPrice(price: Int): String {
        return "%,d원".format(price)
    }
}