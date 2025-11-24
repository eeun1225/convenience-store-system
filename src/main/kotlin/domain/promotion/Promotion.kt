package store.domain.promotion

data class Promotion(
    val name: String,
    val buy: Int,
    val get: Int,
    val startDate: String,
    val endDate: String
) {
    fun isActive(currentDate: String): Boolean {
        return currentDate in startDate..endDate
    }

    fun getRequiredQuantity(): Int = buy + get

    fun canGetMoreFree(purchaseQuantity: Int): Boolean {
        val remainder = purchaseQuantity % getRequiredQuantity()
        return remainder == buy
    }
}