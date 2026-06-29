package com.sandesh.wisespend.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sandesh.wisespend.data.model.Expense
import com.sandesh.wisespend.ui.screens.defaultCategories
import com.sandesh.wisespend.ui.theme.WiseSpendTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun iconForCategory(categoryName: String): ImageVector =
    defaultCategories.find { it.name.equals(categoryName, ignoreCase = true) }?.icon
        ?: Icons.AutoMirrored.Filled.Label

@Composable
fun RecentTransactionsWidget(
    expenses: List<Expense>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (expenses.isNotEmpty()) {
                Text(
                    text = "See all",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp
                )
            }
        }

        if (expenses.isEmpty()) {
            EmptyTransactionsPlaceholder()
        } else {
            expenses.forEachIndexed { index, expense ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    TransactionRow(expense = expense)
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(expense: Expense) {
    val formatter = DateTimeFormatter.ofPattern("MMM dd")
    val displayDate = runCatching {
        LocalDate.parse(expense.date).format(formatter)
    }.getOrDefault(expense.date)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconForCategory(expense.categoryName),
                contentDescription = expense.categoryName,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = "${expense.categoryName} · $displayDate",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }

        // Amount
        Text(
            text = "-${"%.2f".format(expense.amount)}",
            color = MaterialTheme.colorScheme.error,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmptyTransactionsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No transactions yet",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecentTransactionsWidgetPreview() {
    val sampleExpenses = listOf(
        Expense(id = 1, title = "Grocery Shopping", amount = 45.50, categoryName = "Grocery", date = "2023-10-27"),
        Expense(id = 2, title = "Netflix Subscription", amount = 15.99, categoryName = "Entertainment", date = "2023-10-26"),
        Expense(id = 3, title = "Lunch at Cafe", amount = 22.00, categoryName = "Food", date = "2023-10-25")
    )
    WiseSpendTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RecentTransactionsWidget(expenses = sampleExpenses)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecentTransactionsWidgetEmptyPreview() {
    WiseSpendTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RecentTransactionsWidget(expenses = emptyList())
        }
    }
}


