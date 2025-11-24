package store.service

import store.domain.product.Product
import store.domain.product.ProductInventory
import store.domain.promotion.Promotion
import store.domain.purchase.PurchaseItem
import store.view.CustomerOutput
import store.view.InputView

class PurchaseProcessor(
    private val productInventory: ProductInventory,
    private val promotionService: PromotionService,
    private val inputView: InputView?,
    private val customerOutput: CustomerOutput?
) {

    fun processSingleProduct(
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

        if (promotion == null) {
            return processWithoutPromotion(name, requestQuantity, regularProduct ?: promotionProduct)
        }

        val finalQuantity = handleFreeItemOffer(name, requestQuantity, promotion, promotionProduct.quantity)
        val promotionResult = calculatePromotion(finalQuantity, promotion, promotionProduct.quantity)

        handleNonPromotionPurchase(name, promotionResult, regularProduct)

        return createPurchaseItem(name, finalQuantity, promotionProduct.price, promotionResult)
    }

    private fun handleFreeItemOffer(
        name: String,
        requestQuantity: Int,
        promotion: Promotion,
        promotionStock: Int
    ): Int {
        var finalQuantity = requestQuantity

        if (promotion.canGetMoreFree(requestQuantity) &&
            promotionStock >= requestQuantity + promotion.get
        ) {
            customerOutput?.askAddFreeItem(name)
            if (inputView?.readYesOrNo() == true) {
                finalQuantity += promotion.get
            }
        }

        return finalQuantity
    }

    private fun calculatePromotion(
        quantity: Int,
        promotion: Promotion,
        promotionStock: Int
    ): PromotionService.PromotionResult {
        return promotionService.calculatePromotionBenefit(quantity, promotion, promotionStock)
    }

    private fun handleNonPromotionPurchase(
        name: String,
        promotionResult: PromotionService.PromotionResult,
        regularProduct: Product?
    ) {
        if (promotionResult.nonPromotionQuantity > 0) {
            val regularStock = regularProduct?.quantity ?: 0

            require(regularStock >= promotionResult.nonPromotionQuantity) {
                "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요."
            }

            customerOutput?.askBuyWithoutPromotion(name, promotionResult.nonPromotionQuantity)

            if (inputView?.readYesOrNo() == false) {
                throw SkipNonPromotionException(promotionResult.promotionQuantity)
            }
        }
    }

    private fun createPurchaseItem(
        name: String,
        quantity: Int,
        price: Int,
        promotionResult: PromotionService.PromotionResult
    ): PurchaseItem {
        return PurchaseItem(
            productName = name,
            quantity = quantity,
            price = price,
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

    class SkipNonPromotionException(val promotionQuantity: Int) : Exception()
}