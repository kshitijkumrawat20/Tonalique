package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MetricInsightCard
import com.example.ui.components.PrimaryPillButton
import com.example.ui.theme.Border
import com.example.ui.theme.Canvas
import com.example.ui.theme.Ink
import com.example.ui.theme.InkMuted
import com.example.ui.theme.OliveTertiary
import com.example.ui.theme.Plum
import com.example.ui.theme.RosePale
import com.example.ui.theme.Surface
import com.example.ui.viewmodel.BeautyUiState

@Composable
fun InsightsScreen(
    uiState: BeautyUiState,
    onNavigateToTryOn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Canvas)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("insights_screen")
    ) {
        // Editorial Header
        Text(
            text = "COSMETIC CALIBRATION",
            style = MaterialTheme.typography.labelSmall,
            color = InkMuted,
            letterSpacing = 1.2.sp
        )
        Text(
            text = "Cosmetic Insights",
            style = MaterialTheme.typography.headlineLarge,
            color = Ink
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Honest, non-clinical cosmetic observations designed to guide shade harmony and finish pairing.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Studio Manifesto Banner (Non-judgmental philosophy)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RosePale.copy(alpha = 0.5f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Plum.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Plum,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "No diagnostic scoring or clinical flaw grading. Atelier celebrates your unique undertone and natural texture.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2-Column Grid of Metric Insight Cards
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("insights_grid")
        ) {
            items(uiState.cosmeticInsights) { insight ->
                MetricInsightCard(insight = insight)
            }

            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                // Editorial Finish Harmony Recommendation
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Plum,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Recommended Finish Harmony",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Ink
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Based on your balanced hydration signal and neutral rose undertone, pairing a satin skin tint with pressed velvet plum lips and warm terracotta cheek draping creates maximum dimension under daylight.",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkMuted,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        PrimaryPillButton(
                            text = "Experience Look in Try-On",
                            onClick = onNavigateToTryOn,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "tryon_from_insights_btn"
                        )
                    }
                }
            }
        }
    }
}
