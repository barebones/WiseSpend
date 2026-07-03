package com.sandesh.wisespend.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sandesh.wisespend.data.model.Expense
import com.sandesh.wisespend.ui.components.AnalyticsWidgetContent
import com.sandesh.wisespend.viewmodel.ExpenseViewModel


@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    expenseViewModel: ExpenseViewModel = viewModel()
) {
    val expenses by expenseViewModel.allExpenses.collectAsStateWithLifecycle(initialValue = emptyList())
    AnalyticsScreenContent(modifier = modifier, expenses = expenses)
}

@Composable
fun AnalyticsScreenContent(
    modifier: Modifier = Modifier,
    expenses: List<Expense>
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
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            AnalyticsWidgetContent(expenses = expenses)

            Spacer(modifier = Modifier.size(24.dp))

//            TODO: Implement till next release
//            RecentTransactionsWidget(
//                mode = TransactionsMode.DAILY,
//                expenses = expenses,
//                modifier = Modifier
//            )
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
            date = "2026-7-1"
        ),
        Expense(
            id = 2,
            title = "Movie tickets",
            amount = 9.99,
            categoryName = "Entertainment",
            date = "2026-7-1"
        ),

    )
    AnalyticsScreenContent(expenses = expenses)
}