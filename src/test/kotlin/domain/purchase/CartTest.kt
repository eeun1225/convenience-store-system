package domain.purchase

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import store.domain.product.Product
import store.domain.purchase.Cart
import store.domain.purchase.CartItem
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CartTest {
    private lateinit var cart: Cart

    @BeforeEach
    fun setUp() {
        cart = Cart("user123")
    }

    @Test
    fun `장바구니에 상품을 추가할 수 있다`() {
        val product = Product("콜라", 1000, 10)
        cart.addItem(product, 2)

        assertEquals(1, cart.getAllItems().size)
        assertEquals(2, cart.getItem("콜라")?.quantity)
    }

    @Test
    fun `같은 상품을 추가하면 수량이 증가한다`() {
        val product = Product("콜라", 1000, 10)
        cart.addItem(product, 2)
        cart.addItem(product, 3)

        assertEquals(1, cart.getAllItems().size)
        assertEquals(5, cart.getItem("콜라")?.quantity)
    }

    @Test
    fun `장바구니에서 상품을 제거할 수 있다`() {
        val product = Product("콜라", 1000, 10)
        cart.addItem(product, 2)
        cart.removeItem("콜라")

        assertTrue(cart.isEmpty())
    }

    @Test
    fun `상품 수량을 변경할 수 있다`() {
        val product = Product("콜라", 1000, 10)
        cart.addItem(product, 2)
        cart.updateQuantity("콜라", 5)

        assertEquals(5, cart.getItem("콜라")?.quantity)
    }

    @Test
    fun `존재하지 않는 상품의 수량을 변경하면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            cart.updateQuantity("없는상품", 5)
        }
    }

    @Test
    fun `전체 상품 개수를 조회할 수 있다`() {
        cart.addItem(Product("콜라", 1000, 10), 2)
        cart.addItem(Product("사이다", 1000, 10), 3)

        assertEquals(5, cart.getTotalItemCount())
    }

    @Test
    fun `전체 가격을 조회할 수 있다`() {
        cart.addItem(Product("콜라", 1000, 10), 2)
        cart.addItem(Product("사이다", 1000, 10), 3)

        assertEquals(5000, cart.getTotalPrice())
    }

    @Test
    fun `장바구니를 비울 수 있다`() {
        cart.addItem(Product("콜라", 1000, 10), 2)
        cart.addItem(Product("사이다", 1000, 10), 3)
        cart.clear()

        assertTrue(cart.isEmpty())
        assertEquals(0, cart.getTotalItemCount())
    }

    @Test
    fun `빈 장바구니인지 확인할 수 있다`() {
        assertTrue(cart.isEmpty())

        cart.addItem(Product("콜라", 1000, 10), 2)
        assertFalse(cart.isEmpty())
    }
}

class CartItemTest {

    @Test
    fun `CartItem 생성 시 수량은 1개 이상이어야 한다`() {
        val product = Product("콜라", 1000, 10)

        assertThrows<IllegalArgumentException> {
            CartItem(product, 0)
        }

        assertThrows<IllegalArgumentException> {
            CartItem(product, -1)
        }
    }

    @Test
    fun `총 가격을 계산할 수 있다`() {
        val product = Product("콜라", 1000, 10)
        val cartItem = CartItem(product, 3)

        assertEquals(3000, cartItem.getTotalPrice())
    }

    @Test
    fun `수량을 증가시킬 수 있다`() {
        val product = Product("콜라", 1000, 10)
        val cartItem = CartItem(product, 2)
        val increased = cartItem.increaseQuantity(3)

        assertEquals(5, increased.quantity)
        assertEquals(2, cartItem.quantity) // 원본은 변경되지 않음
    }

    @Test
    fun `수량을 감소시킬 수 있다`() {
        val product = Product("콜라", 1000, 10)
        val cartItem = CartItem(product, 5)
        val decreased = cartItem.decreaseQuantity(2)

        assertEquals(3, decreased.quantity)
    }

    @Test
    fun `수량을 1개 미만으로 감소시키면 예외가 발생한다`() {
        val product = Product("콜라", 1000, 10)
        val cartItem = CartItem(product, 2)

        assertThrows<IllegalArgumentException> {
            cartItem.decreaseQuantity(2)
        }
    }
}