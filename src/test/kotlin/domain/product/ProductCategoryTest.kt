package domain.product

import org.junit.jupiter.api.Test
import store.domain.product.ProductCategory
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProductCategoryTest {

    @Test
    fun `displayName으로 카테고리를 찾을 수 있다`() {
        assertEquals(ProductCategory.BEVERAGE, ProductCategory.fromDisplayName("음료"))
        assertEquals(ProductCategory.SNACK, ProductCategory.fromDisplayName("스낵"))
        assertNull(ProductCategory.fromDisplayName("없는카테고리"))
    }

    @Test
    fun `description으로 카테고리를 찾을 수 있다`() {
        assertEquals(ProductCategory.BEVERAGE, ProductCategory.fromDescription("음료수 및 음료 제품"))
        assertEquals(ProductCategory.SNACK, ProductCategory.fromDescription("과자 및 간식류"))
    }

    @Test
    fun `상품명으로 카테고리를 추론할 수 있다`() {
        assertEquals(ProductCategory.BEVERAGE, ProductCategory.fromProductName("코카콜라"))
        assertEquals(ProductCategory.BEVERAGE, ProductCategory.fromProductName("물"))
        assertEquals(ProductCategory.SNACK, ProductCategory.fromProductName("포테이토칩"))
        assertEquals(ProductCategory.INSTANT, ProductCategory.fromProductName("신라면"))
        assertEquals(ProductCategory.FOOD, ProductCategory.fromProductName("김밥"))
        assertEquals(ProductCategory.HEALTH, ProductCategory.fromProductName("비타민C"))
        assertEquals(ProductCategory.ETC, ProductCategory.fromProductName("알수없는상품"))
    }

    @Test
    fun `모든 카테고리 목록을 가져올 수 있다`() {
        val categories = ProductCategory.getAllCategories()
        assertEquals(7, categories.size)
        assert(categories.contains(ProductCategory.BEVERAGE))
        assert(categories.contains(ProductCategory.ETC))
    }
}