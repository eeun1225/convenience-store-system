package store

import store.domain.product.ProductInventory
import store.domain.promotion.PromotionRepository
import store.service.PaymentService
import store.service.PromotionService
import store.service.StoreService
import store.util.FileReader
import store.view.InputView
import store.view.OutputView
import store.controller.StoreController

fun main() {
    try {
        val products = FileReader.readProducts()
        val promotions = FileReader.readPromotions()

        val productInventory = ProductInventory()
        products.forEach { productInventory.addProduct(it) }

        val promotionRepository = PromotionRepository()
        promotions.forEach { promotionRepository.save(it) }

        val inputView = InputView()
        val outputView = OutputView()
        val promotionService = PromotionService(promotionRepository)
        val storeService = StoreService(productInventory, promotionService, inputView)
        val paymentService = PaymentService()

        val controller = StoreController(
            storeService = storeService,
            paymentService = paymentService,
            inputView = inputView,
            outputView = outputView
        )

        controller.run()

    } catch (e: Exception) {
        println("[ERROR] 애플리케이션 실행 중 오류가 발생했습니다: ${e.message}")
    }
}