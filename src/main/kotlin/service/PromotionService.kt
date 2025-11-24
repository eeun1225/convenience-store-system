package store.service

import store.domain.promotion.Promotion
import store.domain.promotion.PromotionRepository
import store.view.NewPromotionInfo

class PromotionService(
    private val promotionRepository: PromotionRepository
) {
    fun getActivePromotion(promotionName: String, currentDate: String): Promotion? {
        val promotion = promotionRepository.findByName(promotionName) ?: return null
        return if (promotion.isActive(currentDate)) promotion else null
    }

    fun calculatePromotionBenefit(
        quantity: Int,
        promotion: Promotion,
        promotionStock: Int
    ): PromotionResult {
        val maxPromotionSets = minOf(
            quantity / promotion.getRequiredQuantity(),
            promotionStock / promotion.getRequiredQuantity()
        )

        val promotionAppliedQuantity = maxPromotionSets * promotion.getRequiredQuantity()
        val freeQuantity = maxPromotionSets * promotion.get
        val remainingQuantity = quantity - promotionAppliedQuantity

        return PromotionResult(
            promotionQuantity = promotionAppliedQuantity,
            freeQuantity = freeQuantity,
            nonPromotionQuantity = remainingQuantity
        )
    }

    fun addPromotion(promotionInfo: NewPromotionInfo) {
        val promotion = Promotion(
            name = promotionInfo.name,
            buy = promotionInfo.buy,
            get = promotionInfo.get,
            startDate = promotionInfo.startDate,
            endDate = promotionInfo.endDate
        )
        promotionRepository.save(promotion)
    }

    data class PromotionResult(
        val promotionQuantity: Int,
        val freeQuantity: Int,
        val nonPromotionQuantity: Int
    )
}