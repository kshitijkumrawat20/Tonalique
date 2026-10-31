package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.components.EditorialChip
import com.example.ui.components.EditorialInputField
import com.example.ui.components.PrimaryPillButton
import com.example.ui.components.ProductRecommendationCard
import com.example.ui.components.SecondaryPillButton
import com.example.ui.theme.Border
import com.example.ui.theme.Canvas
import com.example.ui.theme.Ink
import com.example.ui.theme.InkMuted
import com.example.ui.theme.OliveTertiary
import com.example.ui.theme.Plum
import com.example.ui.theme.PriceDisplayStyle
import com.example.ui.theme.RosePale
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceMuted
import com.example.ui.viewmodel.BeautyUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    uiState: BeautyUiState,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (ProductCategory) -> Unit,
    onProductClick: (Product) -> Unit,
    onToggleRoutine: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDismissDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Canvas)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("products_screen")
    ) {
        // Editorial Header
        Text(
            text = "CURATED CATALOG",
            style = MaterialTheme.typography.labelSmall,
            color = InkMuted,
            letterSpacing = 1.2.sp
        )
        Text(
            text = "Curated Formulations",
            style = MaterialTheme.typography.headlineLarge,
            color = Ink
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Clean cosmetic recommendations verified for undertone alignment and finish texture.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Input Box
        EditorialInputField(
            value = uiState.searchQuery,
            onValueChange = onSearchChange,
            placeholder = "Search by product, brand, or finish…",
            modifier = Modifier.fillMaxWidth(),
            testTag = "products_search_input"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ProductCategory.values()) { cat ->
                EditorialChip(
                    text = cat.label,
                    isSelected = uiState.selectedCategory == cat,
                    onClick = { onCategorySelect(cat) },
                    testTag = "category_chip_${cat.name}"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Products List
        if (uiState.filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No curated products found matching criteria.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("products_list"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.filteredProducts) { product ->
                    ProductRecommendationCard(
                        product = product,
                        onCardClick = { onProductClick(product) },
                        onToggleRoutine = { onToggleRoutine(product.id) },
                        onToggleFavorite = { onToggleFavorite(product.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Product Detail Bottom Sheet
    uiState.selectedProductForDetail?.let { selected ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = onDismissDetail,
            sheetState = sheetState,
            containerColor = Surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .testTag("product_detail_sheet")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selected.brand.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = InkMuted,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = selected.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Ink
                        )
                        Text(
                            text = "${selected.shade} · ${selected.finish}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Plum,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(onClick = onDismissDetail) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Ink)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Product Image Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceMuted)
                ) {
                    if (selected.localDrawableRes != null) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = selected.localDrawableRes),
                            contentDescription = selected.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (selected.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = selected.imageUrl,
                            contentDescription = selected.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reason Match Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceMuted)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = selected.reasonTag,
                        style = MaterialTheme.typography.labelMedium,
                        color = Ink,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = selected.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Key Botanical Actives",
                    style = MaterialTheme.typography.labelMedium,
                    color = InkMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selected.ingredientsHighlight,
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Price and Add Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selected.formattedPrice,
                            style = PriceDisplayStyle,
                            color = Ink
                        )
                        Text(
                            text = selected.verifiedTimestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = OliveTertiary
                        )
                    }

                    PrimaryPillButton(
                        text = if (selected.isInRoutine) "In Routine" else "Add to Routine",
                        onClick = {
                            onToggleRoutine(selected.id)
                            onDismissDetail()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = Surface,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        testTag = "detail_add_routine_btn"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
