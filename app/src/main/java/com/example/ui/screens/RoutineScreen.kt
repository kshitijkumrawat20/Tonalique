package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.BudgetOption
import com.example.data.model.RoutineStep
import com.example.data.repository.BeautyRepository
import com.example.ui.components.BudgetSelectorRow
import com.example.ui.components.EditorialInputField
import com.example.ui.components.PrimaryPillButton
import com.example.ui.components.SecondaryPillButton
import com.example.ui.theme.Border
import com.example.ui.theme.Canvas
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.Ink
import com.example.ui.theme.InkMuted
import com.example.ui.theme.OliveTertiary
import com.example.ui.theme.Plum
import com.example.ui.theme.PriceDisplayStyle
import com.example.ui.theme.RosePale
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceMuted
import com.example.ui.viewmodel.BeautyUiState

@Composable
fun RoutineScreen(
    uiState: BeautyUiState,
    onBudgetSelect: (BudgetOption) -> Unit,
    onCustomBudgetChange: (String) -> Unit,
    onRemoveProductFromRoutine: (String) -> Unit,
    onSaveToVanity: () -> Unit,
    onExploreProducts: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Canvas)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("routine_screen")
    ) {
        // Editorial Header
        Text(
            text = "PERSONALIZED REGIMEN",
            style = MaterialTheme.typography.labelSmall,
            color = InkMuted,
            letterSpacing = 1.2.sp
        )
        Text(
            text = "Routine & Budget",
            style = MaterialTheme.typography.headlineLarge,
            color = Ink
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Curate your routine steps while aligning with conscious cosmetic spending limits.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Budget Selectors Section
        Text(
            text = "Target Routine Investment",
            style = MaterialTheme.typography.headlineSmall,
            color = Ink
        )
        Spacer(modifier = Modifier.height(8.dp))

        BudgetSelectorRow(
            options = BeautyRepository.budgetOptions,
            selectedOption = uiState.selectedBudget,
            onSelect = onBudgetSelect
        )

        // Custom Budget input if selected
        if (uiState.selectedBudget.id == "b_custom") {
            Spacer(modifier = Modifier.height(8.dp))
            EditorialInputField(
                value = uiState.customBudgetValue,
                onValueChange = onCustomBudgetChange,
                placeholder = "Enter custom budget in ₹ (e.g., 3500)",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(),
                testTag = "custom_budget_input"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Routine Investment Adherence Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("budget_adherence_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL ROUTINE INVESTMENT",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${uiState.routineTotalCost}",
                            style = PriceDisplayStyle,
                            color = Ink
                        )
                    }

                    // Budget adherence badge in OliveTertiary (#596B55)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(if (uiState.isRoutineWithinBudget) OliveTertiary.copy(alpha = 0.12f) else ErrorColor.copy(alpha = 0.12f))
                            .border(
                                1.dp,
                                if (uiState.isRoutineWithinBudget) OliveTertiary else ErrorColor,
                                RoundedCornerShape(9999.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (uiState.isRoutineWithinBudget) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (uiState.isRoutineWithinBudget) OliveTertiary else ErrorColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isRoutineWithinBudget) "Within Budget" else "Over Target",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (uiState.isRoutineWithinBudget) OliveTertiary else ErrorColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (uiState.isRoutineWithinBudget) {
                        "Your selected products fall within your ₹${uiState.effectiveBudgetLimit} target threshold."
                    } else {
                        "Current total exceeds target by ₹${uiState.routineTotalCost - uiState.effectiveBudgetLimit}. Consider alternative sheer glaze options."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (uiState.isRoutineWithinBudget) OliveTertiary else ErrorColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Routine Steps Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Application Sequence",
                style = MaterialTheme.typography.headlineSmall,
                color = Ink
            )

            Text(
                text = "${uiState.routineProducts.size} Products Active",
                style = MaterialTheme.typography.labelSmall,
                color = Plum,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Step Sequence List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("routine_steps_list"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.routineSteps) { step ->
                RoutineStepCard(
                    step = step,
                    onRemove = { step.product?.let { onRemoveProductFromRoutine(it.id) } },
                    onExplore = onExploreProducts
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                PrimaryPillButton(
                    text = "Save Routine to Vanity",
                    onClick = onSaveToVanity,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = Surface,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "save_routine_btn"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun RoutineStepCard(
    step: RoutineStep,
    onRemove: () -> Unit,
    onExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Step Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(RosePale),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.stepIndex}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Plum,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = step.phaseTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Ink
                    )
                }

                if (step.product != null && step.product.isInRoutine) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove product",
                            tint = InkMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Associated Product or Empty slot
            if (step.product != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceMuted.copy(alpha = 0.5f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceMuted)
                    ) {
                        if (step.product.localDrawableRes != null) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = step.product.localDrawableRes),
                                contentDescription = step.product.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (step.product.imageUrl.isNotBlank()) {
                            AsyncImage(
                                model = step.product.imageUrl,
                                contentDescription = step.product.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = step.product.brand.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = InkMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = step.product.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = Ink,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${step.product.shade} · ${step.product.formattedPrice}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Plum
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceMuted.copy(alpha = 0.5f))
                        .clickable(onClick = onExplore)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "No product selected for this phase",
                        style = MaterialTheme.typography.bodySmall,
                        color = InkMuted
                    )
                    Text(
                        text = "+ Browse",
                        style = MaterialTheme.typography.labelMedium,
                        color = Plum,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Technique Tip
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "Studio Technique: ",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = step.techniqueTip,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
