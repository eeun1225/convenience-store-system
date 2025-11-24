package store.domain.purchase

data class Receipt(
    val items: List<PurchaseItem>,
    val membershipDiscount: Int
) {
    fun getTotalQuantity(): Int = items.sumOf { it.quantity }

    fun getTotalAmount(): Int = items.sumOf { it.getTotalAmount() }

    fun getPromotionDiscount(): Int = items.sumOf { it.getPromotionDiscount() }

    fun getFinalAmount(): Int {
        return getTotalAmount() - getPromotionDiscount() - membershipDiscount
    }

    fun getFreeItems(): List<Pair<String, Int>> {
        return items.filter { it.freeQuantity > 0 }
            .map { it.productName to it.freeQuantity }
    }
}