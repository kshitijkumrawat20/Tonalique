package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ProductCategory
import com.example.data.repository.BeautyRepository
import com.example.ui.viewmodel.BeautyViewModel
import com.example.ui.viewmodel.StudioScreen
import com.example.ui.viewmodel.TryOnViewMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun readStringFromContext() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Atelier Beauty", appName)
  }

  @Test
  fun verifyCuratedRepositoryData() {
    assertTrue(BeautyRepository.availableShades.isNotEmpty())
    val velvetPlum = BeautyRepository.availableShades.first()
    assertEquals("Velvet Plum", velvetPlum.name)

    // Verify non-clinical cosmetic insights
    assertTrue(BeautyRepository.cosmeticInsights.size >= 4)
    val undertone = BeautyRepository.cosmeticInsights.first { it.id == "insight_undertone" }
    assertEquals("Neutral Rose", undertone.value)
  }

  @Test
  fun testViewModelStateAndScrubberClamping() {
    val vm = BeautyViewModel()
    val initialState = vm.uiState.value
    assertEquals(StudioScreen.TRY_ON, initialState.currentScreen)
    assertEquals(0.5f, initialState.scrubberPosition, 0.01f)

    // Test scrubber clamping
    vm.setScrubberPosition(1.5f)
    assertEquals(0.98f, vm.uiState.value.scrubberPosition, 0.01f)

    vm.setScrubberPosition(-0.5f)
    assertEquals(0.02f, vm.uiState.value.scrubberPosition, 0.01f)

    // Test shade selection
    val terracotta = BeautyRepository.availableShades[1]
    vm.selectShade(terracotta)
    assertEquals(terracotta.id, vm.uiState.value.selectedShade.id)
    assertEquals(terracotta.finishRecommended, vm.uiState.value.selectedFinish)
  }

  @Test
  fun testRoutineAndBudgetCalculation() {
    val vm = BeautyViewModel()
    val state = vm.uiState.value

    // Check routine calculation
    assertTrue(state.routineProducts.isNotEmpty())
    assertTrue(state.routineTotalCost > 0)

    // Test custom look creation
    val initialLookCount = state.looks.size
    vm.createCustomLook("Sunlit Bronze", "Golden Hour", "Sheer bronze cheek drape")
    assertEquals(initialLookCount + 1, vm.uiState.value.looks.size)
    val created = vm.uiState.value.looks.first()
    assertEquals("Sunlit Bronze", created.title)
    assertTrue(created.isSaved)
  }
}
