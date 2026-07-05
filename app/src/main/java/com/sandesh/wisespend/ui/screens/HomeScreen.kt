package com.sandesh.wisespend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.testTag
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Lucide
import com.sandesh.wisespend.data.model.Expense
import com.sandesh.wisespend.ui.components.CardWidget
import com.sandesh.wisespend.ui.components.GreetingHeader
import com.sandesh.wisespend.ui.components.RecentTransactionsWidget
import com.sandesh.wisespend.ui.theme.WiseSpendTheme
import com.sandesh.wisespend.ui.utils.TestTags
import com.sandesh.wisespend.util.CurrencyUtils
import com.sandesh.wisespend.viewmodel.ExpenseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToNotifications:() -> Unit,
    snackbarHostState: SnackbarHostState,
    expenseViewModel: ExpenseViewModel = viewModel()
) {
    val recentExpenses by expenseViewModel.recentExpenses.collectAsStateWithLifecycle()
    val budget by expenseViewModel.budget.collectAsStateWithLifecycle()
    val userName by expenseViewModel.userName.collectAsStateWithLifecycle()
    val totalSpent by expenseViewModel.totalSpent.collectAsStateWithLifecycle()
    val currencyCode by expenseViewModel.currencyCode.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    HomeScreenContent(
        modifier = modifier,
        onNavigateToNotifications = onNavigateToNotifications,
        recentExpenses = recentExpenses,
        budget = budget,
        userName = userName,
        totalSpent = totalSpent,
        currencyCode = currencyCode,
        onSetUsername = { expenseViewModel.setUsername(it) },
        onSetBudget = { expenseViewModel.setBudget(it) },
        onDeleteExpense = { expenseViewModel.deleteExpense(it) },
        onUndoDelete = { expenseViewModel.addExpense(it) },
        snackbarHostState = snackbarHostState,
        scope = scope
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    onNavigateToNotifications: () -> Unit,
    recentExpenses: List<Expense>,
    budget: Double,
    userName: String,
    totalSpent: Double,
    currencyCode: String,
    onSetUsername: (String) -> Unit,
    onSetBudget: (Double) -> Unit,
    onDeleteExpense: (Expense) -> Unit,
    onUndoDelete: (Expense) -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.testTag(TestTags.HOME_SCREEN_ROOT),
        topBar = {
            TopAppBar(
                title = { 
                    GreetingHeader(userName, Modifier) {
                        onSetUsername(it)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(
                        onClick = {
                            onNavigateToNotifications()
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .testTag(TestTags.NOTIFICATION_BUTTON)
                    ) {
                        Icon(
                            imageVector = Lucide.Bell,
                            contentDescription = "Notification",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            val currencySymbol = CurrencyUtils.getCurrencyByCode(currencyCode).symbol
            CardWidget(
                budget = budget,
                spent = totalSpent,
                currencySymbol = currencySymbol,
                onSetBudget = { onSetBudget(it) },
                modifier = Modifier.testTag(TestTags.BUDGET_CARD)
            )
            Spacer(modifier = Modifier.size(24.dp))
            RecentTransactionsWidget(
                expenses = recentExpenses,
                currencySymbol = currencySymbol,
                onDeleteExpense = { expense ->
                    onDeleteExpense(expense)
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Transaction deleted",
                            actionLabel = "Undo"
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            onUndoDelete(expense)
                        }
                    }
                },
                modifier = Modifier.testTag(TestTags.RECENT_TRANSACTIONS)
            )

            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WiseSpendTheme {
        HomeScreenContent(
            onNavigateToNotifications = {},
            recentExpenses = emptyList(),
            budget = 1000.0,
            userName = "Sandesh",
            totalSpent = 250.0,
            currencyCode = "NPR",
            onSetUsername = {},
            onSetBudget = {},
            onDeleteExpense = {},
            onUndoDelete = {},
            snackbarHostState = SnackbarHostState(),
            scope = rememberCoroutineScope()
        )
    }
}
