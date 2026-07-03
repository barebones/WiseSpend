package com.sandesh.wisespend.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sandesh.wisespend.ui.theme.WiseSpendTheme

data class DayValue(
    val label: String,
    val value: Int,
    val heightFraction: Float
)

private val MaxBarHeight = 140.dp
private val LabelsRowHeight = 32.dp
private val DotOverlap = 7.dp

@Composable
fun AnalyticsGraph(
    modifier: Modifier = Modifier,
    data: List<DayValue>,
    selectedIndex: Int,
    currencySymbol: String = "रू",
    onBarSelected: (Int) -> Unit
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No expenses yet for this period",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val safeIndex = selectedIndex.coerceIn(0, data.lastIndex)
    val selected = data[safeIndex]

    val animatedSelectedHeight by animateDpAsState(
        targetValue = MaxBarHeight * selected.heightFraction,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "tooltipHeight"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        // Bars + labels, bottom aligned
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaxBarHeight),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEachIndexed { index, day ->
                    BarItem(
                        modifier = Modifier.weight(1f),
                        day = day,
                        isSelected = index == safeIndex,
                        onClick = { onBarSelected(index) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LabelsRowHeight)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                data.forEachIndexed { index, day ->
                    Text(
                        text = day.label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        fontWeight = if (index == safeIndex) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (index == safeIndex)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // pill overlay just above the selected bar's top
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val slotWidth = maxWidth / data.size
            val bubbleBottomPadding = LabelsRowHeight + animatedSelectedHeight - DotOverlap

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(bottom = bubbleBottomPadding),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                data.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (index == safeIndex) {
                            Box(modifier = Modifier.wrapContentWidth(unbounded = true)) {
                                TooltipBubble(spent = "$currencySymbol ${selected.value}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BarItem(
    modifier: Modifier = Modifier,
    day: DayValue,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val targetHeight = MaxBarHeight * day.heightFraction
    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "barHeight"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(animatedHeight)
                    .clip(RoundedCornerShape(38))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
            )
        } else {
            StripedPill(
                modifier = Modifier
                    .width(34.dp)
                    .height(animatedHeight)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
            )
        }
    }
}

@Composable
private fun StripedPill(modifier: Modifier = Modifier) {
    val stripeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
    val bgColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stripeGap = 10.dp.toPx()
            val stripeWidth = 2.dp.toPx()
            var x = -size.height
            while (x < size.width) {
                drawLine(
                    color = stripeColor,
                    start = Offset(x, size.height),
                    end = Offset(x + size.height, 0f),
                    strokeWidth = stripeWidth
                )
                x += stripeGap
            }
        }
    }
}

@Composable
private fun TooltipBubble(spent: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = spent,
                color = MaterialTheme.colorScheme.surface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                softWrap = false
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(12.dp)
                .border(width = 2.dp, color = MaterialTheme.colorScheme.surface, shape = CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalyticsGraphPreview() {
    val sample = listOf(
        DayValue("Sun", 980, 0.42f),
        DayValue("Mon", 720, 0.30f),
        DayValue("Tue", 860, 0.38f),
        DayValue("Wed", 2313, 1f),
        DayValue("Thr", 640, 0.26f),
        DayValue("Fir", 1480, 0.66f),
        DayValue("Sat", 1510, 0.68f)
    )
    WiseSpendTheme {
        AnalyticsGraph(data = sample, selectedIndex = 3, onBarSelected = {})
    }
}