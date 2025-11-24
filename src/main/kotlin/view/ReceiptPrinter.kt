package store.view

import store.domain.purchase.Receipt

class ReceiptPrinter {

    fun print(receipt: Receipt) {
        printHeader()
        printItems(receipt)
        printFreeItems(receipt)
        printSummary(receipt)
    }

    private fun printHeader() {
        println("\n==============W 편의점================")
        println("상품명\t\t수량\t금액")
    }

    private fun printItems(receipt: Receipt) {
        receipt.items.forEach { item ->
            println("${item.productName}\t\t${item.quantity}\t${formatPrice(item.getTotalAmount())}")
        }
    }

    private fun printFreeItems(receipt: Receipt) {
        val freeItems = receipt.getFreeItems()
        if (freeItems.isNotEmpty()) {
            println("=============증\t정===============")
            freeItems.forEach { (name, quantity) ->
                println("$name\t\t$quantity")
            }
        }
    }

    private fun printSummary(receipt: Receipt) {
        println("====================================")
        println("총구매액\t\t${receipt.getTotalQuantity()}\t${formatPrice(receipt.getTotalAmount())}")
        println("행사할인\t\t\t-${formatPrice(receipt.getPromotionDiscount())}")
        println("멤버십할인\t\t\t-${formatPrice(receipt.membershipDiscount)}")
        println("내실돈\t\t\t ${formatPrice(receipt.getFinalAmount())}\n")
    }

    private fun formatPrice(price: Int): String {
        return "%,d원".format(price)
    }
}