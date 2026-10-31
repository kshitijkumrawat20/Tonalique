package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.BudgetOption
import com.example.data.model.CosmeticInsight
import com.example.data.model.Product
import com.example.data.model.ShadeOption
import com.example.ui.theme.Amber
import com.example.ui.theme.Border
import com.example.ui.theme.Ink
import com.example.ui.theme.InkMuted
import com.example.ui.theme.OliveTertiary
import com.example.ui.theme.Plum
import com.example.ui.theme.PlumPressed
import com.example.ui.theme.PriceDisplayStyle
import com.example.ui.theme.RosePale
import com.example.ui.theme.SoftRose
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceMuted

// Primary Pill Button: Height 52dp, Plum, rounded-full, 150ms pressed transition
@Composable
fun PrimaryPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    testTag: String = "primary_pill_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val bgColor by animateColorAsState(
        targetValue = when {
            !enabled -> InkMuted.copy(alpha = 0.4f)
            isPressed -> PlumPressed
            else -> Plum
        },
        animationSpec = tween(150),
        label = "btn_bg"
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .height(52.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = Surface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Secondary Pill Button: Height 52dp, Surface background, 1px solid border, ink text
@Composable
fun SecondaryPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    testTag: String = "secondary_pill_button"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(52.dp)
            .clip(CircleShape)
            .background(Surface)
            .border(1.dp, Border, CircleShape)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = Ink,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Ghost Button: Borderless, min 48dp touch target, plum text
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "ghost_button"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .heightIn(min = 48.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Plum,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Chips & Preference Selectors:
// Unselected: Surface, 1px Border, ink-muted, height 38dp, pill
// Selected: rose-pale, 1px Plum border, Plum text, SemiBold
@Composable
fun EditorialChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    testTag: String = "editorial_chip"
) {
    val bgColor = if (isSelected) RosePale else Surface
    val borderColor = if (isSelected) Plum else Border
    val textColor = if (isSelected) Plum else InkMuted

    Box(
        modifier = modifier
            .testTag(testTag)
            .height(38.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Plum,
                    modifier = Modifier.size(16.dp).padding(end = 4.dp)
                )
            } else if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

// Product Recommendation Card per specification:
// Container: Surface, 1px solid Border, corner radius 20dp, padding 16dp
// Image: 1:1 aspect ratio, corner radius 14dp, background surface-muted
// Metadata Stack: Brand (label-sm ink-muted), Product Name (headline-sm ink), Shade & Finish (body-sm)
// Reason Tag: Inset tag in surface-muted with ink typography
// Price Block: price-display, timestamp "Price verified today"
@Composable
fun ProductRecommendationCard(
    product: Product,
    onCardClick: () -> Unit,
    onToggleRoutine: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag("product_card_${product.id}")
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Product Image Container (1:1 ratio, 14dp radius, surface-muted)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceMuted)
            ) {
                if (product.localDrawableRes != null) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = product.localDrawableRes),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (product.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Heart action button overlay on top right
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .background(Surface.copy(alpha = 0.85f), CircleShape)
                        .testTag("favorite_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite ${product.name}",
                        tint = if (product.isFavorite) Plum else InkMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metadata Stack
            Text(
                text = product.brand.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = InkMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                color = Ink
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${product.shade} · ${product.finish}",
                style = MaterialTheme.typography.bodySmall,
                color = InkMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Reason Tag: Inset in surface-muted with ink typography
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceMuted)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = product.reasonTag,
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Price Block & Routine Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = product.formattedPrice,
                        style = PriceDisplayStyle,
                        color = Ink
                    )
                    Text(
                        text = product.verifiedTimestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = OliveTertiary
                    )
                }

                // Quick Add/Remove Routine Button
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(CircleShape)
                        .background(if (product.isInRoutine) RosePale else Surface)
                        .border(1.dp, if (product.isInRoutine) Plum else Border, CircleShape)
                        .clickable(onClick = onToggleRoutine)
                        .padding(horizontal = 14.dp)
                        .testTag("toggle_routine_${product.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (product.isInRoutine) Icons.Default.Check else Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = if (product.isInRoutine) Plum else Ink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (product.isInRoutine) "In Routine" else "Add",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (product.isInRoutine) Plum else Ink,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// Metric Insight Card: 2-column grid card, Surface background, 1px Border, radius 20dp, padding 14dp
// Tone: Non-clinical cosmetic observations without numerical score gauges or grading colors
@Composable
fun MetricInsightCard(
    insight: CosmeticInsight,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag("insight_card_${insight.id}")
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Category Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(SurfaceMuted)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = insight.categoryTag,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = insight.title,
                style = MaterialTheme.typography.labelMedium,
                color = InkMuted
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = insight.value,
                style = MaterialTheme.typography.headlineSmall,
                color = Plum,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = insight.cosmeticNote,
                style = MaterialTheme.typography.bodySmall,
                color = Ink,
                lineHeight = 16.sp
            )
        }
    }
}

// Editorial Text Input Container: Height 52dp, 14dp radius, Surface, 1px Border (or 2px Plum when focused)
@Composable
fun EditorialInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onDone: (() -> Unit)? = null,
    testTag: String = "editorial_input"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(52.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = InkMuted.copy(alpha = 0.7f)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Ink),
            cursorBrush = SolidColor(Plum),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Budget Quick Chips: Horizontally scrollable row of pill options (₹750, ₹1,500, ₹2,500, Custom)
@Composable
fun BudgetSelectorRow(
    options: List<BudgetOption>,
    selectedOption: BudgetOption,
    onSelect: (BudgetOption) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { option ->
            val isSelected = option.id == selectedOption.id
            EditorialChip(
                text = option.label,
                isSelected = isSelected,
                onClick = { onSelect(option) },
                testTag = "budget_chip_${option.id}"
            )
        }
    }
}

// Circular Shade Swatch with active inner outline & soft tinted ring
@Composable
fun ShadeSwatchItem(
    shade: ShadeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp)
            .testTag("shade_swatch_${shade.id}")
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isSelected) RosePale else Color.Transparent)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(shade.hexColor))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Plum else Border,
                        shape = CircleShape
                    )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = shade.name,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Plum else InkMuted,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 11.sp
        )
    }
}
