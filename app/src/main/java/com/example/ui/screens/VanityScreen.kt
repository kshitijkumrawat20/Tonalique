package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BeautyLook
import com.example.ui.components.EditorialChip
import com.example.ui.components.EditorialInputField
import com.example.ui.components.GhostButton
import com.example.ui.components.PrimaryPillButton
import com.example.ui.components.SecondaryPillButton
import com.example.ui.theme.Border
import com.example.ui.theme.Canvas
import com.example.ui.theme.Ink
import com.example.ui.theme.InkMuted
import com.example.ui.theme.OliveTertiary
import com.example.ui.theme.Plum
import com.example.ui.theme.RosePale
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceMuted
import com.example.ui.viewmodel.BeautyUiState

@Composable
fun VanityScreen(
    uiState: BeautyUiState,
    onLoadLook: (BeautyLook) -> Unit,
    onToggleSaveLook: (String) -> Unit,
    onOpenCreateLookDialog: () -> Unit,
    onDismissCreateLookDialog: () -> Unit,
    onCreateCustomLook: (title: String, occasion: String, notes: String) -> Unit,
    onProductClick: (com.example.data.model.Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Saved Looks, 1 = Wishlist

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Canvas)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("vanity_screen")
    ) {
        // Editorial Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PERSONAL ARCHIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkMuted,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "My Vanity",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Ink
                )
            }

            Box(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(Plum)
                    .clickable(onClick = onOpenCreateLookDialog)
                    .padding(horizontal = 14.dp)
                    .testTag("new_look_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Surface,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "New Look",
                        style = MaterialTheme.typography.labelSmall,
                        color = Surface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Segmented Switcher (Saved Looks vs Wishlist)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(9999.dp))
                .background(SurfaceMuted)
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(if (activeTab == 0) Surface else Color.Transparent)
                    .clickable { activeTab = 0 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Saved Looks (${uiState.looks.filter { it.isSaved }.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (activeTab == 0) Plum else InkMuted,
                    fontWeight = if (activeTab == 0) FontWeight.SemiBold else FontWeight.Normal
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(if (activeTab == 1) Surface else Color.Transparent)
                    .clickable { activeTab = 1 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Wishlist (${uiState.products.filter { it.isFavorite }.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (activeTab == 1) Plum else InkMuted,
                    fontWeight = if (activeTab == 1) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeTab == 0) {
            // Saved Looks list
            val savedLooks = uiState.looks.filter { it.isSaved }
            if (savedLooks.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved looks yet. Tap '+ New Look' to create one.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth().testTag("saved_looks_list"),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(savedLooks) { look ->
                        SavedLookCard(
                            look = look,
                            onLoad = { onLoadLook(look) },
                            onToggleSave = { onToggleSaveLook(look.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        } else {
            // Wishlist list
            val wishlisted = uiState.products.filter { it.isFavorite }
            if (wishlisted.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No favorites yet. Tap the heart icon on any curated product.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth().testTag("wishlist_list"),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(wishlisted) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProductClick(product) },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SurfaceMuted)
                                ) {
                                    if (product.localDrawableRes != null) {
                                        androidx.compose.foundation.Image(
                                            painter = painterResource(id = product.localDrawableRes),
                                            contentDescription = product.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.brand.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = InkMuted,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Ink,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${product.shade} · ${product.formattedPrice}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Plum
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Plum,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }

    // Create Custom Look Dialog
    if (uiState.showCreateLookDialog) {
        var lookTitle by remember { mutableStateOf("") }
        var selectedOccasion by remember { mutableStateOf("Studio Editorial") }
        var lookNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismissCreateLookDialog,
            containerColor = Surface,
            title = {
                Text(
                    text = "Archive New Look",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Ink
                )
            },
            text = {
                Column {
                    Text(
                        text = "Curate your current virtual try-on swatch and routine into a reusable editorial look profile.",
                        style = MaterialTheme.typography.bodySmall,
                        color = InkMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Look Title",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    EditorialInputField(
                        value = lookTitle,
                        onValueChange = { lookTitle = it },
                        placeholder = "e.g., Midnight Plum Velvet",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Occasion",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Studio Editorial", "Golden Hour", "Everyday Soft").forEach { occ ->
                            EditorialChip(
                                text = occ,
                                isSelected = occ == selectedOccasion,
                                onClick = { selectedOccasion = occ }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Stylist Notes",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    EditorialInputField(
                        value = lookNotes,
                        onValueChange = { lookNotes = it },
                        placeholder = "e.g., Focus on sheer finish and warm cheeks",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                PrimaryPillButton(
                    text = "Save to Archive",
                    onClick = {
                        onCreateCustomLook(lookTitle, selectedOccasion, lookNotes)
                    }
                )
            },
            dismissButton = {
                GhostButton(
                    text = "Cancel",
                    onClick = onDismissCreateLookDialog
                )
            }
        )
    }
}

@Composable
private fun SavedLookCard(
    look: BeautyLook,
    onLoad: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onLoad),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Image and Occasion Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceMuted)
            ) {
                Image(
                    painter = painterResource(id = look.afterImageRes),
                    contentDescription = look.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Shade swatch overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Surface.copy(alpha = 0.9f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(look.shade.hexColor))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${look.shade.name} · ${look.finish}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Bookmark Icon Button
                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .background(Surface.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (look.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save look",
                        tint = Plum,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = look.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Ink
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(RosePale)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = look.occasion,
                        style = MaterialTheme.typography.labelSmall,
                        color = Plum,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = look.description,
                style = MaterialTheme.typography.bodySmall,
                color = InkMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            PrimaryPillButton(
                text = "Load in Studio Try-On",
                onClick = onLoad,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
