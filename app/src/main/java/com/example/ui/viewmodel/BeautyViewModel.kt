package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.R
import com.example.data.model.BeautyLook
import com.example.data.model.BudgetOption
import com.example.data.model.CosmeticInsight
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.RoutineStep
import com.example.data.model.ShadeOption
import com.example.data.repository.BeautyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class TryOnViewMode {
    BEFORE,
    SPLIT,
    AFTER
}

enum class StudioScreen(val label: String, val iconName: String) {
    TRY_ON("Try-On", "face"),
    INSIGHTS("Insights", "auto_awesome"),
    PRODUCTS("Curated", "shopping_bag"),
    ROUTINE("Routine", "spa"),
    VANITY("Vanity", "bookmark")
}

data class BeautyUiState(
    val currentScreen: StudioScreen = StudioScreen.TRY_ON,
    val scrubberPosition: Float = 0.5f,
    val tryOnViewMode: TryOnViewMode = TryOnViewMode.SPLIT,
    val selectedShade: ShadeOption = BeautyRepository.availableShades.first(),
    val selectedFinish: String = "Satin Velvet",
    val selectedOccasion: String = "Studio Editorial",
    val selectedBudget: BudgetOption = BeautyRepository.budgetOptions[1], // ₹1,500
    val customBudgetValue: String = "",
    val selectedCategory: ProductCategory = ProductCategory.ALL,
    val searchQuery: String = "",
    val products: List<Product> = BeautyRepository.initialProducts,
    val looks: List<BeautyLook> = BeautyRepository.initialLooks,
    val activeLook: BeautyLook = BeautyRepository.initialLooks.first(),
    val cosmeticInsights: List<CosmeticInsight> = BeautyRepository.cosmeticInsights,
    val selectedProductForDetail: Product? = null,
    val showCreateLookDialog: Boolean = false,
    val bannerNotice: String? = null
) {
    val effectiveBudgetLimit: Int
        get() = if (selectedBudget.id == "b_custom") {
            customBudgetValue.toIntOrNull() ?: 2500
        } else {
            selectedBudget.maxLimit
        }

    val routineProducts: List<Product>
        get() = products.filter { it.isInRoutine }

    val routineTotalCost: Int
        get() = routineProducts.sumOf { it.priceInRupees }

    val isRoutineWithinBudget: Boolean
        get() = routineTotalCost <= effectiveBudgetLimit

    val filteredProducts: List<Product>
        get() = products.filter { product ->
            val matchesCategory = (selectedCategory == ProductCategory.ALL || product.category == selectedCategory)
            val matchesQuery = searchQuery.isBlank() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true) ||
                    product.shade.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }

    val routineSteps: List<RoutineStep>
        get() {
            val prepProd = products.firstOrNull { it.isInRoutine && it.category == ProductCategory.PREP }
                ?: products.firstOrNull { it.category == ProductCategory.PREP }
            val baseProd = products.firstOrNull { it.isInRoutine && it.category == ProductCategory.BASE }
                ?: products.firstOrNull { it.category == ProductCategory.BASE }
            val cheekProd = products.firstOrNull { it.isInRoutine && it.category == ProductCategory.CHEEKS }
                ?: products.firstOrNull { it.category == ProductCategory.CHEEKS }
            val lipProd = products.firstOrNull { it.isInRoutine && it.category == ProductCategory.LIPS }
                ?: products.firstOrNull { it.category == ProductCategory.LIPS }

            return listOf(
                RoutineStep(1, "Prep & Hydrate", prepProd, "Pat 3-4 drops into warmed palms and gently press outward from cheekbones to jawline."),
                RoutineStep(2, "Featherweight Base", baseProd, "Diffuse sheer veil using fingertips, prioritizing center of face while keeping edges sheer."),
                RoutineStep(3, "Cosmetic Flush", cheekProd, "Warm cream on back of hand and tap upward along the cheek curve toward temples."),
                RoutineStep(4, "Velvet Lip Accent", lipProd, "Dab pigment at center of lips, pressing outward with clean ring finger for soft editorial blur.")
            )
        }
}

class BeautyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BeautyUiState())
    val uiState: StateFlow<BeautyUiState> = _uiState.asStateFlow()

    init {
        // Pre-add a couple of products to routine for immediate delightful showcase
        _uiState.update { state ->
            val updated = state.products.map {
                if (it.id == "prod_serum" || it.id == "prod_lip_plum") {
                    it.copy(isInRoutine = true)
                } else it
            }
            state.copy(products = updated)
        }
    }

    fun setScreen(screen: StudioScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun setScrubberPosition(pos: Float) {
        val clamped = pos.coerceIn(0.02f, 0.98f)
        _uiState.update { it.copy(scrubberPosition = clamped, tryOnViewMode = TryOnViewMode.SPLIT) }
    }

    fun setTryOnMode(mode: TryOnViewMode) {
        val pos = when (mode) {
            TryOnViewMode.BEFORE -> 0.0f
            TryOnViewMode.SPLIT -> 0.5f
            TryOnViewMode.AFTER -> 1.0f
        }
        _uiState.update { it.copy(tryOnViewMode = mode, scrubberPosition = pos) }
    }

    fun selectShade(shade: ShadeOption) {
        _uiState.update { current ->
            current.copy(
                selectedShade = shade,
                selectedFinish = shade.finishRecommended,
                bannerNotice = "Applied ${shade.name} (${shade.finishRecommended})"
            )
        }
    }

    fun selectFinish(finish: String) {
        _uiState.update { it.copy(selectedFinish = finish) }
    }

    fun selectOccasion(occasion: String) {
        _uiState.update { it.copy(selectedOccasion = occasion) }
    }

    fun selectBudget(option: BudgetOption) {
        _uiState.update { it.copy(selectedBudget = option) }
    }

    fun setCustomBudget(amount: String) {
        _uiState.update { it.copy(customBudgetValue = amount) }
    }

    fun selectCategory(category: ProductCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleFavorite(productId: String) {
        _uiState.update { current ->
            val updated = current.products.map {
                if (it.id == productId) it.copy(isFavorite = !it.isFavorite) else it
            }
            current.copy(products = updated)
        }
    }

    fun toggleRoutine(productId: String) {
        _uiState.update { current ->
            val updated = current.products.map {
                if (it.id == productId) it.copy(isInRoutine = !it.isInRoutine) else it
            }
            val target = updated.firstOrNull { it.id == productId }
            val notice = if (target?.isInRoutine == true) {
                "Added ${target.name} to routine"
            } else {
                "Removed from routine"
            }
            current.copy(products = updated, bannerNotice = notice)
        }
    }

    fun loadLook(look: BeautyLook) {
        _uiState.update { current ->
            current.copy(
                activeLook = look,
                selectedShade = look.shade,
                selectedFinish = look.finish,
                selectedOccasion = look.occasion,
                currentScreen = StudioScreen.TRY_ON,
                bannerNotice = "Loaded ${look.title}"
            )
        }
    }

    fun toggleLookSaved(lookId: String) {
        _uiState.update { current ->
            val updated = current.looks.map {
                if (it.id == lookId) it.copy(isSaved = !it.isSaved) else it
            }
            current.copy(looks = updated)
        }
    }

    fun openProductDetail(product: Product) {
        _uiState.update { it.copy(selectedProductForDetail = product) }
    }

    fun dismissProductDetail() {
        _uiState.update { it.copy(selectedProductForDetail = null) }
    }

    fun showCreateLookDialog(show: Boolean) {
        _uiState.update { it.copy(showCreateLookDialog = show) }
    }

    fun createCustomLook(title: String, occasion: String, notes: String) {
        val current = _uiState.value
        val newLook = BeautyLook(
            id = "custom_${System.currentTimeMillis()}",
            title = if (title.isBlank()) "My Custom Studio Look" else title,
            occasion = occasion,
            finish = current.selectedFinish,
            shade = current.selectedShade,
            beforeImageRes = R.drawable.tryon_before,
            afterImageRes = R.drawable.tryon_after,
            description = if (notes.isBlank()) "Personal editorial curation created in Atelier Studio." else notes,
            recommendedProductIds = current.routineProducts.map { it.id },
            isSaved = true,
            isCustom = true
        )
        _uiState.update {
            it.copy(
                looks = listOf(newLook) + it.looks,
                showCreateLookDialog = false,
                bannerNotice = "Saved new look: ${newLook.title}"
            )
        }
    }

    fun dismissBanner() {
        _uiState.update { it.copy(bannerNotice = null) }
    }
}
