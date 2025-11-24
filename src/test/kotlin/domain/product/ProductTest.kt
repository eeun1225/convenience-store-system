package domain.product

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import store.domain.product.Product
import store.domain.product.ProductCategory
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProductTest {

    @Test
    fun `상품 생성 시 카테고리가 자동으로 설정된다`() {
        val product = Product("콜라", 1000, 10)
        assertEquals(ProductCategory.BEVERAGE, product.category)
    }

    @Test
    fun `프로모션이 있는 상품인지 확인할 수 있다`() {
        val promotionProduct = Product("콜라", 1000, 10, "탄산2+1")
        val regularProduct = Product("물", 500, 20)

        assertTrue(promotionProduct.hasPromotion())
        assertFalse(regularProduct.hasPromotion())
    }

    @Test
    fun `요청 수량만큼 구매 가능한지 확인할 수 있다`() {
        val product = Product("콜라", 1000, 10)

        assertTrue(product.canPurchase(5))
        assertTrue(product.canPurchase(10))
        assertFalse(product.canPurchase(11))
    }

    @Test
    fun `재고를 감소시킬 수 있다`() {
        val product = Product("콜라", 1000, 10)
        val updatedProduct = product.decreaseQuantity(3)

        assertEquals(7, updatedProduct.quantity)
        assertEquals(10, product.quantity) // 원본은 변경되지 않음
    }

    @Test
    fun `재고보다 많이 감소시키면 예외가 발생한다`() {
        val product = Product("콜라", 1000, 10)

        assertThrows<IllegalArgumentException> {
            product.decreaseQuantity(11)
        }
    }

    @Test
    fun `특정 카테고리에 속하는지 확인할 수 있다`() {
        val product = Product("콜라", 1000, 10)

        assertTrue(product.isInCategory(ProductCategory.BEVERAGE))
        assertFalse(product.isInCategory(ProductCategory.SNACK))
    }
}