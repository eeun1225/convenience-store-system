package store.domain.promotion

enum class PromotionType(
    val displayName: String,
    val buy: Int,
    val get: Int
) {
    ONE_PLUS_ONE("1+1", 1, 1),
    TWO_PLUS_ONE("2+1", 2, 1),
    TWO_PLUS_TWO("2+2", 2, 2);

    companion object {
        fun fromBuyGet(buy: Int, get: Int): PromotionType? {
            return entries.find { it.buy == buy && it.get == get }
        }

        fun fromDisplayName(displayName: String): PromotionType? {
            return entries.find { it.displayName == displayName }
        }
    }

    fun calculateFreeQuantity(purchaseQuantity: Int): Int {
        return purchaseQuantity / getRequiredQuantity() * get
    }

    fun getAdditionalQuantityForPromotion(purchaseQuantity: Int): Int {
        return if (canGetMoreFree(purchaseQuantity)) get else 0
    }

    private fun getRequiredQuantity(): Int = buy + get

    private fun canGetMoreFree(purchaseQuantity: Int): Boolean {
        val remainder = purchaseQuantity % getRequiredQuantity()
        return remainder == buy
    }
}