package store.service

import store.domain.product.Product
import store.domain.product.ProductInventory
import store.domain.purchase.PurchaseItem
import store.view.CustomerOutput
import store.view.InputView
import store.view.NewProductInfo
import store.view.NewPromotionInfo

class StoreService(
    private val productInventory: ProductInventory,
    private val promotionService: PromotionService,
    private val inputView: InputView? = null,
    private val customerOutput: CustomerOutput? = null
) {
    fun processPurchase(
        purchaseRequests: List<Pair<String, Int>>,
        currentDate: String
    ): List<PurchaseItem> {
        return purchaseRequests.map { (name, quantity) ->
            processSingleProduct(name, quantity, currentDate)
        }
    }

    private fun processSingleProduct(
        name: String,
        requestQuantity: Int,
        currentDate: String
    ): PurchaseItem {
        val promotionProduct = productInventory.getPromotionProduct(name)
        val regularProduct = productInventory.getRegularProduct(name)

        validateProductExists(name, promotionProduct, regularProduct)
        validateStock(name, requestQuantity)

        return if (promotionProduct != null && promotionProduct.quantity > 0) {
            processWithPromotion(name, requestQuantity, promotionProduct, regularProduct, currentDate)
        } else {
            processWithoutPromotion(name, requestQuantity, regularProduct!!)
        }
    }

    private fun processWithPromotion(
        name: String,
        requestQuantity: Int,
        promotionProduct: Product,
        regularProduct: Product?,
        currentDate: String
    ): PurchaseItem {

        val promotion = promotionService.getActivePromotion(promotionProduct.promotion!!, currentDate)

        // 프로모션 없으면 일반 구매 처리
        if (promotion == null) {
            return processWithoutPromotion(name, requestQuantity, regularProduct ?: promotionProduct)
        }

        var finalQuantity = requestQuantity
        val promotionStock = promotionProduct.quantity

        if (promotion.canGetMoreFree(requestQuantity) &&
            promotionStock >= requestQuantity + promotion.get
        ) {
            customerOutput?.askAddFreeItem(name)

            val shouldAdd = inputView?.readYesOrNo() ?: false
            if (shouldAdd) {
                finalQuantity += promotion.get
            }
        }

        // 프로모션 계산
        val promotionResult = promotionService.calculatePromotionBenefit(
            quantity = finalQuantity,
            promotion = promotion,
            promotionStock = promotionStock
        )

        // 프로모션 재고 부족 시 정가 구매 안내
        if (promotionResult.nonPromotionQuantity > 0) {
            val regularStock = regularProduct?.quantity ?: 0

            if (regularStock < promotionResult.nonPromotionQuantity) {
                throw IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.")
            }

            customerOutput?.askBuyWithoutPromotion(
                name,
                promotionResult.nonPromotionQuantity
            )

            val shouldBuy = inputView?.readYesOrNo() ?: true

            if (!shouldBuy) {
                finalQuantity = promotionResult.promotionQuantity

                return PurchaseItem(
                    productName = name,
                    quantity = finalQuantity,
                    price = promotionProduct.price,
                    promotionQuantity = promotionResult.promotionQuantity,
                    freeQuantity = promotionResult.freeQuantity
                )
            }
        }

        return PurchaseItem(
            productName = name,
            quantity = finalQuantity,
            price = promotionProduct.price,
            promotionQuantity = promotionResult.promotionQuantity,
            freeQuantity = promotionResult.freeQuantity
        )
    }

    private fun processWithoutPromotion(
        name: String,
        requestQuantity: Int,
        product: Product
    ): PurchaseItem {
        return PurchaseItem(
            productName = name,
            quantity = requestQuantity,
            price = product.price,
            promotionQuantity = 0,
            freeQuantity = 0
        )
    }

    fun updateInventory(items: List<PurchaseItem>) {
        items.forEach { item ->
            updateProductStock(item)
        }
    }

    private fun updateProductStock(item: PurchaseItem) {
        val promotionProduct = productInventory.getPromotionProduct(item.productName)
        val regularProduct = productInventory.getRegularProduct(item.productName)

        var remainingQuantity = item.quantity + item.freeQuantity

        if (promotionProduct != null && promotionProduct.quantity > 0) {
            val deductFromPromotion = minOf(remainingQuantity, promotionProduct.quantity)
            val updatedPromotionProduct = promotionProduct.decreaseQuantity(deductFromPromotion)
            productInventory.updateProduct(item.productName, true, updatedPromotionProduct)
            remainingQuantity -= deductFromPromotion
        }

        if (remainingQuantity > 0 && regularProduct != null) {
            val updatedRegularProduct = regularProduct.decreaseQuantity(remainingQuantity)
            productInventory.updateProduct(item.productName, false, updatedRegularProduct)
        }
    }

    fun getAllProducts(): List<Product> {
        return productInventory.getAllProducts()
    }

    fun getProductForCart(name: String): Product {
        val promotionProduct = productInventory.getPromotionProduct(name)
        val regularProduct = productInventory.getRegularProduct(name)

        require(promotionProduct != null || regularProduct != null) {
            "[ERROR] 존재하지 않는 상품입니다."
        }

        return promotionProduct ?: regularProduct!!
    }

    fun addNewProduct(productInfo: NewProductInfo) {
        val product = Product(
            name = productInfo.name,
            price = productInfo.price,
            quantity = productInfo.quantity,
            promotion = null,
            description = productInfo.description,
            category = productInfo.category
        )
        productInventory.addProduct(product)
    }

    fun updateProductInfo(name: String, price: Int, quantity: Int) {
        val regularProduct = productInventory.getRegularProduct(name)
            ?: throw IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다.")

        val updatedProduct = regularProduct.copy(price = price, quantity = quantity)
        productInventory.updateProduct(name, false, updatedProduct)
    }

    fun addCustomPromotion(promotionInfo: NewPromotionInfo) {
        promotionService.addPromotion(promotionInfo)
    }

    fun getLowStockProducts(threshold: Int = 5): List<Product> {
        return productInventory.getAllProducts()
            .filter { it.quantity <= threshold }
            .sortedBy { it.quantity }
    }

    private fun validateProductExists(
        name: String,
        promotionProduct: Product?,
        regularProduct: Product?
    ) {
        require(promotionProduct != null || regularProduct != null) {
            "[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요."
        }
    }

    private fun validateStock(name: String, quantity: Int) {
        val totalStock = productInventory.getTotalQuantity(name)
        require(totalStock >= quantity) {
            "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요."
        }
    }
}