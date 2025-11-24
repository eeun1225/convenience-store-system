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
}