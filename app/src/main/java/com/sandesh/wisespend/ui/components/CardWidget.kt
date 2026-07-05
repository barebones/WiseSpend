package com.sandesh.wisespend.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sandesh.wisespend.ui.screens.TextGray
import com.sandesh.wisespend.ui.theme.WiseSpendTheme
import com.sandesh.wisespend.ui.utils.TestTags
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CardWidget(
    modifier: Modifier = Modifier,
    budget: Double,
    spent: Double,
    currencySymbol: String = "रू",
    onSetBudget: (Double) -> Unit
) {

    var isBalanceVisible by remember { mutableStateOf(true) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    val available = (budget - spent).coerceAtLeast(0.0)
    val displayText = if (isBalanceVisible) "$currencySymbol ${"%.0f".format(available)}" else "$currencySymbol •••••"

    if (showBudgetDialog) {
        SetBudgetDialog(
            currentBudget = budget,
            currencySymbol = currencySymbol,
            onDismiss = { showBudgetDialog = false },
            onConfirm = { newBudget ->
                onSetBudget(newBudget)
                showBudgetDialog = false
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Balance",
                    fontSize = 13.sp,
                    letterSpacing = 0.4.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .clickable { showBudgetDialog = true }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag(TestTags.SET_BUDGET_BTN)
                ) {
                    Text(
                        text = "Set Budget",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row {
                Text(
                    text = displayText,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = {
                        isBalanceVisible = !isBalanceVisible
                    },
                    Modifier
                        .background(Color.Transparent)
                        .testTag(TestTags.BALANCE_VISIBILITY_TOGGLE)
                ) {
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Eye",
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BudgetProgress(
                modifier = Modifier.fillMaxWidth(),
                spent = spent.toFloat(),
                total = budget.toFloat(),
                currencySymbol = currencySymbol,
            )
            }


        }
    }
}

@Composable
fun SetBudgetDialog(
    currentBudget: Double,
    currencySymbol: String = "₹",
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var input by remember {
        mutableStateOf(
            if (currentBudget > 0) currentBudget.toInt().toString() else ""
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Set Monthly Budget",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.onSurface.copy(0.15f),
                            RoundedCornerShape(16.dp)
                        )
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "  $currencySymbol", color = TextGray, fontSize = 16.sp)
                    TextField(
                        value = input,
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { it.isDigit() || it == '.' }
                            val parts = filtered.split('.')
                            val isWithinLimits = when {
                                parts.size == 1 -> parts[0].length <= 9
                                parts.size == 2 -> parts[0].length <= 9 && parts[1].length <= 2
                                else -> false
                            }
                            if (isWithinLimits && filtered.length <= 12) {
                                input = filtered
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(TestTags.BUDGET_DIALOG_INPUT),
                        singleLine = true,
                        placeholder = {
                            Text("Enter budget (e.g. 5000)")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,

                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,

                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Cancel", color = TextGray) }

                    Button(
                        onClick = {
                            input.toDoubleOrNull()?.let { 
                                if (it > 0) onConfirm(it) 
                            }
                        },
                        enabled = (input.toDoubleOrNull() ?: 0.0) > 0.0,
                        modifier = Modifier
                            .weight(1f)
                            .testTag(TestTags.BUDGET_DIALOG_SAVE),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Save", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetProgress(
    modifier: Modifier = Modifier,
    spent: Float,
    total: Float,
    currencySymbol: String = "₹",
) {
    val progress = if (total > 0f) {
        (spent / total).coerceIn(0f, 1f)
    } else {
        0f
    }

    var targetProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    LaunchedEffect(spent, total) {
        // Go little further than actual progress value then come back
        // for smooooooth animation
        targetProgress = (progress * 1.3f).coerceAtMost(1f)
        delay(300.milliseconds)
        targetProgress = progress
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Spent",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Out of",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$currencySymbol${spent.toInt()}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53935),
                fontSize = 15.sp
            )

            Text(
                text = "$currencySymbol${total.toInt()}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF43A047),
                fontSize = 15.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .height(16.dp)
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.onSurface.copy(.1f),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(3.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surface,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CardWidgetPreview() {
    WiseSpendTheme {
        CardWidget(
            Modifier,
            budget = 1000.00,
            spent = 100.12,
            onSetBudget = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SetBudgetDialogPreview() {
    WiseSpendTheme {
        SetBudgetDialog(
            currentBudget = 0.0,
            onDismiss = {},
            onConfirm = {}
        )
    }
}