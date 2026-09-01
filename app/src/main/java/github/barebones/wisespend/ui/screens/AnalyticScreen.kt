package github.barebones.wisespend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Lucide
import github.barebones.wisespend.data.model.Expense
import github.barebones.wisespend.ui.components.AnalyticsWidgetContent
import github.barebones.wisespend.ui.components.RecentTransactionsWidget
import github.barebones.wisespend.ui.components.TransactionsMode
import github.barebones.wisespend.util.AnalyticsRange
import github.barebones.wisespend.util.CurrencyUtils
import github.barebones.wisespend.viewmodel.ExpenseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters


@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    onNavigateToSavings: () -> Unit,
    expenseViewModel: ExpenseViewModel = viewModel()
) {
    val expenses by expenseViewModel.allExpenses.collectAsStateWithLifecycle(initialValue = emptyList())
    val currencyCode by expenseViewModel.currencyCode.collectAsStateWithLifecycle()
    val currency = remember(currencyCode) { CurrencyUtils.getCurrencyByCode(currencyCode) }

    AnalyticsScreenContent(
        modifier = modifier,
        expenses = expenses,
        currencySymbol = currency.symbol,
        onNavigateToSavings = onNavigateToSavings
    )
}

@Composable
fun AnalyticsScreenContent(
    modifier: Modifier = Modifier,
    expenses: List<Expense>,
    currencySymbol: String = "रू",
    onNavigateToSavings: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Analytics",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(
                        onClick = onNavigateToSavings,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(
                            imageVector = Lucide.History,
                            contentDescription = "Budget History",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        var selectedRange by remember { mutableStateOf(AnalyticsRange.WEEK) }
        var selectedIndex by remember { mutableIntStateOf(-1) }

        val filteredExpenses = remember(expenses, selectedRange, selectedIndex) {
            if (selectedIndex == -1) return@remember emptyList<Expense>()

            val today = LocalDate.now()
            val targetDate = when (selectedRange) {
                AnalyticsRange.DAY -> today
                AnalyticsRange.WEEK -> {
                    val daysSinceSunday = today.dayOfWeek.value % 7
                    val sunday = today.minusDays(daysSinceSunday.toLong())
                    sunday.plusDays(selectedIndex.toLong())
                }
                AnalyticsRange.MONTH -> {
                    // This is trickier because of week buckets.
                    // for simplicity, i'll just filter if the date falls in that week for now.
                    null
                }
                AnalyticsRange.YEAR -> {
                    null
                }
            }

            if (targetDate != null) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val targetDateString = targetDate.format(formatter)
                expenses.filter {
                    // try to match both yyyy-MM-dd and yyyy-M-d
                    it.date == targetDateString || it.date == "${targetDate.year}-${targetDate.monthValue}-${targetDate.dayOfMonth}"
                }
            } else if (selectedRange == AnalyticsRange.YEAR) {
                expenses.filter {
                    val d = runCatching { LocalDate.parse(it.date, DateTimeFormatter.ofPattern("yyyy-M-d")) }.getOrNull()
                    d?.year == today.year && d.monthValue == (selectedIndex + 1)
                }
            } else if (selectedRange == AnalyticsRange.MONTH) {
                val firstOfMonth = today.withDayOfMonth(1)
                val lastOfMonth = today.withDayOfMonth(today.lengthOfMonth())
                val weekBuckets = mutableListOf<Pair<LocalDate, LocalDate>>()
                var cursor = firstOfMonth
                while (!cursor.isAfter(lastOfMonth)) {
                    val weekEnd = minOf(cursor.plusDays(6), lastOfMonth)
                    weekBuckets.add(cursor to weekEnd)
                    cursor = weekEnd.plusDays(1)
                }
                if (selectedIndex in weekBuckets.indices) {
                    val (start, end) = weekBuckets[selectedIndex]
                    expenses.filter {
                        val d = runCatching { LocalDate.parse(it.date, DateTimeFormatter.ofPattern("yyyy-M-d")) }.getOrNull()
                        d != null && !d.isBefore(start) && !d.isAfter(end)
                    }
                } else emptyList()
            } else {
                emptyList()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            AnalyticsWidgetContent(
                expenses = expenses,
                currencySymbol = currencySymbol,
                onSelectionChanged = { range, index ->
                    selectedRange = range
                    selectedIndex = index
                }
            )

            Spacer(modifier = Modifier.size(24.dp))

            RecentTransactionsWidget(
                mode = TransactionsMode.DAILY,
                expenses = filteredExpenses,
                modifier = Modifier,
                onNavigateToAllExpenses = null,
                filterInternally = false,
                currencySymbol = currencySymbol,
                onDeleteExpense = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    val expenses: List<Expense> = listOf(
        Expense(
            id = 1,
            title = "Grocery Shopping",
            amount = 45.50,
            categoryName = "Grocery",
            date = "2026-07-01",
            time = "10:30"
        ),
        Expense(
            id = 2,
            title = "Movie tickets",
            amount = 9.99,
            categoryName = "Entertainment",
            date = "2026-07-01",
            time = "18:45"
        ),

    )
    AnalyticsScreenContent(expenses = expenses)
}