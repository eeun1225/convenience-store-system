package store.view

import store.domain.product.Product

object ProductDisplayFormatter {

    fun formatProductList(products: List<Product>): List<String> {
        val groupedProducts = products.groupBy { it.name }
        val result = mutableListOf<String>()

        groupedProducts.forEach { (name, productList) ->
            productList.forEach { product ->
                result.add(formatProduct(product))
            }

            // 프로모션 상품만 있으면 일반 재고 없음 표시
            if (productList.size == 1 && productList[0].hasPromotion()) {
                result.add(formatOutOfStock(name, productList[0].price))
            }
        }

        return result
    }

    fun formatProduct(product: Product): String {
        val stockInfo = formatStockInfo(product.quantity)
        val promotionInfo = product.promotion?.let { " $it" } ?: ""
        return "- ${product.name} ${formatPrice(product.price)} $stockInfo$promotionInfo"
    }

    fun formatInventoryProduct(product: Product): String {
        val status = getStockStatus(product.quantity)
        val promoInfo = product.promotion?.let { " ($it)" } ?: ""
        return "  ${product.name} - ${product.quantity}개 (${formatPrice(product.price)}) $status$promoInfo"
    }

    private fun formatStockInfo(quantity: Int): String {
        return if (quantity == 0) "재고 없음" else "${quantity}개"
    }

    private fun formatOutOfStock(name: String, price: Int): String {
        return "- $name ${formatPrice(price)} 재고 없음"
    }

    private fun getStockStatus(quantity: Int): String {
        return when {
            quantity == 0 -> "⚠️ 품절"
            quantity < 5 -> "⚠️ 재고 부족"
            else -> "✅ 정상"
        }
    }

    fun formatPrice(price: Int): String {
        return "%,d원".format(price)
    }
}