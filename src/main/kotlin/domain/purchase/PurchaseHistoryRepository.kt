package store.domain.purchase

import java.time.LocalDateTime

class PurchaseHistoryRepository(
    val histories: MutableMap<String, MutableList<PurchaseHistory>> = mutableMapOf()
) {
    fun save(history: PurchaseHistory) {
        histories.getOrPut(history.userId) { mutableListOf() }.add(history)
    }

    fun findByUserId(userId: String): List<PurchaseHistory> {
        return histories[userId]?.sortedByDescending { it.purchasedAt } ?: emptyList()
    }

    fun findById(id: String): PurchaseHistory? {
        return histories.values.flatten().find { it.id == id }
    }

    fun findRecentByUserId(userId: String, limit: Int = 10): List<PurchaseHistory> {
        return findByUserId(userId).take(limit)
    }

    fun getTotalPurchaseAmount(userId: String): Int {
        return findByUserId(userId).sumOf { it.getTotalAmount() }
    }

    fun getTotalPurchaseCount(userId: String): Int {
        return findByUserId(userId).size
    }

    fun findByDateRange(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<PurchaseHistory> {
        return findByUserId(userId).filter {
            it.purchasedAt in startDate..endDate
        }
    }
}