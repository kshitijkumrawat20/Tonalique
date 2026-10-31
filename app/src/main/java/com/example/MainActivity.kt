package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.RoutineScreen
import com.example.ui.screens.TryOnScreen
import com.example.ui.screens.VanityScreen
import com.example.ui.theme.AtelierBeautyTheme
import com.example.ui.theme.Border
import com.example.ui.theme.Canvas
import com.example.ui.theme.Ink
import com.example.ui.theme.InkMuted
import com.example.ui.theme.Plum
import com.example.ui.theme.RosePale
import com.example.ui.theme.Surface
import com.example.ui.viewmodel.BeautyUiState
import com.example.ui.viewmodel.BeautyViewModel
import com.example.ui.viewmodel.StudioScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: BeautyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtelierBeautyTheme {
                val uiState by viewModel.uiState.collectAsState()

                AtelierBeautyApp(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun AtelierBeautyApp(
    uiState: BeautyUiState,
    viewModel: BeautyViewModel
) {
    // Graceful back navigation: if on subscreen, back returns to Try-On
    if (uiState.currentScreen != StudioScreen.TRY_ON) {
        BackHandler {
            viewModel.setScreen(StudioScreen.TRY_ON)
        }
    }

    // Auto-dismiss banner notice
    LaunchedEffect(uiState.bannerNotice) {
        if (uiState.bannerNotice != null) {
            delay(2800)
            viewModel.dismissBanner()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Canvas)) {
        val isWideScreen = maxWidth > 600.dp

        if (isWideScreen) {
            // Adaptive Tablet Layout: Side NavigationRail + Centered Content Pane
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = Surface,
                    contentColor = Ink,
                    modifier = Modifier
                        .fillMaxHeight()
                        .border(1.dp, Border, RoundedCornerShape(0.dp))
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .testTag("tablet_nav_rail")
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Plum),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Surface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    StudioScreen.values().forEach { screen ->
                        NavigationRailItem(
                            selected = uiState.currentScreen == screen,
                            onClick = { viewModel.setScreen(screen) },
                            icon = {
                                Icon(
                                    imageVector = getScreenIcon(screen),
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Surface,
                                selectedTextColor = Plum,
                                indicatorColor = Plum,
                                unselectedIconColor = InkMuted,
                                unselectedTextColor = InkMuted
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(modifier = Modifier.fillMaxSize().widthIn(max = 840.dp)) {
                        ScreenContent(
                            uiState = uiState,
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        } else {
            // Standard Mobile Layout: Scaffold with Top Status & Bottom NavigationBar
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Canvas,
                contentWindowInsets = WindowInsets.statusBars,
                bottomBar = {
                    AtelierBottomBar(
                        currentScreen = uiState.currentScreen,
                        onScreenSelect = { viewModel.setScreen(it) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ScreenContent(
                        uiState = uiState,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Banner Notice
                    AnimatedVisibility(
                        visible = uiState.bannerNotice != null,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                    ) {
                        uiState.bannerNotice?.let { notice ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(Plum)
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                                    .testTag("app_banner_notice")
                            ) {
                                Text(
                                    text = notice,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Surface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    uiState: BeautyUiState,
    viewModel: BeautyViewModel,
    modifier: Modifier = Modifier
) {
    when (uiState.currentScreen) {
        StudioScreen.TRY_ON -> {
            TryOnScreen(
                uiState = uiState,
                onScrubberChange = { viewModel.setScrubberPosition(it) },
                onModeChange = { viewModel.setTryOnMode(it) },
                onShadeSelect = { viewModel.selectShade(it) },
                onFinishSelect = { viewModel.selectFinish(it) },
                onOccasionSelect = { viewModel.selectOccasion(it) },
                onSaveLook = { viewModel.toggleLookSaved(uiState.activeLook.id) },
                onNavigateToProducts = { viewModel.setScreen(StudioScreen.PRODUCTS) },
                onNavigateToRoutine = { viewModel.setScreen(StudioScreen.ROUTINE) },
                modifier = modifier
            )
        }
        StudioScreen.INSIGHTS -> {
            InsightsScreen(
                uiState = uiState,
                onNavigateToTryOn = { viewModel.setScreen(StudioScreen.TRY_ON) },
                modifier = modifier
            )
        }
        StudioScreen.PRODUCTS -> {
            ProductsScreen(
                uiState = uiState,
                onSearchChange = { viewModel.setSearchQuery(it) },
                onCategorySelect = { viewModel.selectCategory(it) },
                onProductClick = { viewModel.openProductDetail(it) },
                onToggleRoutine = { viewModel.toggleRoutine(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onDismissDetail = { viewModel.dismissProductDetail() },
                modifier = modifier
            )
        }
        StudioScreen.ROUTINE -> {
            RoutineScreen(
                uiState = uiState,
                onBudgetSelect = { viewModel.selectBudget(it) },
                onCustomBudgetChange = { viewModel.setCustomBudget(it) },
                onRemoveProductFromRoutine = { viewModel.toggleRoutine(it) },
                onSaveToVanity = { viewModel.showCreateLookDialog(true) },
                onExploreProducts = { viewModel.setScreen(StudioScreen.PRODUCTS) },
                modifier = modifier
            )
        }
        StudioScreen.VANITY -> {
            VanityScreen(
                uiState = uiState,
                onLoadLook = { viewModel.loadLook(it) },
                onToggleSaveLook = { viewModel.toggleLookSaved(it) },
                onOpenCreateLookDialog = { viewModel.showCreateLookDialog(true) },
                onDismissCreateLookDialog = { viewModel.showCreateLookDialog(false) },
                onCreateCustomLook = { title, occ, notes ->
                    viewModel.createCustomLook(title, occ, notes)
                },
                onProductClick = { viewModel.openProductDetail(it) },
                modifier = modifier
            )
        }
    }
}

@Composable
fun AtelierBottomBar(
    currentScreen: StudioScreen,
    onScreenSelect: (StudioScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .border(1.dp, Border, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .testTag("atelier_bottom_nav"),
        containerColor = Surface,
        contentColor = Ink,
        tonalElevation = 0.dp
    ) {
        StudioScreen.values().forEach { screen ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onScreenSelect(screen) },
                icon = {
                    Icon(
                        imageVector = getScreenIcon(screen),
                        contentDescription = screen.label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = screen.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Surface,
                    selectedTextColor = Plum,
                    indicatorColor = Plum,
                    unselectedIconColor = InkMuted,
                    unselectedTextColor = InkMuted
                ),
                modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
            )
        }
    }
}

private fun getScreenIcon(screen: StudioScreen): ImageVector {
    return when (screen) {
        StudioScreen.TRY_ON -> Icons.Default.Face
        StudioScreen.INSIGHTS -> Icons.Default.AutoAwesome
        StudioScreen.PRODUCTS -> Icons.Default.ShoppingBag
        StudioScreen.ROUTINE -> Icons.Default.Spa
        StudioScreen.VANITY -> Icons.Default.Bookmark
    }
}
