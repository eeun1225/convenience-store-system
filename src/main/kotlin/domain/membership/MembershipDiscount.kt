package store.domain.purchase

object MembershipDiscount {
    private const val DISCOUNT_RATE = 0.3
    private const val MAX_DISCOUNT = 8000

    fun calculate(nonPromotionAmount: Int): Int {
        val discount = (nonPromotionAmount * DISCOUNT_RATE).toInt()
        return minOf(discount, MAX_DISCOUNT)
    }
}