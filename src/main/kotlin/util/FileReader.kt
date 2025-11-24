package store.util

import store.domain.product.Product
import store.domain.product.ProductCategory
import store.domain.promotion.Promotion
import java.io.File

object FileReader {
    private const val PRODUCTS_FILE = "src/main/resources/products.md"
    private const val PROMOTIONS_FILE = "src/main/resources/promotions.md"

    fun readProducts(): List<Product> {
        val lines = readFile(PRODUCTS_FILE)
        return parseProducts(lines)
    }

    fun readPromotions(): List<Promotion> {
        val lines = readFile(PROMOTIONS_FILE)
        return parsePromotions(lines)
    }

    private fun readFile(filePath: String): List<String> {
        return try {
            File(filePath).readLines()
                .filter { it.isNotBlank() }
        } catch (e: Exception) {
            throw IllegalStateException("[ERROR] 파일을 읽을 수 없습니다: $filePath", e)
        }
    }

    private fun parseProducts(lines: List<String>): List<Product> {
        if (lines.isEmpty()) return emptyList()

        return lines.drop(1).map { line ->
            parseProductLine(line)
        }
    }

    private fun parseProductLine(line: String): Product {
        try {
            val parts = line.split(",").map { it.trim() }

            require(parts.size >= 4) {
                "[ERROR] 상품 파일 형식이 올바르지 않습니다: $line"
            }

            val name = parts[0]
            val price = parts[1].toIntOrNull()
                ?: throw IllegalArgumentException("[ERROR] 가격 형식이 올바르지 않습니다: ${parts[1]}")
            val quantity = parts[2].toIntOrNull()
                ?: throw IllegalArgumentException("[ERROR] 수량 형식이 올바르지 않습니다: ${parts[2]}")
            val promotion = parts[3].takeIf { it != "null" }
            val description = if (parts.size > 4) parts[4].takeIf { it.isNotBlank() } else null
            val category = if (parts.size > 5) {
                ProductCategory.fromDisplayName(parts[5]) ?: ProductCategory.fromProductName(name)
            } else {
                ProductCategory.fromProductName(name)
            }

            return Product(name, price, quantity, promotion, description, category)
        } catch (e: Exception) {
            throw IllegalArgumentException("[ERROR] 상품 정보 파싱 실패: $line", e)
        }
    }

    private fun parsePromotions(lines: List<String>): List<Promotion> {
        if (lines.isEmpty()) return emptyList()

        return lines.drop(1).map { line ->
            parsePromotionLine(line)
        }
    }

    private fun parsePromotionLine(line: String): Promotion {
        try {
            val parts = line.split(",").map { it.trim() }

            require(parts.size >= 5) {
                "[ERROR] 프로모션 파일 형식이 올바르지 않습니다: $line"
            }

            val name = parts[0]
            val buy = parts[1].toIntOrNull()
                ?: throw IllegalArgumentException("[ERROR] buy 형식이 올바르지 않습니다: ${parts[1]}")
            val get = parts[2].toIntOrNull()
                ?: throw IllegalArgumentException("[ERROR] get 형식이 올바르지 않습니다: ${parts[2]}")
            val startDate = parts[3]
            val endDate = parts[4]

            Validator.validateDate(startDate, "시작일")
            Validator.validateDate(endDate, "종료일")
            Validator.validatePromotionValues(buy, get)

            return Promotion(name, buy, get, startDate, endDate)
        } catch (e: Exception) {
            throw IllegalArgumentException("[ERROR] 프로모션 정보 파싱 실패: $line", e)
        }
    }
}