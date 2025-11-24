package store.domain.product

enum class ProductCategory(
    val displayName: String,
    val description: String
) {
    BEVERAGE("음료", "음료수 및 음료 제품"),
    SNACK("스낵", "과자 및 간식류"),
    FOOD("식품", "식사 대용 식품"),
    INSTANT("즉석식품", "즉석 조리 식품"),
    HEALTH("건강식품", "건강 보조 식품"),
    DAILY("생활용품", "일상 생활용품"),
    ETC("기타", "기타 상품");

    companion object {
        fun fromDisplayName(displayName: String): ProductCategory? {
            return entries.find { it.displayName == displayName }
        }

        fun fromDescription(description: String): ProductCategory? {
            return entries.find { it.description == description }
        }

        fun fromProductName(productName: String): ProductCategory {
            return when {
                productName.contains("콜라") ||
                        productName.contains("사이다") ||
                        productName.contains("주스") ||
                        productName.contains("탄산수") ||
                        productName.contains("물") ||
                        productName.contains("워터") -> BEVERAGE

                productName.contains("칩") ||
                        productName.contains("초코") ||
                        productName.contains("바") ||
                        productName.contains("새우깡") ||
                        productName.contains("과자") -> SNACK

                productName.contains("도시락") ||
                        productName.contains("김밥") -> FOOD

                productName.contains("라면") ||
                        productName.contains("컵라면") ||
                        productName.contains("햇반") ||
                        productName.contains("핫도그") -> INSTANT

                productName.contains("비타민") ||
                        productName.contains("에너지") ||
                        productName.contains("프로틴") -> HEALTH

                else -> ETC
            }
        }

        fun getAllCategories(): List<ProductCategory> = entries
    }
}