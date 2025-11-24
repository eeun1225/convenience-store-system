package domain.promotion

import org.junit.jupiter.api.Test
import store.domain.promotion.Promotion
import store.domain.promotion.PromotionType
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PromotionTest {

    @Test
    fun `프로모션 활성 기간을 확인할 수 있다`() {
        val promotion = Promotion("탄산2+1", 2, 1, "2024-01-01", "2024-12-31")

        assertTrue(promotion.isActive("2024-06-15"))
        assertTrue(promotion.isActive("2024-01-01"))
        assertTrue(promotion.isActive("2024-12-31"))
        assertFalse(promotion.isActive("2023-12-31"))
        assertFalse(promotion.isActive("2025-01-01"))
    }

    @Test
    fun `프로모션 필요 수량을 계산할 수 있다`() {
        val onePlusOne = Promotion("1+1", 1, 1, "2024-01-01", "2024-12-31")
        val twoPlusOne = Promotion("2+1", 2, 1, "2024-01-01", "2024-12-31")

        assertEquals(2, onePlusOne.getRequiredQuantity())
        assertEquals(3, twoPlusOne.getRequiredQuantity())
    }

    @Test
    fun `추가 무료 증정이 가능한지 확인할 수 있다`() {
        val twoPlusOne = Promotion("2+1", 2, 1, "2024-01-01", "2024-12-31")

        assertTrue(twoPlusOne.canGetMoreFree(2))  // 2개 구매 시 1개 더 가능
        assertFalse(twoPlusOne.canGetMoreFree(3)) // 3개 구매 시 이미 세트 완성
        assertTrue(twoPlusOne.canGetMoreFree(5))  // 5개 구매 시 1개 더 가능
        assertFalse(twoPlusOne.canGetMoreFree(6)) // 6개 구매 시 이미 세트 완성
    }
}

class PromotionTypeTest {

    @Test
    fun `buy와 get으로 프로모션 타입을 찾을 수 있다`() {
        assertEquals(PromotionType.ONE_PLUS_ONE, PromotionType.fromBuyGet(1, 1))
        assertEquals(PromotionType.TWO_PLUS_ONE, PromotionType.fromBuyGet(2, 1))
        assertEquals(PromotionType.TWO_PLUS_TWO, PromotionType.fromBuyGet(2, 2))
    }

    @Test
    fun `displayName으로 프로모션 타입을 찾을 수 있다`() {
        assertEquals(PromotionType.ONE_PLUS_ONE, PromotionType.fromDisplayName("1+1"))
        assertEquals(PromotionType.TWO_PLUS_ONE, PromotionType.fromDisplayName("2+1"))
    }

    @Test
    fun `구매 수량에 따른 무료 수량을 계산할 수 있다`() {
        val onePlusOne = PromotionType.ONE_PLUS_ONE
        assertEquals(0, onePlusOne.calculateFreeQuantity(1))
        assertEquals(1, onePlusOne.calculateFreeQuantity(2))
        assertEquals(2, onePlusOne.calculateFreeQuantity(4))

        val twoPlusOne = PromotionType.TWO_PLUS_ONE
        assertEquals(0, twoPlusOne.calculateFreeQuantity(2))
        assertEquals(1, twoPlusOne.calculateFreeQuantity(3))
        assertEquals(2, twoPlusOne.calculateFreeQuantity(6))
    }

    @Test
    fun `프로모션 완성을 위한 추가 수량을 계산할 수 있다`() {
        val twoPlusOne = PromotionType.TWO_PLUS_ONE

        assertEquals(1, twoPlusOne.getAdditionalQuantityForPromotion(2))
        assertEquals(0, twoPlusOne.getAdditionalQuantityForPromotion(3))
        assertEquals(1, twoPlusOne.getAdditionalQuantityForPromotion(5))
        assertEquals(0, twoPlusOne.getAdditionalQuantityForPromotion(6))
    }
}