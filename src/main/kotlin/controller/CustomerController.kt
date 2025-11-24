package store.controller

import store.domain.purchase.Cart
import store.domain.purchase.PurchaseHistory
import store.domain.purchase.PurchaseHistoryRepository
import store.domain.user.User
import store.domain.user.UserRepository
import store.service.PaymentService
import store.service.StoreService
import store.view.CustomerOutput
import store.view.InputView
import store.view.OutputView
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class CustomerController(
    private val storeService: StoreService,
    private val paymentService: PaymentService,
    private val inputView: InputView,
    private val outputView: OutputView,
    private val customerOutput: CustomerOutput,
    private val userRepository: UserRepository,
    private val historyRepository: PurchaseHistoryRepository
) {
    private val carts = mutableMapOf<String, Cart>()

    fun run() {
        val customer = createOrLoginCustomer()
        val cart = carts.getOrPut(customer.id) { Cart(customer.id) }

        var shouldExit = false
        while (!shouldExit) {
            try {
                val action = selectAction()
                shouldExit = processAction(action, customer, cart)
            } catch (e: IllegalArgumentException) {
                outputView.printError("[ERROR] ${e.message}")
            }
        }
    }

    private fun createOrLoginCustomer(): User.Customer {
        return try {
            customerOutput.showLoginMenu()
            when (val loginType = inputView.readSelection()) {
                "1" -> signUpCustomer()
                "2" -> loginCustomer()
                "3" -> User.Customer.createGuest()
                else -> throw IllegalArgumentException("잘못된 선택입니다.")
            }
        } catch (e: IllegalArgumentException) {
            outputView.printError("[ERROR] ${e.message}")
            createOrLoginCustomer()
        }
    }

    private fun signUpCustomer(): User.Customer {
        customerOutput.showSignUpForm()

        customerOutput.askName()
        val name = inputView.readName()

        customerOutput.askPhoneNumber()
        val phoneNumber = inputView.readPhoneNumber()

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw IllegalArgumentException("이미 가입된 휴대폰 번호입니다.")
        }

        customerOutput.askPassword()
        val password = inputView.readPassword()

        customerOutput.askPasswordConfirm()
        val passwordConfirm = inputView.readPasswordConfirm()

        if (password != passwordConfirm) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
        }

        val customer = User.Customer.createMember(name, phoneNumber, password)
        userRepository.saveCustomer(customer)

        customerOutput.showSignUpSuccess(name)
        return customer
    }

    private fun loginCustomer(): User.Customer {
        customerOutput.showLoginForm()

        customerOutput.askPhoneNumber()
        val phoneNumber = inputView.readPhoneNumber()

        val customer = userRepository.findCustomerByPhoneNumber(phoneNumber)
            ?: throw IllegalArgumentException("가입되지 않은 휴대폰 번호입니다.")

        customerOutput.askPassword()
        val password = inputView.readPassword()

        if (!customer.matchesPassword(password)) {
            customerOutput.showLoginFailed()
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
        }

        customerOutput.showLoginSuccess(customer.name)
        return customer
    }

    private fun selectAction(): CustomerAction {
        return try {
            outputView.printCustomerMenu()
            val input = inputView.readSelection()
            CustomerAction.select(input)
                ?: throw IllegalArgumentException("잘못된 선택입니다.")
        } catch (e: IllegalArgumentException) {
            outputView.printError("[ERROR] ${e.message}")
            selectAction()
        }
    }

    private fun processAction(action: CustomerAction, customer: User.Customer, cart: Cart): Boolean {
        return when (action) {
            CustomerAction.VIEW_PRODUCTS -> {
                viewProducts()
                false
            }
            CustomerAction.ADD_TO_CART -> {
                addToCart(cart)
                false
            }
            CustomerAction.VIEW_CART -> {
                viewCart(cart)
                false
            }
            CustomerAction.CHECKOUT -> {
                checkout(customer, cart)
                askContinueShopping()
            }
            CustomerAction.VIEW_HISTORY -> {
                viewPurchaseHistory(customer.id)
                false
            }
        }
    }

    private fun viewProducts() {
        outputView.printWelcome()
        outputView.printProducts(storeService.getAllProducts())
    }

    private fun addToCart(cart: Cart) {
        outputView.printWelcome()
        outputView.printProducts(storeService.getAllProducts())

        customerOutput.showPurchaseInputGuide()
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

        customerOutput.askModifyCart()
        if (inputView.readYesOrNo()) {
            modifyCart(cart)
        }
    }

    private fun modifyCart(cart: Cart) {
        outputView.printCart(cart)
        customerOutput.showCartModifyMenu()
        val action = inputView.readSelection()

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

        customerOutput.askConfirmCheckout()
        if (!inputView.readYesOrNo()) return

        val currentDate = LocalDate.now().toString()
        val cartItems = cart.getAllItems().map { it.product.name to it.quantity }

        val purchaseItems = storeService.processPurchase(cartItems, currentDate)
        val applyMembership = shouldApplyMembership(customer)
        val receipt = paymentService.createReceipt(purchaseItems, applyMembership)

        storeService.updateInventory(purchaseItems)
        savePurchaseHistory(customer.id, purchaseItems, receipt)

        outputView.printReceipt(receipt)
        cart.clear()
        outputView.printPurchaseComplete()
    }

    private fun shouldApplyMembership(customer: User.Customer): Boolean {
        if (!customer.isMember) return false

        customerOutput.askMembershipDiscount()
        return inputView.readYesOrNo()
    }

    private fun savePurchaseHistory(userId: String, purchaseItems: List<store.domain.purchase.PurchaseItem>, receipt: store.domain.purchase.Receipt) {
        val history = PurchaseHistory(
            id = UUID.randomUUID().toString(),
            userId = userId,
            items = purchaseItems,
            receipt = receipt,
            purchasedAt = LocalDateTime.now()
        )
        historyRepository.save(history)
    }

    private fun viewPurchaseHistory(userId: String) {
        val histories = historyRepository.findRecentByUserId(userId, 10)

        if (histories.isEmpty()) {
            outputView.printNoPurchaseHistory()
            return
        }

        outputView.printPurchaseHistories(histories)

        customerOutput.askViewDetailHistory()
        if (!inputView.readYesOrNo()) return

        showDetailHistory()
    }

    private fun showDetailHistory() {
        customerOutput.askHistoryId()
        val id = inputView.readHistoryId()
        val history = historyRepository.findById(id)

        if (history != null) {
            outputView.printDetailHistory(history)
        } else {
            outputView.printError("[ERROR] 구매 이력이 존재하지 않습니다.")
        }
    }

    private fun askContinueShopping(): Boolean {
        customerOutput.askContinueShopping()
        return !inputView.readYesOrNo()
    }
}