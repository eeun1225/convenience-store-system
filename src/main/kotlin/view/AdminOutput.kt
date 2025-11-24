package store.view

import store.domain.product.Product
import store.domain.product.ProductCategory

class AdminOutput(private val out: CommonOutput) {

    fun askAdminNumber() {
        out.printInline("관리자 번호를 입력해주세요: ")
    }

    fun askPassword() {
        out.printInline("비밀번호를 입력해주세요: ")
    }

    fun showAdminMenu() {
        out.printMessage("\n=== 관리자 기능 ===")
        out.printMessage("1. 신규 상품 등록")
        out.printMessage("2. 상품 정보 수정")
        out.printMessage("3. 프로모션 추가")
        out.printMessage("4. 통계 보기")
        out.printMessage("5. 종료")
        out.printInline("선택: ")
    }

    fun askContinueAdminMode() {
        out.askYesOrNo("계속 관리하시겠습니까?")
    }

    fun showCategoryMenu(categories: List<ProductCategory>) {
        out.printMessage("\n카테고리를 선택하세요:")
        categories.forEachIndexed { idx, category ->
            out.printMessage("${idx + 1}. ${category.displayName}")
        }
    }

    fun askProductToUpdate() {
        out.printInline("수정할 상품명: ")
    }

    fun askUpdatePrice() {
        out.printInline("새 가격: ")
    }

    fun askUpdateQuantity() {
        out.printInline("새 수량: ")
    }

    fun printAdminInventory(products: List<Product>) {
        out.printMessage("\n=== 전체 재고 목록 ===")
        products.forEach { product ->
            out.printMessage("${product.name} | ${product.price}원 | 수량: ${product.quantity}")
        }
    }

    fun askViewStatistics() {
        out.askYesOrNo("카테고리별 통계를 보시겠습니까?")
    }

    fun printCategoryStatistics(products: List<Product>) {
        out.printMessage("\n=== 카테고리별 재고 통계 ===")
        val grouped = products.groupBy { it.category }

        grouped.forEach { (category, items) ->
            val totalStock = items.sumOf { it.quantity }
            out.printMessage("${category.displayName}: 총 $totalStock 개")
        }
    }
}