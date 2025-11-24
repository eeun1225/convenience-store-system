package store.domain.product

class ProductRepository(
    val inventory: ProductInventory = ProductInventory()
) {
    fun save(product: Product) {
        inventory.addProduct(product)
    }

    fun saveAll(products: List<Product>) {
        products.forEach { save(it) }
    }

    fun findByName(name: String): List<Product> {
        return inventory.findByName(name)
    }

    fun findPromotionProduct(name: String): Product? {
        return inventory.getPromotionProduct(name)
    }

    fun findAll(): List<Product> {
        return inventory.getAllProducts()
    }

    fun getTotalStock(name: String): Int {
        return inventory.getTotalQuantity(name)
    }

    fun update(name: String, isPromotion: Boolean, product: Product) {
        inventory.updateProduct(name, isPromotion, product)
    }

    fun exists(name: String): Boolean {
        return inventory.findByName(name).isNotEmpty()
    }
}