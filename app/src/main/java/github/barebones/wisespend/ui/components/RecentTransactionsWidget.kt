package github.barebones.wisespend.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.composables.icons.lucide.CalendarDays
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import github.barebones.wisespend.data.model.Expense
import github.barebones.wisespend.ui.screens.defaultCategories
import github.barebones.wisespend.ui.theme.WiseSpendTheme
import github.barebones.wisespend.ui.utils.TestTags
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class TransactionsMode {
    RECENT,
    DAILY
}

fun iconForCategory(categoryName: String): ImageVector =
    defaultCategories.find { it.name.equals(categoryName, ignoreCase = true) }?.icon
        ?: Icons.AutoMirrored.Filled.Label

@Composable
fun RecentTransactionsWidget(
    modifier: Modifier = Modifier,
    expenses: List<Expense>,
    mode: TransactionsMode = TransactionsMode.RECENT,
    filterInternally: Boolean = true,
    currencySymbol: String = "$",
    onDeleteExpense: (Expense) -> Unit = {}
) {
    val todayDateString = remember {
        LocalDate.now(ZoneId.systemDefault()).toString()
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

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
            EmptyTransactionsPlaceholder(Modifier.testTag(TestTags.EMPTY_TRANSACTIONS))
        } else {
            displayedExpenses.forEachIndexed { _, expense ->
                key(expense.id) {

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                    ) {
                        TransactionRow(
                            mode = mode,
                            expense = expense,
                            currencySymbol = currencySymbol,
                            onDelete = {
                                expenseToDelete = it
                                showDeleteDialog = true
                            },
                            modifier = Modifier.testTag("${TestTags.TRANSACTION_ITEM}${expense.id}")
                        )
                    }
                }
            }
        }

        if (showDeleteDialog && expenseToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    expenseToDelete = null
                },
                title = { Text("Delete Transaction") },
                text = { Text("Are you sure you want to delete this transaction?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            expenseToDelete?.let { onDeleteExpense(it) }
                            showDeleteDialog = false
                            expenseToDelete = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        expenseToDelete = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun TransactionRow(
    modifier: Modifier = Modifier,
    mode: TransactionsMode,
    expense: Expense,
    currencySymbol: String = "$",
    onDelete: (Expense) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMM dd")
    val displayDate = runCatching {
        LocalDate.parse(expense.date).format(formatter)
    }.getOrDefault(expense.date)

    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val displayTime = runCatching {
        if (expense.time != null) {
            val localTime = java.time.LocalTime.parse(expense.time)
            localTime.format(timeFormatter)
        } else {
            val instant = Instant.ofEpochMilli(expense.createdAt)
            val dateTime = instant.atZone(ZoneId.systemDefault())
            dateTime.format(timeFormatter)
        }
    }.getOrDefault("Invalid Time")

//    swipe to delete
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete(expense)
            dismissState.reset()
        }
    }


    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = when (dismissState.targetValue) {
                SwipeToDismissBoxValue.Settled-> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Lucide.Trash2,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    ) {
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
                else {
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
}

@Composable
private fun EmptyTransactionsPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
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
            date = "2023-10-27",
            time = "10:30"
        ),
        Expense(
            id = 2,
            title = "Netflix Subscription",
            amount = 15.99,
            categoryName = "Entertainment",
            date = "2023-10-26",
            time = "20:00"
        ),
        Expense(
            id = 3,
            title = "Lunch at Cafe",
            amount = 22.00,
            categoryName = "Food",
            date = "2023-10-25",
            time = "13:15"
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


