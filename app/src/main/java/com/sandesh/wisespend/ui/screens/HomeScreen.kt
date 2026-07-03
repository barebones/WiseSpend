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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Lucide
import com.sandesh.wisespend.ui.components.CardWidget
import com.sandesh.wisespend.ui.components.GreetingHeader
import com.sandesh.wisespend.ui.components.RecentTransactionsWidget
import com.sandesh.wisespend.ui.theme.WiseSpendTheme
import com.sandesh.wisespend.util.CurrencyUtils
import com.sandesh.wisespend.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToNotifications:() -> Unit,
    expenseViewModel: ExpenseViewModel = viewModel()
    ) {

    val scrollState = rememberScrollState()

    val recentExpenses by expenseViewModel.recentExpenses.collectAsStateWithLifecycle()
    val budget by expenseViewModel.budget.collectAsStateWithLifecycle()
    val userName by expenseViewModel.userName.collectAsStateWithLifecycle()
    val totalSpent by expenseViewModel.totalSpent.collectAsStateWithLifecycle()
    val currencyCode by expenseViewModel.currencyCode.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { GreetingHeader(userName, Modifier) {
                    expenseViewModel.setUsername(it)
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
                    ) {
                        Icon(
                            imageVector = Lucide.Bell,
                            contentDescription = "Notification",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                // will implement in future
//                actions = {
//                    IconButton(
//                        onClick = {
//                            // scan the image to get bill info
//                        },
//                        modifier = Modifier
//                            .clip(CircleShape)
//                            .background(MaterialTheme.colorScheme.surface)
//                    ) {
//                        Icon(
//                            imageVector = Lucide.ScanLine,
//                            contentDescription = "Scan Bill/Barcode",
//                            tint = MaterialTheme.colorScheme.primary,
//                            modifier = Modifier.size(22.dp)
//                        )
//                    }
//                }
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
                onSetBudget = { expenseViewModel.setBudget(it) }
            )
            Spacer(modifier = Modifier.size(24.dp))
            RecentTransactionsWidget(
                expenses = recentExpenses,
                currencySymbol = currencySymbol
            )

            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WiseSpendTheme {
        HomeScreen(
            Modifier,
            {}
        )
    }
}
