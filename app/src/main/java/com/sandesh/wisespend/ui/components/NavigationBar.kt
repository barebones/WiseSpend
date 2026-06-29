package com.sandesh.wisespend.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sandesh.wisespend.NavItems
import com.sandesh.wisespend.ui.theme.WiseSpendTheme
import kotlinx.coroutines.launch
import kotlin.math.abs

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun WiseNavigationBar(
    items: List<NavItems>,
    pagePosition: Float,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    val primary = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .shadow(
                    elevation = 8.dp, // Reduced for a clean, non-glass look
                    shape = RoundedCornerShape(30.dp)
                )
                .clip(RoundedCornerShape(30.dp))
                .background(surface) // Solid surface background
                .border(1.dp, outlineVariant, RoundedCornerShape(30.dp))
        ) {
            val count = items.size
            val horizontalPad = 10.dp
            val verticalPad = 10.dp
            val capsuleCorner = 22.dp

            SlidingCapsule(
                pagePosition = pagePosition,
                tabCount = count,
                horizontalPad = horizontalPad,
                verticalPad = verticalPad,
                cornerRadius = capsuleCorner,
                capsuleColor = primary,
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPad, vertical = verticalPad),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items.forEachIndexed { index, item ->
                    val fraction = 1f - minOf(1f, abs(pagePosition - index))

                    ExpandingNavTab(
                        item = item,
                        selectionFraction = fraction,
                        activeColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveColor = onSurfaceVariant.copy(alpha = 0.7f),
                        onClick = { onSelect(index) },
                        modifier = Modifier.weight(1f + 0.50f * fraction)
                    )
                }
            }
        }
    }
}

@Composable
private fun SlidingCapsule(
    pagePosition: Float,
    tabCount: Int,
    horizontalPad: Dp,
    verticalPad: Dp,
    cornerRadius: Dp,
    capsuleColor: Color,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val animatedPosition by animateFloatAsState(
        targetValue = pagePosition,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "capsulePos"
    )

    val hPadPx = with(density) { horizontalPad.toPx() }
    val vPadPx = with(density) { verticalPad.toPx() }
    val cornerPx = with(density) { cornerRadius.toPx() }
    val gapPx = with(density) { 6.dp.toPx() }

    Box(
        modifier = modifier.drawBehind {
            val availableW = size.width - hPadPx * 2
            val totalGap = gapPx * (tabCount - 1)

            val expandFactor = 0.50f
            val baseWeight = 1f
            val weights = (0 until tabCount).map { i ->
                val frac = 1f - minOf(1f, abs(animatedPosition - i))
                baseWeight + expandFactor * frac
            }
            val totalWeight = weights.sum()

            val leftIdx = animatedPosition.toInt().coerceIn(0, tabCount - 1)
            val rightIdx = (leftIdx + 1).coerceIn(0, tabCount - 1)

            fun capsuleXAndWidth(idx: Int): Pair<Float, Float> {
                var x = hPadPx
                for (i in 0 until idx) {
                    x += (availableW - totalGap) * (weights[i] / totalWeight) + gapPx
                }
                val w = (availableW - totalGap) * (weights[idx] / totalWeight)
                return x to w
            }

            val (lx, lw) = capsuleXAndWidth(leftIdx)
            val (rx, rw) = capsuleXAndWidth(rightIdx)

            val frac = animatedPosition - animatedPosition.toInt()
            val cx = lx + (rx - lx) * frac
            val cw = lw + (rw - lw) * frac

            val height = size.height - vPadPx * 2

            drawRoundRect(
                color = capsuleColor,
                topLeft = Offset(cx, vPadPx),
                size = Size(cw, height),
                cornerRadius = CornerRadius(cornerPx, cornerPx)
            )
        }
    )
}

@Composable
private fun ExpandingNavTab(
    item: NavItems,
    selectionFraction: Float,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val bounceScale = remember { Animatable(1f) }

    val iconTint by animateColorAsState(
        targetValue = lerp(inactiveColor, activeColor, selectionFraction),
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "iconTint"
    )

    val labelAlpha by animateFloatAsState(
        targetValue = selectionFraction,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "labelAlpha"
    )

    val iconScale by animateFloatAsState(
        targetValue = 1f + 0.10f * selectionFraction,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconScale"
    )

    val spacerWidth by animateFloatAsState(
        targetValue = 8f * selectionFraction,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "spacer"
    )

    val labelWidth by animateFloatAsState(
        targetValue = 72f * selectionFraction,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "labelW"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(22.dp))
            .graphicsLayer {
                scaleX = bounceScale.value
                scaleY = bounceScale.value
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // fire bounce
                scope.launch {
                    bounceScale.animateTo(
                        targetValue = 0.88f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    )
                    bounceScale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconTint,
                modifier = Modifier
                    .size(22.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
            )

            Spacer(modifier = Modifier.width(spacerWidth.dp))

            Box(
                modifier = Modifier
                    .width(labelWidth.dp)
                    .clipToBounds(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = item.label,
                    maxLines = 1,
                    softWrap = false,
                    color = activeColor.copy(alpha = labelAlpha),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

private fun lerp(start: Color, end: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (end.red - start.red) * f,
        green = start.green + (end.green - start.green) * f,
        blue = start.blue + (end.blue - start.blue) * f,
        alpha = start.alpha + (end.alpha - start.alpha) * f
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun WiseNavigationBarPreview() {
    WiseSpendTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            WiseNavigationBar(
                items = listOf(
                    NavItems("Home", Icons.Default.Home),
                    NavItems("Settings", Icons.Default.Settings),
                    NavItems("Home", Icons.Default.Home),
                    NavItems("Settings", Icons.Default.Settings)
                ),
                pagePosition = 0f,
                onSelect = {}
            )
        }
    }
}