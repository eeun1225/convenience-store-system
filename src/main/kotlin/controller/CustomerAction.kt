package store.controller

enum class CustomerAction(val displayName: String) {
    VIEW_PRODUCTS("상품 보기"),
    ADD_TO_CART("장바구니에 담기"),
    VIEW_CART("장바구니 보기"),
    CHECKOUT("결제하기"),
    VIEW_HISTORY("구매 이력 보기");

    companion object {
        fun select(input: String): CustomerAction? {
            return entries.getOrNull(input.toIntOrNull()?.minus(1) ?: -1)
        }
    }
}