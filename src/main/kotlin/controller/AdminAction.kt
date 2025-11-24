package store.controller

enum class AdminAction(val displayName: String) {
    VIEW_INVENTORY("재고 현황"),
    ADD_PRODUCT("상품 추가"),
    UPDATE_PRODUCT("상품 수정"),
    ADD_PROMOTION("프로모션 추가"),
    VIEW_SALES("매출 통계"),
    MANAGE_STOCK("재고 관리");

    companion object {
        fun select(input: String): AdminAction? {
            return entries.getOrNull(input.toIntOrNull()?.minus(1) ?: -1)
        }
    }
}