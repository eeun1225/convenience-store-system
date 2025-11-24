package store.service

import store.domain.product.ProductInventory
import store.domain.purchase.PurchaseItem

class InventoryManager(
    private val productInventory: ProductInventory
) {

    fun updateInventory(items: List<PurchaseItem>) {
        items.forEach { item ->
            updateProductStock(item)
        }
    }

    private fun updateProductStock(item: PurchaseItem) {
        val promotionProduct = productInventory.getPromotionProduct(item.productName)
        val regularProduct = productInventory.getRegularProduct(item.productName)

        var remainingQuantity = item.quantity + item.freeQuantity

        // 프로모션 재고부터 차감
        if (promotionProduct != null && promotionProduct.quantity > 0) {
            remainingQuantity = deductFromPromotion(item.productName, promotionProduct, remainingQuantity)
        }

        // 남은 수량은 일반 재고에서 차감
        if (remainingQuantity > 0 && regularProduct != null) {
            deductFromRegular(item.productName, regularProduct, remainingQuantity)
        }
    }

    private fun deductFromPromotion(
        productName: String,
        promotionProduct: store.domain.product.Product,
        quantity: Int
    ): Int {
        val deductAmount = minOf(quantity, promotionProduct.quantity)
        val updatedProduct = promotionProduct.decreaseQuantity(deductAmount)
        productInventory.updateProduct(productName, true, updatedProduct)
        return quantity - deductAmount
    }

    private fun deductFromRegular(
        productName: String,
        regularProduct: store.domain.product.Product,
        quantity: Int
    ) {
        val updatedProduct = regularProduct.decreaseQuantity(quantity)
        productInventory.updateProduct(productName, false, updatedProduct)
    }
}