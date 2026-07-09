package github.barebones.wisespend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import github.barebones.wisespend.data.model.Expense
import github.barebones.wisespend.ui.components.TransactionRow
import github.barebones.wisespend.ui.components.TransactionsMode
import github.barebones.wisespend.util.CurrencyUtils
import github.barebones.wisespend.viewmodel.ExpenseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllExpensesScreen(
    onBack: () -> Unit,
    viewModel: ExpenseViewModel = viewModel()
) {
    val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()
    val currency = remember(currencyCode) { CurrencyUtils.getCurrencyByCode(currencyCode) }

    AllExpensesContent(
        expenses = expenses,
        currencySymbol = currency.symbol,
        onBack = onBack,
        onDeleteExpense = { viewModel.deleteExpense(it) }
    )

}

@Composable
fun AllExpensesContent(
    modifier: Modifier = Modifier,
    expenses: List<Expense>,
    currencySymbol: String = "$",
    onBack: () -> Unit,
    onDeleteExpense: (Expense) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    val filteredExpenses = remember(expenses, searchQuery) {
        if (searchQuery.isEmpty()) {
            expenses
        } else {
            expenses.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.categoryName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val groupedExpenses = remember(filteredExpenses) {
        filteredExpenses.groupBy { it.date }.toSortedMap(compareByDescending { it })
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "All Expenses",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Lucide.ChevronLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->


        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Search by title or category",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (filteredExpenses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isEmpty()) "No expenses recorded yet" else "No matching expenses found for $searchQuery",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    groupedExpenses.forEach { (date, expensesForDate) ->
                        item {
                            Text(
                                text = formatDateHeader(date),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        items(expensesForDate, key = { it.id }) { expense ->
                            TransactionRow(
                                mode = TransactionsMode.RECENT,
                                expense = expense,
                                currencySymbol = currencySymbol,
                                onDelete = {
                                    expenseToDelete = it
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
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

private fun formatDateHeader(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        when (date) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault()))
        }
    } catch (_: Exception) {
        dateString
    }
}

@Preview(showBackground = true)
@Composable
fun AllExpensesScreenPreview() {
    val sampleExpenses = listOf(
        Expense(
            id = 1,
            title = "Lunch",
            amount = 12.5,
            categoryName = "Food",
            date = LocalDate.now().toString()
        ),
        Expense(
            id = 2,
            title = "Bus",
            amount = 2.0,
            categoryName = "Transport",
            date = LocalDate.now().toString()
        ),
        Expense(
            id = 3,
            title = "Grocery",
            amount = 45.0,
            categoryName = "Shopping",
            date = LocalDate.now().minusDays(1).toString()
        ),
        Expense(
            id = 4,
            title = "Movie",
            amount = 15.0,
            categoryName = "Entertainment",
            date = "2023-10-20"
        )
    )
    MaterialTheme {
        AllExpensesContent(
            onBack = {},
            expenses = sampleExpenses,
            currencySymbol = "$",
            onDeleteExpense = {},
        )
    }
}
