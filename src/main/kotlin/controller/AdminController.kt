package store.controller

import store.domain.purchase.PurchaseHistoryRepository
import store.domain.user.User
import store.service.StoreService
import store.view.AdminOutput
import store.view.InputView
import store.view.OutputView
import java.util.UUID

class AdminController(
    private val storeService: StoreService,
    private val inputView: InputView,
    private val outputView: OutputView,
    private val adminOutput: AdminOutput,
    private val historyRepository: PurchaseHistoryRepository
) {

    fun run() {
        val admin = createOrLoginAdmin()

        do {
            try {
                processAdminAction(admin)
            } catch (e: IllegalArgumentException) {
                outputView.printError("[ERROR] ${e.message}")
            }
        } while (askContinue())
    }

    private fun createOrLoginAdmin(): User.Admin {
        return try {
            adminOutput.askAdminNumber()
            val number = inputView.readAdminNumber()

            adminOutput.askPassword()
            val password = inputView.readPassword()

            User.Admin(
                id = UUID.randomUUID().toString(),
                number = number,
                password = password
            )
        } catch (e: IllegalArgumentException) {
            outputView.printError("[ERROR] ${e.message}")
            createOrLoginAdmin()
        }
    }

    private fun selectAction(): AdminAction {
        return try {
            outputView.printOwnerMenu()
            val input = inputView.readSelection()
            AdminAction.select(input)
                ?: throw IllegalArgumentException("잘못된 선택입니다.")
        } catch (e: IllegalArgumentException) {
            outputView.printError("[ERROR] ${e.message}")
            selectAction()
        }
    }

    private fun processAdminAction(admin: User.Admin) {
        when (val action = selectAction()) {
            AdminAction.VIEW_INVENTORY -> viewInventory()
            AdminAction.ADD_PRODUCT -> addProduct()
            AdminAction.UPDATE_PRODUCT -> updateProduct()
            AdminAction.ADD_PROMOTION -> addPromotion()
            AdminAction.VIEW_SALES -> viewSalesStatistics()
            AdminAction.MANAGE_STOCK -> manageStock()
        }
    }

    private fun viewInventory() {
        adminOutput.printAdminInventory(storeService.getAllProducts())

        adminOutput.askViewStatistics()
        if (inputView.readYesOrNo()) {
            adminOutput.printCategoryStatistics(storeService.getAllProducts())
        }
    }

    private fun addProduct() {
        adminOutput.showCategoryMenu(store.domain.product.ProductCategory.getAllCategories())
        val productInfo = inputView.readNewProductInfo()
        storeService.addNewProduct(productInfo)
        outputView.printProductAdded(productInfo.name)
    }

    private fun updateProduct() {
        adminOutput.askProductToUpdate()
        adminOutput.askUpdatePrice()
        adminOutput.askUpdateQuantity()

        val (name, price, quantity) = inputView.readProductUpdateInfo()
        storeService.updateProductInfo(name, price, quantity)
        outputView.printProductUpdated(name)
    }

    private fun addPromotion() {
        val promotionInfo = inputView.readNewPromotionInfo()
        storeService.addCustomPromotion(promotionInfo)
        outputView.printPromotionAdded(promotionInfo.name)
    }

    private fun viewSalesStatistics() {
        val allHistories = historyRepository.histories.values.flatten()
        outputView.printSalesStatistics(allHistories)
    }

    private fun manageStock() {
        outputView.printLowStockWarning(storeService.getLowStockProducts())
    }

    private fun askContinue(): Boolean {
        return try {
            adminOutput.askContinueAdminMode()
            inputView.readYesOrNo()
        } catch (e: IllegalArgumentException) {
            outputView.printError("[ERROR] ${e.message}")
            askContinue()
        }
    }
}