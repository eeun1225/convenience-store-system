package domain

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import store.domain.product.Product
import store.domain.product.ProductRepository
import store.domain.promotion.Promotion
import store.domain.promotion.PromotionRepository
import store.domain.purchase.PurchaseHistory
import store.domain.purchase.PurchaseHistoryRepository
import store.domain.purchase.PurchaseItem
import store.domain.purchase.Receipt
import store.domain.user.User
import store.domain.user.UserRepository
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProductRepositoryTest {
    private lateinit var repository: ProductRepository

    @BeforeEach
    fun setUp() {
        repository = ProductRepository()
    }

    @Test
    fun `상품을 저장할 수 있다`() {
        val product = Product("콜라", 1000, 10)
        repository.save(product)

        val found = repository.findByName("콜라")
        assertEquals(1, found.size)
    }

    @Test
    fun `여러 상품을 한 번에 저장할 수 있다`() {
        val products = listOf(
            Product("콜라", 1000, 10),
            Product("사이다", 1000, 10)
        )
        repository.saveAll(products)

        val all = repository.findAll()
        assertEquals(2, all.size)
    }

    @Test
    fun `프로모션 상품을 조회할 수 있다`() {
        repository.save(Product("콜라", 1000, 10, "탄산2+1"))
        repository.save(Product("콜라", 1000, 10))

        val promo = repository.findPromotionProduct("콜라")
        assertNotNull(promo)
        assertEquals("탄산2+1", promo.promotion)
    }

    @Test
    fun `전체 재고를 조회할 수 있다`() {
        repository.save(Product("콜라", 1000, 10, "탄산2+1"))
        repository.save(Product("콜라", 1000, 10))

        assertEquals(20, repository.getTotalStock("콜라"))
    }

    @Test
    fun `상품을 업데이트할 수 있다`() {
        val product = Product("콜라", 1000, 10)
        repository.save(product)

        val updated = product.copy(quantity = 20)
        repository.update("콜라", false, updated)

        val found = repository.findByName("콜라")[0]
        assertEquals(20, found.quantity)
    }

    @Test
    fun `상품 존재 여부를 확인할 수 있다`() {
        repository.save(Product("콜라", 1000, 10))

        assertTrue(repository.exists("콜라"))
        assertFalse(repository.exists("사이다"))
    }
}

class PromotionRepositoryTest {
    private lateinit var repository: PromotionRepository

    @BeforeEach
    fun setUp() {
        repository = PromotionRepository()
    }

    @Test
    fun `프로모션을 저장할 수 있다`() {
        val promotion = Promotion("탄산2+1", 2, 1, "2024-01-01", "2024-12-31")
        repository.save(promotion)

        val found = repository.findByName("탄산2+1")
        assertNotNull(found)
        assertEquals("탄산2+1", found.name)
    }

    @Test
    fun `여러 프로모션을 한 번에 저장할 수 있다`() {
        val promotions = listOf(
            Promotion("탄산2+1", 2, 1, "2024-01-01", "2024-12-31"),
            Promotion("MD추천상품", 1, 1, "2024-01-01", "2024-12-31")
        )
        repository.saveAll(promotions)

        val all = repository.findAll()
        assertEquals(2, all.size)
    }

    @Test
    fun `존재하지 않는 프로모션은 null을 반환한다`() {
        val found = repository.findByName("없는프로모션")
        assertNull(found)
    }

    @Test
    fun `프로모션 존재 여부를 확인할 수 있다`() {
        repository.save(Promotion("탄산2+1", 2, 1, "2024-01-01", "2024-12-31"))

        assertTrue(repository.exists("탄산2+1"))
        assertFalse(repository.exists("없는프로모션"))
    }
}

class UserRepositoryTest {
    private lateinit var repository: UserRepository

    @BeforeEach
    fun setUp() {
        repository = UserRepository()
    }

    @Test
    fun `고객을 저장할 수 있다`() {
        val customer = User.Customer.createMember("홍길동", "010-1234-5678", "Password123!")
        repository.saveCustomer(customer)

        val found = repository.findCustomerByPhoneNumber("010-1234-5678")
        assertNotNull(found)
        assertEquals("홍길동", found.name)
    }

    @Test
    fun `전화번호로 고객 존재 여부를 확인할 수 있다`() {
        val customer = User.Customer.createMember("홍길동", "010-1234-5678", "Password123!")
        repository.saveCustomer(customer)

        assertTrue(repository.existsByPhoneNumber("010-1234-5678"))
        assertFalse(repository.existsByPhoneNumber("010-9999-9999"))
    }

    @Test
    fun `모든 고객을 조회할 수 있다`() {
        repository.saveCustomer(User.Customer.createMember("홍길동", "010-1234-5678", "Password123!"))
        repository.saveCustomer(User.Customer.createMember("김철수", "010-9999-9999", "Password123!"))

        val all = repository.getAllCustomers()
        assertEquals(2, all.size)
    }
}

class PurchaseHistoryRepositoryTest {
    private lateinit var repository: PurchaseHistoryRepository

    @BeforeEach
    fun setUp() {
        repository = PurchaseHistoryRepository()
    }

    @Test
    fun `구매 이력을 저장할 수 있다`() {
        val history = createPurchaseHistory("user1")
        repository.save(history)

        val found = repository.findById(history.id)
        assertNotNull(found)
        assertEquals("user1", found.userId)
    }

    @Test
    fun `사용자별 구매 이력을 조회할 수 있다`() {
        repository.save(createPurchaseHistory("user1"))
        repository.save(createPurchaseHistory("user1"))
        repository.save(createPurchaseHistory("user2"))

        val user1Histories = repository.findByUserId("user1")
        assertEquals(2, user1Histories.size)
    }

    @Test
    fun `최근 구매 이력을 제한된 개수만큼 조회할 수 있다`() {
        repeat(15) {
            repository.save(createPurchaseHistory("user1"))
        }

        val recent = repository.findRecentByUserId("user1", 10)
        assertEquals(10, recent.size)
    }

    @Test
    fun `총 구매 금액을 계산할 수 있다`() {
        repository.save(createPurchaseHistory("user1", 5000))
        repository.save(createPurchaseHistory("user1", 3000))

        val total = repository.getTotalPurchaseAmount("user1")
        assertEquals(8000, total)
    }

    @Test
    fun `총 구매 횟수를 조회할 수 있다`() {
        repository.save(createPurchaseHistory("user1"))
        repository.save(createPurchaseHistory("user1"))
        repository.save(createPurchaseHistory("user1"))

        val count = repository.getTotalPurchaseCount("user1")
        assertEquals(3, count)
    }

    @Test
    fun `날짜 범위로 구매 이력을 조회할 수 있다`() {
        val now = LocalDateTime.now()
        val history1 = createPurchaseHistory("user1").copy(purchasedAt = now.minusDays(5))
        val history2 = createPurchaseHistory("user1").copy(purchasedAt = now.minusDays(3))
        val history3 = createPurchaseHistory("user1").copy(purchasedAt = now.minusDays(10))

        repository.save(history1)
        repository.save(history2)
        repository.save(history3)

        val inRange = repository.findByDateRange(
            "user1",
            now.minusDays(7),
            now
        )

        assertEquals(2, inRange.size)
    }

    private fun createPurchaseHistory(userId: String, amount: Int = 5000): PurchaseHistory {
        val items = listOf(PurchaseItem("콜라", 2, 1000))
        val receipt = Receipt(items, 0)
        return PurchaseHistory(
            id = java.util.UUID.randomUUID().toString(),
            userId = userId,
            items = items,
            receipt = receipt.copy(membershipDiscount = 5000 - amount)
        )
    }
}