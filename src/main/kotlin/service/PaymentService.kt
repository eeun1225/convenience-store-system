package store.service

import store.domain.purchase.MembershipDiscount
import store.domain.purchase.PurchaseItem
import store.domain.purchase.Receipt

class PaymentService {
    fun createReceipt(
        items: List<PurchaseItem>,
        applyMembership: Boolean
    ): Receipt {
        val membershipDiscount = if (applyMembership) {
            calculateMembershipDiscount(items)
        } else 0

        return Receipt(items, membershipDiscount)
    }

    private fun calculateMembershipDiscount(items: List<PurchaseItem>): Int {
        val nonPromotionAmount = items.sumOf { item ->
            item.getNonPromotionQuantity() * item.price
        }
        return MembershipDiscount.calculate(nonPromotionAmount)
    }
}