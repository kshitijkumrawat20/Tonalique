package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.repository.BeautyRepository
import com.example.ui.components.EditorialChip
import com.example.ui.components.GhostButton
import com.example.ui.components.PrimaryPillButton
import com.example.ui.components.SecondaryPillButton
import com.example.ui.components.ShadeSwatchItem
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
import com.example.ui.viewmodel.StudioScreen
import com.example.ui.viewmodel.TryOnViewMode
import kotlin.math.roundToInt

@Composable
fun TryOnScreen(
    uiState: BeautyUiState,
    onScrubberChange: (Float) -> Unit,
    onModeChange: (TryOnViewMode) -> Unit,
    onShadeSelect: (com.example.data.model.ShadeOption) -> Unit,
    onFinishSelect: (String) -> Unit,
    onOccasionSelect: (String) -> Unit,
    onSaveLook: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToRoutine: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Canvas)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("try_on_screen")
    ) {
        // Editorial Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ATELIER STUDIO",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkMuted,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Virtual Try-On",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Ink
                )
            }

            // Occasion pill tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(RosePale)
                    .border(1.dp, Plum.copy(alpha = 0.3f), RoundedCornerShape(9999.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = uiState.selectedOccasion,
                    style = MaterialTheme.typography.labelSmall,
                    color = Plum,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Virtual Try-On Comparison Module
        // Split Container: corner radius 20dp, border 1px solid border
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tryon_comparison_container"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, Border),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {

                // Viewfinder Box With Constraints for Interactive Scrubber
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceMuted)
                        .clipToBounds()
                ) {
                    val containerWidthPx = constraints.maxWidth.toFloat()
                    val scrubberFraction = uiState.scrubberPosition.coerceIn(0.01f, 0.99f)
                    val splitX = containerWidthPx * scrubberFraction

                    // LAYER 1: Full "Before" image underneath
                    Image(
                        painter = painterResource(id = uiState.activeLook.beforeImageRes),
                        contentDescription = "Natural Bare Skin portrait before try-on",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // LAYER 2: "After / Preview" image clipped to the right side of scrubber
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                FractionalRectShape(
                                    startFraction = if (uiState.tryOnViewMode == TryOnViewMode.BEFORE) 1.0f
                                    else if (uiState.tryOnViewMode == TryOnViewMode.AFTER) 0.0f
                                    else scrubberFraction,
                                    endFraction = 1.0f
                                )
                            )
                    ) {
                        Image(
                            painter = painterResource(id = uiState.activeLook.afterImageRes),
                            contentDescription = "Curated Makeup Preview with ${uiState.selectedShade.name}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Subtle tint overlay to reflect the selected shade color nuances on lips/cheeks
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(uiState.selectedShade.hexColor).copy(alpha = 0.08f))
                        )
                    }

                    // LAYER 3: Interactive Scrubber (thin 2dp vertical divider line in #FFFCF9 with centered 36dp pill handle)
                    if (uiState.tryOnViewMode == TryOnViewMode.SPLIT) {
                        // Vertical divider line
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(splitX.roundToInt(), 0) }
                                .width(2.dp)
                                .fillMaxHeight()
                                .background(Surface)
                        )

                        // Centered 36dp pill handle containing dual directional indicators (< >)
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        (splitX - 18.dp.toPx()).roundToInt(),
                                        (constraints.maxHeight / 2 - 18.dp.toPx()).roundToInt()
                                    )
                                }
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Surface)
                                .border(1.dp, Border, CircleShape)
                                .pointerInput(Unit) {
                                    detectHorizontalDragGestures { change, dragAmount ->
                                        change.consume()
                                        val newPos = (splitX + dragAmount) / containerWidthPx
                                        onScrubberChange(newPos)
                                    }
                                }
                                .testTag("tryon_scrubber_handle")
                                .semantics { contentDescription = "Drag to compare before and after preview" },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = Plum,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Plum,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }

                    // Interactive touch area covering entire viewfinder for smooth dragging
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(containerWidthPx) {
                                detectHorizontalDragGestures { change, dragAmount ->
                                    change.consume()
                                    val currentPx = containerWidthPx * uiState.scrubberPosition
                                    val newPos = (currentPx + dragAmount) / containerWidthPx
                                    onScrubberChange(newPos)
                                }
                            }
                    )

                    // Floating Watermark / Disclaimer Pill in surface with 80% opacity, label-sm
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(Surface.copy(alpha = 0.85f))
                            .border(0.5.dp, Border, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("tryon_disclaimer_pill")
                    ) {
                        Text(
                            text = "AI illustrative preview · Non-diagnostic",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkMuted,
                            fontSize = 10.sp
                        )
                    }

                    // Floating Shade Label Pill top left
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(Surface.copy(alpha = 0.9f))
                            .border(0.5.dp, Border, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(uiState.selectedShade.hexColor))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${uiState.selectedShade.name} · ${uiState.selectedFinish}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Ink,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Accessibility Fallback: Segmented control below the viewfinder with Before, Split, Preview options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9999.dp))
                        .background(SurfaceMuted)
                        .padding(3.dp)
                        .testTag("tryon_segmented_controls"),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SegmentOption(
                        label = "Before (Bare)",
                        isSelected = uiState.tryOnViewMode == TryOnViewMode.BEFORE,
                        onClick = { onModeChange(TryOnViewMode.BEFORE) },
                        modifier = Modifier.weight(1f)
                    )
                    SegmentOption(
                        label = "Interactive Split",
                        isSelected = uiState.tryOnViewMode == TryOnViewMode.SPLIT,
                        onClick = { onModeChange(TryOnViewMode.SPLIT) },
                        modifier = Modifier.weight(1f)
                    )
                    SegmentOption(
                        label = "Curated Preview",
                        isSelected = uiState.tryOnViewMode == TryOnViewMode.AFTER,
                        onClick = { onModeChange(TryOnViewMode.AFTER) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Shade Selection Row
        Text(
            text = "Select Editorial Shade",
            style = MaterialTheme.typography.headlineSmall,
            color = Ink
        )
        Text(
            text = "Carefully tuned mineral pigments complementing natural skin undertones",
            style = MaterialTheme.typography.bodySmall,
            color = InkMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(BeautyRepository.availableShades) { shade ->
                ShadeSwatchItem(
                    shade = shade,
                    isSelected = shade.id == uiState.selectedShade.id,
                    onClick = { onShadeSelect(shade) }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Desired Finish Selector
        Text(
            text = "Cosmetic Finish Profile",
            style = MaterialTheme.typography.headlineSmall,
            color = Ink
        )

        Spacer(modifier = Modifier.height(8.dp))

        val finishOptions = listOf("Satin Velvet", "Dewy Radiant", "Soft Matte", "Luminous Sheer")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(finishOptions) { finish ->
                EditorialChip(
                    text = finish,
                    isSelected = finish == uiState.selectedFinish,
                    onClick = { onFinishSelect(finish) },
                    testTag = "finish_chip_$finish"
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Occasion Preference
        Text(
            text = "Occasion Context",
            style = MaterialTheme.typography.headlineSmall,
            color = Ink
        )

        Spacer(modifier = Modifier.height(8.dp))

        val occasions = listOf("Studio Editorial", "Golden Hour", "Everyday Soft", "Evening Gala")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(occasions) { occ ->
                EditorialChip(
                    text = occ,
                    isSelected = occ == uiState.selectedOccasion,
                    onClick = { onOccasionSelect(occ) },
                    testTag = "occasion_chip_$occ"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Triggers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryPillButton(
                text = "Save Look",
                onClick = onSaveLook,
                leadingIcon = {
                    Icon(
                        imageVector = if (uiState.activeLook.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = Plum,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.weight(1f),
                testTag = "save_look_action"
            )

            PrimaryPillButton(
                text = "Build Routine",
                onClick = onNavigateToRoutine,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Surface,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.weight(1f),
                testTag = "build_routine_action"
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Look Metadata card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, Border)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.activeLook.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Ink
                    )
                    Text(
                        text = uiState.selectedShade.undertone,
                        style = MaterialTheme.typography.labelSmall,
                        color = OliveTertiary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = uiState.activeLook.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SegmentOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(if (isSelected) Surface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Plum else InkMuted,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// Custom shape to clip a rectangle from startFraction to endFraction horizontally
private class FractionalRectShape(
    private val startFraction: Float,
    private val endFraction: Float
) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {
        val left = size.width * startFraction.coerceIn(0f, 1f)
        val right = size.width * endFraction.coerceIn(0f, 1f)
        val rect = androidx.compose.ui.geometry.Rect(
            left = left,
            top = 0f,
            right = right,
            bottom = size.height
        )
        return androidx.compose.ui.graphics.Outline.Rectangle(rect)
    }
}
