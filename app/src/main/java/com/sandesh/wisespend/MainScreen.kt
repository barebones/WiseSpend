package com.sandesh.wisespend

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sandesh.wisespend.ui.components.WiseNavigationBar
import com.sandesh.wisespend.ui.screens.AnalyticsScreen
import com.sandesh.wisespend.ui.screens.AnalyticsScreenContent
import com.sandesh.wisespend.ui.screens.HomeScreen
import com.sandesh.wisespend.ui.screens.HomeScreenContent
import com.sandesh.wisespend.ui.screens.SettingsScreen
import com.sandesh.wisespend.ui.screens.SettingsScreenContent
import com.sandesh.wisespend.ui.theme.AppColorScheme
import com.sandesh.wisespend.ui.theme.AppThemeMode
import com.sandesh.wisespend.ui.theme.WiseSpendTheme
import kotlinx.coroutines.launch

data class NavItems(
    val label: String,
    val icon: ImageVector,
)

@Composable
fun MainScreen(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val navItems = listOf(
        NavItems("Home", Icons.Default.Home),
        NavItems("Analytics", Icons.Default.BarChart),
        NavItems("Settings", Icons.Default.Settings),
    )

    val pagerState = rememberPagerState { navItems.size }
    
    MainScreenContent(
        navItems = navItems,
        pagerState = pagerState,
        onNavigateToAddExpense = onNavigateToAddExpense,
        onNavigateToNotifications = onNavigateToNotifications
    )
}

@Composable
fun MainScreenContent(
    navItems: List<NavItems>,
    pagerState: PagerState,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val scope = rememberCoroutineScope()

    BackHandler(enabled = pagerState.currentPage != 0) {
        scope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            Modifier.fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding().coerceAtLeast(0.dp)),
                beyondViewportPageCount = 0
            ) { page ->
                when (page) {
                    0 -> {
                        if (LocalInspectionMode.current) {
                            HomeScreenContent(
                                onNavigateToNotifications = onNavigateToNotifications,
                                recentExpenses = emptyList(),
                                budget = 1000.0,
                                userName = "Sandesh",
                                totalSpent = 250.0,
                                currencyCode = "NPR",
                                onSetUsername = {},
                                onSetBudget = {}
                            )
                        } else {
                            HomeScreen(
                                modifier = Modifier,
                                onNavigateToNotifications = onNavigateToNotifications,
                            )
                        }
                    }
                    1 -> {
                        if (LocalInspectionMode.current) {
                            AnalyticsScreenContent(
                                expenses = emptyList(),
                                currencySymbol = "रू"
                            )
                        } else {
                            AnalyticsScreen()
                        }
                    }
                    2 -> {
                        if (LocalInspectionMode.current) {
                            SettingsScreenContent(
                                budget = 1000.0,
                                currencyCode = "NPR",
                                currentMode = AppThemeMode.SYSTEM,
                                currentScheme = AppColorScheme.MONO2,
                                onModeChange = {},
                                onSchemeChange = {},
                                onCurrencyChange = {}
                            )
                        } else {
                            SettingsScreen()
                        }
                    }
                }
            }
            val isHomeVisible = pagerState.currentPage == 0
            AnimatedVisibility(
                visible = isHomeVisible,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 110.dp)
                    .navigationBarsPadding()
            ) {
                FloatingActionButton(
                    onClick = onNavigateToAddExpense,
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(64.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add expenses",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            WiseNavigationBar(
                items = navItems,
                pagePosition = pagerState.currentPage + pagerState.currentPageOffsetFraction,
                onSelect = { idx ->
                    scope.launch { pagerState.animateScrollToPage(idx) }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navItems = listOf(
        NavItems("Home", Icons.Default.Home),
        NavItems("Analytics", Icons.Default.BarChart),
        NavItems("Settings", Icons.Default.Settings),
    )
    val pagerState = rememberPagerState { navItems.size }

    WiseSpendTheme {
        MainScreenContent(
            navItems = navItems,
            pagerState = pagerState,
            onNavigateToAddExpense = {},
            onNavigateToNotifications = {}
        )
    }
}
