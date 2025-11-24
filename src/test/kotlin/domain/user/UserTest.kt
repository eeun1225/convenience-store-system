package domain.user

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import store.domain.user.User
import store.domain.user.UserMode
import store.domain.user.UserValidator
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserTest {

    @Test
    fun `회원을 생성할 수 있다`() {
        val customer = User.Customer.createMember(
            "홍길동",
            "010-1234-5678",
            "Password123!"
        )

        assertEquals("홍길동", customer.name)
        assertEquals("010-1234-5678", customer.phoneNumber)
        assertTrue(customer.isMember)
    }

    @Test
    fun `잘못된 전화번호 형식으로 회원을 생성하면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            User.Customer.createMember("홍길동", "01012345678", "Password123!")
        }

        assertThrows<IllegalArgumentException> {
            User.Customer.createMember("홍길동", "010-123-4567", "Password123!")
        }
    }

    @Test
    fun `잘못된 비밀번호로 회원을 생성하면 예외가 발생한다`() {
        // 8자 미만
        assertThrows<IllegalArgumentException> {
            User.Customer.createMember("홍길동", "010-1234-5678", "Pass1!")
        }

        // 숫자 없음
        assertThrows<IllegalArgumentException> {
            User.Customer.createMember("홍길동", "010-1234-5678", "Password!")
        }

        // 영문 없음
        assertThrows<IllegalArgumentException> {
            User.Customer.createMember("홍길동", "010-1234-5678", "12345678!")
        }

        // 특수문자 없음
        assertThrows<IllegalArgumentException> {
            User.Customer.createMember("홍길동", "010-1234-5678", "Password123")
        }
    }

    @Test
    fun `비회원을 생성할 수 있다`() {
        val guest = User.Customer.createGuest()

        assertEquals("비회원", guest.name)
        assertFalse(guest.isMember)
    }

    @Test
    fun `비밀번호 일치 여부를 확인할 수 있다`() {
        val customer = User.Customer.createMember(
            "홍길동",
            "010-1234-5678",
            "Password123!"
        )

        assertTrue(customer.matchesPassword("Password123!"))
        assertFalse(customer.matchesPassword("WrongPassword!"))
    }
}

class UserValidatorTest {

    @Test
    fun `올바른 전화번호 형식을 검증할 수 있다`() {
        UserValidator.validatePhoneNumber("010-1234-5678")
        UserValidator.validatePhoneNumber("011-1234-5678")
    }

    @Test
    fun `잘못된 전화번호 형식은 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            UserValidator.validatePhoneNumber("01012345678")
        }

        assertThrows<IllegalArgumentException> {
            UserValidator.validatePhoneNumber("010-123-4567")
        }
    }

    @Test
    fun `올바른 비밀번호를 검증할 수 있다`() {
        UserValidator.validatePassword("Password123!")
        UserValidator.validatePassword("Abcd1234@")
    }

    @Test
    fun `8자 미만 비밀번호는 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            UserValidator.validatePassword("Pass1!")
        }
    }

    @Test
    fun `비밀번호 일치 여부를 검증할 수 있다`() {
        UserValidator.validatePasswordMatch("Password123!", "Password123!")
    }

    @Test
    fun `비밀번호가 일치하지 않으면 예외를 발생시킨다`() {
        assertThrows<IllegalArgumentException> {
            UserValidator.validatePasswordMatch("Password123!", "DifferentPass123!")
        }
    }
}

class UserModeTest {

    @Test
    fun `입력값으로 사용자 모드를 선택할 수 있다`() {
        assertEquals(UserMode.CUSTOMER, UserMode.select("1"))
        assertEquals(UserMode.ADMIN, UserMode.select("2"))
    }

    @Test
    fun `잘못된 입력값은 null을 반환한다`() {
        assertEquals(null, UserMode.select("3"))
        assertEquals(null, UserMode.select("abc"))
    }
}