package store

import store.controller.StoreController
import store.domain.product.ProductInventory
import store.domain.promotion.PromotionRepository
import store.domain.user.UserRepository
import store.domain.purchase.PurchaseHistoryRepository
import store.service.PaymentService
import store.service.PromotionService
import store.service.StoreService
import store.util.FileReader
import store.view.*

fun main() {
    try {
        val dependencies = initializeDependencies()
        val controller = createController(dependencies)
        controller.run()
    } catch (e: Exception) {
        println("[ERROR] 애플리케이션 실행 중 오류가 발생했습니다: ${e.message}")
        e.printStackTrace()
    }
}

private fun initializeDependencies(): AppDependencies {
    // 데이터 로드
    val products = FileReader.readProducts()
    val promotions = FileReader.readPromotions()

    // 리포지토리 초기화
    val productInventory = ProductInventory()
    products.forEach { productInventory.addProduct(it) }

    val promotionRepository = PromotionRepository()
    promotions.forEach { promotionRepository.save(it) }

    val userRepository = UserRepository()
    val historyRepository = PurchaseHistoryRepository()

    // View 초기화
    val inputView = InputView()
    val outputView = OutputView()
    val commonOutput = CommonOutput()
    val customerOutput = CustomerOutput(commonOutput)
    val adminOutput = AdminOutput(commonOutput)

    // Service 초기화
    val promotionService = PromotionService(promotionRepository)
    val storeService = StoreService(
        productInventory = productInventory,
        promotionService = promotionService,
        inputView = inputView,
        customerOutput = customerOutput
    )
    val paymentService = PaymentService()

    return AppDependencies(
        storeService = storeService,
        paymentService = paymentService,
        inputView = inputView,
        outputView = outputView,
        customerOutput = customerOutput,
        adminOutput = adminOutput,
        userRepository = userRepository,
        historyRepository = historyRepository
    )
}

private fun createController(dependencies: AppDependencies): StoreController {
    return StoreController(
        storeService = dependencies.storeService,
        paymentService = dependencies.paymentService,
        inputView = dependencies.inputView,
        outputView = dependencies.outputView,
        customerOutput = dependencies.customerOutput,
        adminOutput = dependencies.adminOutput,
        userRepository = dependencies.userRepository,
        historyRepository = dependencies.historyRepository
    )
}

private data class AppDependencies(
    val storeService: StoreService,
    val paymentService: PaymentService,
    val inputView: InputView,
    val outputView: OutputView,
    val customerOutput: CustomerOutput,
    val adminOutput: AdminOutput,
    val userRepository: UserRepository,
    val historyRepository: PurchaseHistoryRepository
)