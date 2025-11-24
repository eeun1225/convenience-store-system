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
    private val purchaseProcessor = PurchaseProcessor(
        productInventory,
        promotionService,
        inputView,
        customerOutput
    )

    private val inventoryManager = InventoryManager(productInventory)

    // 구매 처리
    fun processPurchase(
        purchaseRequests: List<Pair<String, Int>>,
        currentDate: String
    ): List<PurchaseItem> {
        return purchaseRequests.map { (name, quantity) ->
            purchaseProcessor.processSingleProduct(name, quantity, currentDate)
        }
    }

    fun updateInventory(items: List<PurchaseItem>) {
        inventoryManager.updateInventory(items)
    }

    // 상품 조회
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

    fun getLowStockProducts(threshold: Int = 5): List<Product> {
        return productInventory.getAllProducts()
            .filter { it.quantity <= threshold }
            .sortedBy { it.quantity }
    }

    // 상품 관리
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

    // 프로모션 관리
    fun addCustomPromotion(promotionInfo: NewPromotionInfo) {
        promotionService.addPromotion(promotionInfo)
    }
}