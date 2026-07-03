package com.sandesh.wisespend.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.CalendarDays
import com.composables.icons.lucide.Lucide
import com.sandesh.wisespend.data.model.Expense
import com.sandesh.wisespend.ui.screens.defaultCategories
import com.sandesh.wisespend.ui.theme.WiseSpendTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class TransactionsMode{
    RECENT,
    DAILY
}

fun iconForCategory(categoryName: String): ImageVector =
    defaultCategories.find { it.name.equals(categoryName, ignoreCase = true) }?.icon
        ?: Icons.AutoMirrored.Filled.Label

@Composable
fun RecentTransactionsWidget(
    expenses: List<Expense>,
    mode: TransactionsMode = TransactionsMode.RECENT,
    filterInternally: Boolean = true,
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    val todayDateString = remember {
        LocalDate.now(ZoneId.systemDefault()).toString()
    }

    val displayedExpenses = remember(expenses, mode, filterInternally) {
        if (mode == TransactionsMode.DAILY && filterInternally) {
            expenses.filter { it.date == todayDateString }
        } else {
            expenses
        }
    }

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
                text = if (mode == TransactionsMode.DAILY) "Today's Transactions" else "Recent Transactions",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
//            TODO: add see all page to list all expenses with filters
//            if (expenses.isNotEmpty()) {
//                Text(
//                    text = "See all",
//                    color = MaterialTheme.colorScheme.primary,
//                    fontSize = 13.sp
//                )
//            }
        }

        if (displayedExpenses.isEmpty()) {
            EmptyTransactionsPlaceholder()
        } else {
            displayedExpenses.forEachIndexed { _, expense ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    TransactionRow(mode = mode, expense = expense, currencySymbol = currencySymbol)
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    mode: TransactionsMode,
    expense: Expense,
    currencySymbol: String = "$"
) {
    val formatter = DateTimeFormatter.ofPattern("MMM dd")
    val displayDate = runCatching {
        LocalDate.parse(expense.date).format(formatter)
    }.getOrDefault(expense.date)

    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val displayTime = runCatching {
        val instant = Instant.ofEpochMilli(expense.createdAt)
        val dateTime = instant.atZone(ZoneId.systemDefault())
        dateTime.format(timeFormatter)
    }.getOrDefault("Invalid Time")

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
            if (mode == TransactionsMode.RECENT)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Lucide.CalendarDays,
                    contentDescription = "calendar icon",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = displayDate,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
            else{
                Text(
                    text = "Today",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Amount and time
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "-$currencySymbol${"%.2f".format(expense.amount)}",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayTime,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
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
        Expense(
            id = 1,
            title = "Grocery Shopping",
            amount = 45.50,
            categoryName = "Grocery",
            date = "2023-10-27"
        ),
        Expense(
            id = 2,
            title = "Netflix Subscription",
            amount = 15.99,
            categoryName = "Entertainment",
            date = "2023-10-26"
        ),
        Expense(
            id = 3,
            title = "Lunch at Cafe",
            amount = 22.00,
            categoryName = "Food",
            date = "2023-10-25"
        )
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


