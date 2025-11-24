package store.controller

import store.domain.purchase.PurchaseHistoryRepository
import store.domain.user.UserMode
import store.domain.user.UserRepository
import store.service.PaymentService
import store.service.StoreService
import store.view.*

class StoreController(
    private val storeService: StoreService,
    private val paymentService: PaymentService,
    private val inputView: InputView,
    private val outputView: OutputView,
    private val customerOutput: CustomerOutput,
    private val adminOutput: AdminOutput,
    private val userRepository: UserRepository = UserRepository(),
    private val historyRepository: PurchaseHistoryRepository = PurchaseHistoryRepository()
) {

    fun run() {
        val mode = selectMode()

        when (mode) {
            UserMode.CUSTOMER -> runCustomerMode()
            UserMode.ADMIN -> runAdminMode()
        }
    }

    private fun selectMode(): UserMode {
        return try {
            outputView.printModeSelection()
            val input = inputView.readSelection()
            UserMode.select(input)
                ?: throw IllegalArgumentException("잘못된 선택입니다.")
        } catch (e: IllegalArgumentException) {
            outputView.printError("[ERROR] ${e.message}")
            selectMode()
        }
    }

    private fun runCustomerMode() {
        val customerController = CustomerController(
            storeService = storeService,
            paymentService = paymentService,
            inputView = inputView,
            outputView = outputView,
            customerOutput = customerOutput,
            userRepository = userRepository,
            historyRepository = historyRepository
        )
        customerController.run()
    }

    private fun runAdminMode() {
        val adminController = AdminController(
            storeService = storeService,
            inputView = inputView,
            outputView = outputView,
            adminOutput = adminOutput,
            historyRepository = historyRepository
        )
        adminController.run()
    }
}