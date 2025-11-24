package util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import store.util.InputValidator
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InputValidatorTest {

    @Test
    fun `Y 또는 N 입력을 검증할 수 있다`() {
        assertTrue(InputValidator.validateYesOrNo("Y"))
        assertTrue(InputValidator.validateYesOrNo("y"))
        assertFalse(InputValidator.validateYesOrNo("N"))
        assertFalse(InputValidator.validateYesOrNo("n"))
    }

    @Test
    fun `잘못된 Y_N 입력은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validateYesOrNo("YES")
        }

        assertThrows<IllegalArgumentException> {
            InputValidator.validateYesOrNo("1")
        }
    }

    @Test
    fun `양수를 파싱할 수 있다`() {
        assertEquals(10, InputValidator.parsePositiveInt("10", "수량"))
        assertEquals(1, InputValidator.parsePositiveInt("1", "가격"))
    }

    @Test
    fun `0 또는 음수는 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.parsePositiveInt("0", "수량")
        }

        assertThrows<IllegalArgumentException> {
            InputValidator.parsePositiveInt("-5", "수량")
        }
    }

    @Test
    fun `숫자가 아닌 입력은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.parsePositiveInt("abc", "수량")
        }
    }

    @Test
    fun `날짜 형식을 검증할 수 있다`() {
        InputValidator.validateDate("2024-01-01", "시작일")
        InputValidator.validateDate("2024-12-31", "종료일")
    }

    @Test
    fun `잘못된 날짜 형식은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validateDate("2024/01/01", "날짜")
        }

        assertThrows<IllegalArgumentException> {
            InputValidator.validateDate("20240101", "날짜")
        }
    }

    @Test
    fun `유효하지 않은 날짜 값은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validateDate("2024-13-01", "날짜") // 13월
        }

        assertThrows<IllegalArgumentException> {
            InputValidator.validateDate("2024-01-32", "날짜") // 32일
        }

        assertThrows<IllegalArgumentException> {
            InputValidator.validateDate("1899-01-01", "날짜") // 1900년 이전
        }
    }

    @Test
    fun `구매 입력 형식을 검증할 수 있다`() {
        val result = InputValidator.validatePurchaseInput("[콜라-2],[사이다-3]")

        assertEquals(2, result.size)
        assertEquals("콜라" to 2, result[0])
        assertEquals("사이다" to 3, result[1])
    }

    @Test
    fun `공백이 포함된 구매 입력도 처리할 수 있다`() {
        val result = InputValidator.validatePurchaseInput("[콜라-2], [사이다-3]")

        assertEquals(2, result.size)
        assertEquals("콜라" to 2, result[0])
        assertEquals("사이다" to 3, result[1])
    }

    @Test
    fun `잘못된 구매 입력 형식은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validatePurchaseInput("콜라-2")
        }

        assertThrows<IllegalArgumentException> {
            InputValidator.validatePurchaseInput("[콜라2]")
        }

        assertThrows<IllegalArgumentException> {
            InputValidator.validatePurchaseInput("[콜라-]")
        }
    }

    @Test
    fun `빈 입력은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validatePurchaseInput("")
        }

        assertThrows<IllegalArgumentException> {
            InputValidator.validatePurchaseInput("   ")
        }
    }

    @Test
    fun `상품명을 검증할 수 있다`() {
        InputValidator.validateProductName("콜라")
        InputValidator.validateProductName("사이다")
    }

    @Test
    fun `빈 상품명은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validateProductName("")
        }

        assertThrows<IllegalArgumentException> {
            InputValidator.validateProductName("   ")
        }
    }

    @Test
    fun `대괄호가 포함된 상품명은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validateProductName("[콜라]")
        }
    }

    @Test
    fun `수량을 검증할 수 있다`() {
        InputValidator.validateQuantity(1)
        InputValidator.validateQuantity(100)
    }

    @Test
    fun `0 이하의 수량은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validateQuantity(0)
        }

        assertThrows<IllegalArgumentException> {
            InputValidator.validateQuantity(-1)
        }
    }

    @Test
    fun `재고를 검증할 수 있다`() {
        InputValidator.validateStock(5, 10, "콜라")
    }

    @Test
    fun `재고 초과 구매는 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validateStock(15, 10, "콜라")
        }
    }

    @Test
    fun `상품 존재 여부를 검증할 수 있다`() {
        InputValidator.validateProductExists(true, "콜라")
    }

    @Test
    fun `존재하지 않는 상품은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validateProductExists(false, "없는상품")
        }
    }

    @Test
    fun `가격을 검증할 수 있다`() {
        InputValidator.validatePrice(1000)
    }

    @Test
    fun `0 이하의 가격은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validatePrice(0)
        }

        assertThrows<IllegalArgumentException> {
            InputValidator.validatePrice(-1000)
        }
    }

    @Test
    fun `프로모션 값을 검증할 수 있다`() {
        InputValidator.validatePromotionValues(2, 1)
    }

    @Test
    fun `0 이하의 프로모션 값은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validatePromotionValues(0, 1)
        }

        assertThrows<IllegalArgumentException> {
            InputValidator.validatePromotionValues(2, 0)
        }
    }
}