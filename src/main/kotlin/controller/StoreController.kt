package store.controller

import store.domain.purchase.Cart
import store.domain.purchase.PurchaseHistory
import store.domain.purchase.PurchaseHistoryRepository
import store.domain.user.User
import store.domain.user.UserMode
import store.service.PaymentService
import store.service.StoreService
import store.view.InputView
import store.view.OutputView
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class StoreController(
    private val storeService: StoreService,
    private val paymentService: PaymentService,
    private val inputView: InputView,
    private val outputView: OutputView,
    private val historyRepository: PurchaseHistoryRepository = PurchaseHistoryRepository()
) {
    private val carts = mutableMapOf<String, Cart>()

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
                ?: throw IllegalArgumentException("[ERROR] 잘못된 선택입니다.")
        } catch (e: IllegalArgumentException) {
            outputView.printError(e.message ?: "[ERROR] 잘못된 입력입니다.")
            selectMode()
        }
    }

    private fun runCustomerMode() {
        val customer = createOrLoginCustomer()
        val cart = carts.getOrPut(customer.id) { Cart(customer.id) }

        var shouldContinue = true
        while (shouldContinue) {
            try {
                processCustomerAction(customer, cart)
                // 결제 완료 후에만 계속 쇼핑할지 물어봄
            } catch (e: IllegalArgumentException) {
                outputView.printError(e.message ?: "[ERROR] 잘못된 입력입니다.")
            }
        }
    }

    private fun processCustomerAction(customer: User.Customer, cart: Cart) {
        val action = selectCustomerAction()

        when (action) {
            CustomerAction.VIEW_PRODUCTS -> viewProducts()
            CustomerAction.ADD_TO_CART -> addToCart(cart)
            CustomerAction.VIEW_CART -> viewCart(cart)
            CustomerAction.CHECKOUT -> checkout(customer, cart)
            CustomerAction.VIEW_HISTORY -> viewPurchaseHistory(customer.id)
        }
    }

    private fun addToCart(cart: Cart) {
        outputView.printWelcome()
        outputView.printProducts(storeService.getAllProducts())

        val purchaseRequests = inputView.readPurchaseItems()

        purchaseRequests.forEach { (name, quantity) ->
            val product = storeService.getProductForCart(name)
            cart.addItem(product, quantity)
        }

        outputView.printCartUpdated(cart)
    }

    private fun viewCart(cart: Cart) {
        if (cart.isEmpty()) {
            outputView.printEmptyCart()
            return
        }

        outputView.printCart(cart)

        if (inputView.askModifyCart()) {
            modifyCart(cart)
        }
    }

    private fun modifyCart(cart: Cart) {
        outputView.printCart(cart)
        val action = inputView.readCartModifyAction()

        when (action) {
            "1" -> {
                val (name, quantity) = inputView.readCartQuantityUpdate()
                cart.updateQuantity(name, quantity)
                outputView.printCartUpdated(cart)
            }
            "2" -> {
                val name = inputView.readProductNameToRemove()
                cart.removeItem(name)
                outputView.printCartUpdated(cart)
            }
            "3" -> {
                cart.clear()
                outputView.printCartCleared()
            }
        }
    }

    private fun checkout(customer: User.Customer, cart: Cart) {
        if (cart.isEmpty()) {
            outputView.printEmptyCart()
            return
        }

        outputView.printCart(cart)

        if (!inputView.askConfirmCheckout()) {
            return
        }

        val currentDate = LocalDate.now().toString()
        val cartItems = cart.getAllItems().map { it.product.name to it.quantity }

        val purchaseItems = storeService.processPurchase(cartItems, currentDate)

        val applyMembership = if (customer.isMember) {
            inputView.askMembershipDiscount()
        } else {
            false
        }

        val receipt = paymentService.createReceipt(purchaseItems, applyMembership)
        storeService.updateInventory(purchaseItems)

        val history = PurchaseHistory(
            id = UUID.randomUUID().toString(),
            userId = customer.id,
            items = purchaseItems,
            receipt = receipt,
            purchasedAt = LocalDateTime.now()
        )
        historyRepository.save(history)

        outputView.printReceipt(receipt)
        cart.clear()
        outputView.printPurchaseComplete()
    }

    private fun viewPurchaseHistory(userId: String) {
        val histories = historyRepository.findRecentByUserId(userId, 10)

        if (histories.isEmpty()) {
            outputView.printNoPurchaseHistory()
            return
        }

        outputView.printPurchaseHistories(histories)

        if (inputView.askViewDetailHistory()) {
            val historyId = inputView.readHistoryId()
            val history = historyRepository.findById(historyId)

            if (history != null) {
                outputView.printDetailHistory(history)
            } else {
                outputView.printError("[ERROR] 구매 이력을 찾을 수 없습니다.")
            }
        }
    }

    private fun runAdminMode() {
        val admin = createOrLoginAdmin()

        do {
            try {
                processAdminAction(admin)
            } catch (e: IllegalArgumentException) {
                outputView.printError(e.message ?: "[ERROR] 잘못된 입력입니다.")
                continue
            }
        } while (askContinueOwnerMode())
    }

    private fun processAdminAction(owner: User.Admin) {
        val action = selectOwnerAction()

        when (action) {
            AdminAction.VIEW_INVENTORY -> viewInventory()
            AdminAction.ADD_PRODUCT -> addProduct()
            AdminAction.UPDATE_PRODUCT -> updateProduct()
            AdminAction.ADD_PROMOTION -> addPromotion()
            AdminAction.VIEW_SALES -> viewSalesStatistics()
            AdminAction.MANAGE_STOCK -> manageStock()
        }
    }

    private fun viewInventory() {
        outputView.printOwnerInventory(storeService.getAllProducts())

        if (inputView.askViewStatistics()) {
            outputView.printCategoryStatistics(storeService.getAllProducts())
        }
    }

    private fun addProduct() {
        val productInfo = inputView.readNewProductInfo()
        storeService.addNewProduct(productInfo)
        outputView.printProductAdded(productInfo.name)
    }

    private fun updateProduct() {
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

    private fun createOrLoginCustomer(): User.Customer {
        val loginType = inputView.askLoginOrGuest()

        return if (loginType == "1") {
            val phoneNumber = inputView.readPhoneNumber()
            User.Customer(
                id = UUID.randomUUID().toString(),
                name = "회원",
                phoneNumber = phoneNumber,
                isMember = true
            )
        } else {
            User.Customer(
                id = UUID.randomUUID().toString(),
                name = "손님",
                isMember = false
            )
        }
    }

    private fun createOrLoginAdmin(): User.Admin {
        val number = inputView.readAdminNumber()
        val password = inputView.readPassword()
        return User.Admin(
            id = UUID.randomUUID().toString(),
            number = number,
            password = password
        )
    }

    private fun selectCustomerAction(): CustomerAction {
        return try {
            outputView.printCustomerMenu()
            val input = inputView.readSelection()
            CustomerAction.select(input)
                ?: throw IllegalArgumentException("[ERROR] 잘못된 선택입니다.")
        } catch (e: IllegalArgumentException) {
            outputView.printError(e.message ?: "[ERROR] 잘못된 입력입니다.")
            selectCustomerAction()
        }
    }

    private fun selectOwnerAction(): AdminAction {
        return try {
            outputView.printOwnerMenu()
            val input = inputView.readSelection()
            AdminAction.select(input)
                ?: throw IllegalArgumentException("[ERROR] 잘못된 선택입니다.")
        } catch (e: IllegalArgumentException) {
            outputView.printError(e.message ?: "[ERROR] 잘못된 입력입니다.")
            selectOwnerAction()
        }
    }

    private fun viewProducts() {
        outputView.printWelcome()
        outputView.printProducts(storeService.getAllProducts())
    }

    private fun askContinueShopping(): Boolean {
        return try {
            inputView.askContinueShopping()
        } catch (e: IllegalArgumentException) {
            outputView.printError(e.message ?: "[ERROR] 잘못된 입력입니다.")
            askContinueShopping()
        }
    }

    private fun askContinueOwnerMode(): Boolean {
        return try {
            inputView.askContinueOwnerMode()
        } catch (e: IllegalArgumentException) {
            outputView.printError(e.message ?: "[ERROR] 잘못된 입력입니다.")
            askContinueOwnerMode()
        }
    }
}