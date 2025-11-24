package store.domain.purchase

import store.domain.product.Product

class Cart(
    val userId: String,
    private val items: MutableMap<String, CartItem> = mutableMapOf()
) {
    fun addItem(product: Product, quantity: Int) {
        val existingItem = items[product.name]

        if (existingItem != null) {
            items[product.name] = existingItem.increaseQuantity(quantity)
        } else {
            items[product.name] = CartItem(product, quantity)
        }
    }

    fun removeItem(productName: String) {
        items.remove(productName)
    }

    fun updateQuantity(productName: String, quantity: Int) {
        val item = items[productName]
            ?: throw IllegalArgumentException("[ERROR] 장바구니에 없는 상품입니다.")
        items[productName] = item.updateQuantity(quantity)
    }

    fun getItem(productName: String): CartItem? {
        return items[productName]
    }

    fun getAllItems(): List<CartItem> {
        return items.values.toList()
    }

    fun getTotalItemCount(): Int {
        return items.values.sumOf { it.quantity }
    }

    fun getTotalPrice(): Int {
        return items.values.sumOf { it.getTotalPrice() }
    }

    fun clear() {
        items.clear()
    }

    fun isEmpty(): Boolean = items.isEmpty()
}