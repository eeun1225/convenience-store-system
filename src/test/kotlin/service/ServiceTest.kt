package service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import store.domain.product.Product
import store.domain.product.ProductInventory
import store.domain.promotion.Promotion
import store.domain.promotion.PromotionRepository
import store.domain.purchase.PurchaseItem
import store.service.InventoryManager
import store.service.PaymentService
import store.service.PromotionService
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PromotionServiceTest {
    private lateinit var promotionRepository: PromotionRepository
    private lateinit var promotionService: PromotionService

    @BeforeEach
    fun setUp() {
        promotionRepository = PromotionRepository()
        promotionService = PromotionService(promotionRepository)
    }

    @Test
    fun `활성화된 프로모션을 조회할 수 있다`() {
        val promotion = Promotion("탄산2+1", 2, 1, "2024-01-01", "2024-12-31")
        promotionRepository.save(promotion)

        val active = promotionService.getActivePromotion("탄산2+1", "2024-06-15")
        assertNotNull(active)
        assertEquals("탄산2+1", active.name)
    }

    @Test
    fun `비활성 프로모션은 null을 반환한다`() {
        val promotion = Promotion("탄산2+1", 2, 1, "2024-01-01", "2024-06-30")
        promotionRepository.save(promotion)

        val inactive = promotionService.getActivePromotion("탄산2+1", "2024-12-31")
        assertEquals(null, inactive)
    }

    @Test
    fun `프로모션 혜택을 계산할 수 있다`() {
        val promotion = Promotion("2+1", 2, 1, "2024-01-01", "2024-12-31")
        val result = promotionService.calculatePromotionBenefit(
            quantity = 7,
            promotion = promotion,
            promotionStock = 10
        )

        assertEquals(6, result.promotionQuantity) // 2세트 = 6개
        assertEquals(2, result.freeQuantity) // 2개 무료
        assertEquals(1, result.nonPromotionQuantity) // 1개 정가
    }

    @Test
    fun `프로모션 재고가 부족하면 적용 가능한 만큼만 계산된다`() {
        val promotion = Promotion("2+1", 2, 1, "2024-01-01", "2024-12-31")
        val result = promotionService.calculatePromotionBenefit(
            quantity = 7,
            promotion = promotion,
            promotionStock = 3 // 재고 3개 = 1세트만 가능
        )

        assertEquals(3, result.promotionQuantity)
        assertEquals(1, result.freeQuantity)
        assertEquals(4, result.nonPromotionQuantity)
    }
}

class PaymentServiceTest {
    private val paymentService = PaymentService()

    @Test
    fun `멤버십 할인이 적용된 영수증을 생성할 수 있다`() {
        val items = listOf(
            PurchaseItem("콜라", 3, 1000, promotionQuantity = 3, freeQuantity = 1),
            PurchaseItem("물", 2, 500)
        )

        val receipt = paymentService.createReceipt(items, applyMembership = true)

        // 멤버십 할인: 물 1000원의 30% = 300원
        assertEquals(300, receipt.membershipDiscount)
    }

    @Test
    fun `멤버십 할인이 없는 영수증을 생성할 수 있다`() {
        val items = listOf(
            PurchaseItem("콜라", 3, 1000)
        )

        val receipt = paymentService.createReceipt(items, applyMembership = false)
        assertEquals(0, receipt.membershipDiscount)
    }

    @Test
    fun `프로모션 적용 상품은 멤버십 할인에서 제외된다`() {
        val items = listOf(
            PurchaseItem("콜라", 6, 1000, promotionQuantity = 6, freeQuantity = 2),
            PurchaseItem("물", 3, 500) // 1500원의 30% = 450원
        )

        val receipt = paymentService.createReceipt(items, applyMembership = true)
        assertEquals(450, receipt.membershipDiscount)
    }
}

class InventoryManagerTest {
    private lateinit var inventory: ProductInventory
    private lateinit var inventoryManager: InventoryManager

    @BeforeEach
    fun setUp() {
        inventory = ProductInventory()
        inventoryManager = InventoryManager(inventory)
    }

    @Test
    fun `구매 후 재고를 업데이트할 수 있다`() {
        inventory.addProduct(Product("콜라", 1000, 10, "탄산2+1"))
        inventory.addProduct(Product("콜라", 1000, 10))

        val items = listOf(
            PurchaseItem("콜라", 5, 1000, promotionQuantity = 5, freeQuantity = 0)
        )

        inventoryManager.updateInventory(items)

        val promotionProduct = inventory.getPromotionProduct("콜라")
        assertEquals(5, promotionProduct?.quantity)
    }

    @Test
    fun `프로모션 재고가 부족하면 일반 재고에서 차감된다`() {
        inventory.addProduct(Product("콜라", 1000, 3, "탄산2+1"))
        inventory.addProduct(Product("콜라", 1000, 10))

        val items = listOf(
            PurchaseItem("콜라", 5, 1000, promotionQuantity = 3, freeQuantity = 1)
        )

        inventoryManager.updateInventory(items)

        val promotionProduct = inventory.getPromotionProduct("콜라")
        val regularProduct = inventory.getRegularProduct("콜라")

        assertEquals(0, promotionProduct?.quantity)
        assertEquals(8, regularProduct?.quantity) // 10 - 2 = 8
    }
}