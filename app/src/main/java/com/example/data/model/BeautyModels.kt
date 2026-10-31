package com.example.data.model

import androidx.annotation.DrawableRes

enum class ProductCategory(val label: String) {
    ALL("All Products"),
    PREP("Prep & Serums"),
    BASE("Complexion & Base"),
    LIPS("Lip Color"),
    CHEEKS("Cheeks & Blush")
}

data class ShadeOption(
    val id: String,
    val name: String,
    val hexColor: Long,
    val undertone: String,
    val finishRecommended: String,
    val description: String
)

data class Product(
    val id: String,
    val brand: String,
    val name: String,
    val shade: String,
    val finish: String,
    val category: ProductCategory,
    val reasonTag: String,
    val priceInRupees: Int,
    val verifiedTimestamp: String = "Price verified today",
    @DrawableRes val localDrawableRes: Int? = null,
    val imageUrl: String = "",
    val description: String = "",
    val ingredientsHighlight: String = "",
    val isFavorite: Boolean = false,
    val isInRoutine: Boolean = false
) {
    val formattedPrice: String
        get() = "₹$priceInRupees"
}

data class CosmeticInsight(
    val id: String,
    val title: String,
    val value: String,
    val cosmeticNote: String,
    val categoryTag: String
)

data class BeautyLook(
    val id: String,
    val title: String,
    val occasion: String,
    val finish: String,
    val shade: ShadeOption,
    @DrawableRes val beforeImageRes: Int,
    @DrawableRes val afterImageRes: Int,
    val description: String,
    val recommendedProductIds: List<String> = emptyList(),
    val isSaved: Boolean = false,
    val isCustom: Boolean = false
)

data class BudgetOption(
    val id: String,
    val label: String,
    val maxLimit: Int
)

data class RoutineStep(
    val stepIndex: Int,
    val phaseTitle: String,
    val product: Product?,
    val techniqueTip: String
)
