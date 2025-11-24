package domain.purchase

import org.junit.jupiter.api.Test
import store.domain.purchase.MembershipDiscount
import store.domain.purchase.PurchaseItem
import store.domain.purchase.Receipt
import kotlin.test.assertEquals

class ReceiptTest {

    @Test
    fun `전체 구매 수량을 계산할 수 있다`() {
        val items = listOf(
            PurchaseItem("콜라", 3, 1000),
            PurchaseItem("사이다", 2, 1000)
        )
        val receipt = Receipt(items, 0)

        assertEquals(5, receipt.getTotalQuantity())
    }

    @Test
    fun `전체 구매 금액을 계산할 수 있다`() {
        val items = listOf(
            PurchaseItem("콜라", 3, 1000),
            PurchaseItem("사이다", 2, 1000)
        )
        val receipt = Receipt(items, 0)

        assertEquals(5000, receipt.getTotalAmount())
    }

    @Test
    fun `프로모션 할인 금액을 계산할 수 있다`() {
        val items = listOf(
            PurchaseItem("콜라", 3, 1000, promotionQuantity = 3, freeQuantity = 1),
            PurchaseItem("사이다", 2, 1000)
        )
        val receipt = Receipt(items, 0)

        assertEquals(1000, receipt.getPromotionDiscount())
    }

    @Test
    fun `최종 결제 금액을 계산할 수 있다`() {
        val items = listOf(
            PurchaseItem("콜라", 3, 1000, promotionQuantity = 3, freeQuantity = 1),
            PurchaseItem("사이다", 2, 1000)
        )
        val membershipDiscount = 600
        val receipt = Receipt(items, membershipDiscount)

        // 5000 - 1000(프로모션) - 600(멤버십) = 3400
        assertEquals(3400, receipt.getFinalAmount())
    }

    @Test
    fun `무료 증정 상품 목록을 조회할 수 있다`() {
        val items = listOf(
            PurchaseItem("콜라", 3, 1000, promotionQuantity = 3, freeQuantity = 1),
            PurchaseItem("사이다", 3, 1000, promotionQuantity = 3, freeQuantity = 1),
            PurchaseItem("물", 1, 500)
        )
        val receipt = Receipt(items, 0)
        val freeItems = receipt.getFreeItems()

        assertEquals(2, freeItems.size)
        assertEquals("콜라" to 1, freeItems[0])
        assertEquals("사이다" to 1, freeItems[1])
    }
}

class PurchaseItemTest {

    @Test
    fun `총 구매 금액을 계산할 수 있다`() {
        val item = PurchaseItem("콜라", 3, 1000)
        assertEquals(3000, item.getTotalAmount())
    }

    @Test
    fun `프로모션 할인 금액을 계산할 수 있다`() {
        val item = PurchaseItem("콜라", 3, 1000, promotionQuantity = 3, freeQuantity = 1)
        assertEquals(1000, item.getPromotionDiscount())
    }

    @Test
    fun `프로모션 미적용 수량을 계산할 수 있다`() {
        val item = PurchaseItem("콜라", 5, 1000, promotionQuantity = 3, freeQuantity = 1)
        assertEquals(2, item.getNonPromotionQuantity())
    }

    @Test
    fun `프로모션이 없는 경우 전체가 미적용 수량이다`() {
        val item = PurchaseItem("물", 3, 500)
        assertEquals(3, item.getNonPromotionQuantity())
    }
}

class MembershipDiscountTest {

    @Test
    fun `멤버십 할인을 계산할 수 있다`() {
        val discount = MembershipDiscount.calculate(10000)
        assertEquals(3000, discount) // 30%
    }

    @Test
    fun `멤버십 할인은 최대 8000원이다`() {
        val discount = MembershipDiscount.calculate(30000)
        assertEquals(8000, discount) // 9000원이지만 최대 8000원
    }

    @Test
    fun `0원에 대한 멤버십 할인은 0원이다`() {
        val discount = MembershipDiscount.calculate(0)
        assertEquals(0, discount)
    }
}