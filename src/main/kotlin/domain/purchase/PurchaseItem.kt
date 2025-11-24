package store.domain.purchase

data class PurchaseItem(
    val productName: String,
    val quantity: Int,
    val price: Int,
    val promotionQuantity: Int = 0,
    val freeQuantity: Int = 0
) {
    fun getTotalAmount(): Int = quantity * price

    fun getPromotionDiscount(): Int = freeQuantity * price

    fun getNonPromotionQuantity(): Int = quantity - promotionQuantity
}