package store.domain.product

class ProductInventory(
    private val products: MutableMap<String, MutableList<Product>> = mutableMapOf()
) {
    fun addProduct(product: Product) {
        products.getOrPut(product.name) { mutableListOf() }.add(product)
    }

    fun findByName(name: String): List<Product> {
        return products[name] ?: emptyList()
    }

    fun findByCategory(category: ProductCategory): List<Product> {
        return products.values.flatten().filter { it.category == category }
    }

    fun getProductsByCategory(): Map<ProductCategory, List<Product>> {
        return products.values.flatten().groupBy { it.category }
    }

    fun getPromotionProduct(name: String): Product? {
        return findByName(name).firstOrNull { it.hasPromotion() }
    }

    fun getRegularProduct(name: String): Product? {
        return findByName(name).firstOrNull { !it.hasPromotion() }
    }

    fun getTotalQuantity(name: String): Int {
        return findByName(name).sumOf { it.quantity }
    }

    fun getTotalQuantityByCategory(category: ProductCategory): Int {
        return findByCategory(category).sumOf { it.quantity }
    }

    fun updateProduct(name: String, isPromotion: Boolean, newProduct: Product) {
        val productList = products[name] ?: return
        val index = productList.indexOfFirst {
            if (isPromotion) it.hasPromotion() else !it.hasPromotion()
        }
        if (index != -1) {
            productList[index] = newProduct
        }
    }

    fun getAllProducts(): List<Product> {
        return products.values.flatten().sortedBy { it.name }
    }

    fun getCategories(): Set<ProductCategory> {
        return products.values.flatten().map { it.category }.toSet()
    }
}