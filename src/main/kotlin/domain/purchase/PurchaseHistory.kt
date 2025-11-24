package store.domain.purchase

import java.time.LocalDateTime

data class PurchaseHistory(
    val id: String,
    val userId: String,
    val items: List<PurchaseItem>,
    val receipt: Receipt,
    val purchasedAt: LocalDateTime = LocalDateTime.now()
) {
    fun getTotalAmount(): Int = receipt.getFinalAmount()

    fun getTotalQuantity(): Int = items.sumOf { it.quantity }

    fun getProductNames(): List<String> = items.map { it.productName }

    fun containsProduct(productName: String): Boolean {
        return items.any { it.productName == productName }
    }
}