package store.domain.purchase

import store.domain.product.Product
import java.time.LocalDateTime

data class CartItem(
    val product: Product,
    val quantity: Int,
    val addedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(quantity > 0) { "[ERROR] 수량은 1개 이상이어야 합니다." }
    }

    fun getTotalPrice(): Int = product.price * quantity

    fun updateQuantity(newQuantity: Int): CartItem {
        return copy(quantity = newQuantity)
    }

    fun increaseQuantity(amount: Int = 1): CartItem {
        return copy(quantity = quantity + amount)
    }

    fun decreaseQuantity(amount: Int = 1): CartItem {
        val newQuantity = quantity - amount
        require(newQuantity > 0) { "[ERROR] 수량은 1개 이상이어야 합니다." }
        return copy(quantity = newQuantity)
    }
}
