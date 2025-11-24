package domain.product

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import store.domain.product.Product
import store.domain.product.ProductCategory
import store.domain.product.ProductInventory
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ProductInventoryTest {
    private lateinit var inventory: ProductInventory

    @BeforeEach
    fun setUp() {
        inventory = ProductInventory()
    }

    @Test
    fun `상품을 추가할 수 있다`() {
        val product = Product("콜라", 1000, 10)
        inventory.addProduct(product)

        val found = inventory.findByName("콜라")
        assertEquals(1, found.size)
        assertEquals("콜라", found[0].name)
    }

    @Test
    fun `같은 이름의 상품을 여러 개 추가할 수 있다`() {
        val promotionProduct = Product("콜라", 1000, 10, "탄산2+1")
        val regularProduct = Product("콜라", 1000, 10)

        inventory.addProduct(promotionProduct)
        inventory.addProduct(regularProduct)

        val found = inventory.findByName("콜라")
        assertEquals(2, found.size)
    }

    @Test
    fun `카테고리별로 상품을 조회할 수 있다`() {
        inventory.addProduct(Product("콜라", 1000, 10))
        inventory.addProduct(Product("사이다", 1000, 10))
        inventory.addProduct(Product("감자칩", 1500, 5))

        val beverages = inventory.findByCategory(ProductCategory.BEVERAGE)
        assertEquals(2, beverages.size)

        val snacks = inventory.findByCategory(ProductCategory.SNACK)
        assertEquals(1, snacks.size)
    }

    @Test
    fun `프로모션 상품을 조회할 수 있다`() {
        val promotionProduct = Product("콜라", 1000, 10, "탄산2+1")
        val regularProduct = Product("콜라", 1000, 10)

        inventory.addProduct(promotionProduct)
        inventory.addProduct(regularProduct)

        val found = inventory.getPromotionProduct("콜라")
        assertNotNull(found)
        assertEquals("탄산2+1", found.promotion)
    }

    @Test
    fun `일반 상품을 조회할 수 있다`() {
        val promotionProduct = Product("콜라", 1000, 10, "탄산2+1")
        val regularProduct = Product("콜라", 1000, 10)

        inventory.addProduct(promotionProduct)
        inventory.addProduct(regularProduct)

        val found = inventory.getRegularProduct("콜라")
        assertNotNull(found)
        assertNull(found.promotion)
    }

    @Test
    fun `전체 재고 수량을 조회할 수 있다`() {
        inventory.addProduct(Product("콜라", 1000, 10, "탄산2+1"))
        inventory.addProduct(Product("콜라", 1000, 15))

        val total = inventory.getTotalQuantity("콜라")
        assertEquals(25, total)
    }

    @Test
    fun `카테고리별 전체 재고 수량을 조회할 수 있다`() {
        inventory.addProduct(Product("콜라", 1000, 10))
        inventory.addProduct(Product("사이다", 1000, 15))
        inventory.addProduct(Product("감자칩", 1500, 5))

        val beverageTotal = inventory.getTotalQuantityByCategory(ProductCategory.BEVERAGE)
        assertEquals(25, beverageTotal)
    }

    @Test
    fun `상품 정보를 업데이트할 수 있다`() {
        val product = Product("콜라", 1000, 10)
        inventory.addProduct(product)

        val updatedProduct = product.copy(quantity = 20)
        inventory.updateProduct("콜라", false, updatedProduct)

        val found = inventory.getRegularProduct("콜라")
        assertEquals(20, found?.quantity)
    }

    @Test
    fun `모든 상품을 이름순으로 조회할 수 있다`() {
        inventory.addProduct(Product("콜라", 1000, 10))
        inventory.addProduct(Product("감자칩", 1500, 5))
        inventory.addProduct(Product("사이다", 1000, 10))

        val all = inventory.getAllProducts()
        assertEquals("감자칩", all[0].name)
        assertEquals("사이다", all[1].name)
        assertEquals("콜라", all[2].name)
    }

    @Test
    fun `등록된 카테고리 목록을 조회할 수 있다`() {
        inventory.addProduct(Product("콜라", 1000, 10))
        inventory.addProduct(Product("감자칩", 1500, 5))

        val categories = inventory.getCategories()
        assertEquals(2, categories.size)
        assert(categories.contains(ProductCategory.BEVERAGE))
        assert(categories.contains(ProductCategory.SNACK))
    }
}