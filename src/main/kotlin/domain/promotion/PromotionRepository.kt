package store.domain.promotion

class PromotionRepository(
    private val promotions: MutableMap<String, Promotion> = mutableMapOf()
) {
    fun save(promotion: Promotion) {
        promotions[promotion.name] = promotion
    }

    fun saveAll(promotions: List<Promotion>) {
        promotions.forEach { save(it) }
    }

    fun findByName(name: String): Promotion? {
        return promotions[name]
    }

    fun findAll(): List<Promotion> {
        return promotions.values.toList()
    }

    fun exists(name: String): Boolean {
        return promotions.containsKey(name)
    }
}