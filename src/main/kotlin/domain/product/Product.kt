package store.domain.product

data class Product(
    val name: String,
    val price: Int,
    val quantity: Int,
    val promotion: String? = null,
    val description: String? = null,
    val category: ProductCategory = ProductCategory.fromProductName(name)
) {
    fun hasPromotion() = promotion != null

    fun canPurchase(requestQuantity: Int): Boolean {
        return quantity >= requestQuantity
    }

    fun decreaseQuantity(amount: Int): Product {
        require(amount <= quantity) { "재고가 부족합니다" }
        return copy(quantity = quantity - amount)
    }

    fun isInCategory(category: ProductCategory): Boolean {
        return this.category == category
    }
}