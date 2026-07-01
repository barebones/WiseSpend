package com.sandesh.wisespend.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sandesh.wisespend.data.model.Expense
import com.sandesh.wisespend.ui.theme.WiseSpendTheme
import com.sandesh.wisespend.util.AnalyticsCalculator
import com.sandesh.wisespend.util.AnalyticsRange

@Composable
fun AnalyticsWidgetContent(
    modifier: Modifier = Modifier,
    expenses: List<Expense>
) {
    var selectedRange by remember { mutableStateOf(AnalyticsRange.WEEK) }
    var manuallySelectedIndex by remember { mutableIntStateOf(-1) }

    val analytics = remember(expenses, selectedRange) {
        AnalyticsCalculator.compute(expenses, selectedRange)
    }

    val selectedIndex = if (manuallySelectedIndex in analytics.points.indices) {
        manuallySelectedIndex
    } else {
        analytics.selectedIndex
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Activity",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Total Spending",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // TODO implement filter by days/week/month/year in future
//                Box {
//                    Row(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(50))
//                            .background(MaterialTheme.colorScheme.surface)
//                            .clickable { menuExpanded = true }
//                            .padding(horizontal = 16.dp, vertical = 10.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = selectedRange.label,
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                        Icon(
//                            imageVector = Icons.Default.KeyboardArrowDown,
//                            contentDescription = "Select time range",
//                            modifier = Modifier.padding(start = 4.dp),
//                            tint = MaterialTheme.colorScheme.onSurface
//                        )
//                    }
//
//                    DropdownMenu(
//                        expanded = menuExpanded,
//                        onDismissRequest = { menuExpanded = false },
//                        modifier = Modifier.clip(MaterialTheme.shapes.medium)
//                    ) {
//                        AnalyticsRange.entries.forEach { option ->
//                            DropdownMenuItem(
//                                text = { Text(option.label) },
//                                onClick = {
//                                    selectedRange = option
//                                    manuallySelectedIndex = -1
//                                    menuExpanded = false
//                                }
//                            )
//                        }
//                    }
//                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "Total ₹${"%.0f".format(analytics.total)}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "  •  Avg ₹${"%.0f".format(analytics.average)}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnalyticsGraph(
                modifier = Modifier.padding(top = 12.dp),
                data = analytics.points,
                selectedIndex = selectedIndex,
                onBarSelected = { manuallySelectedIndex = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalyticsWidgetPreview() {
    WiseSpendTheme {
    AnalyticsWidgetContent(expenses = emptyList())
    }
}