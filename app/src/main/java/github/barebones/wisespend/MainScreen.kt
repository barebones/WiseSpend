package github.barebones.wisespend

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.barebones.wisespend.ui.components.WiseNavigationBar
import github.barebones.wisespend.ui.screens.AnalyticsScreen
import github.barebones.wisespend.ui.screens.AnalyticsScreenContent
import github.barebones.wisespend.ui.screens.HomeScreen
import github.barebones.wisespend.ui.screens.HomeScreenContent
import github.barebones.wisespend.ui.screens.SettingsScreen
import github.barebones.wisespend.ui.screens.SettingsScreenContent
import github.barebones.wisespend.ui.theme.AppColorScheme
import github.barebones.wisespend.ui.theme.AppThemeMode
import github.barebones.wisespend.ui.theme.WiseSpendTheme
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
    val snackbarHostState = remember { SnackbarHostState() }
    
    MainScreenContent(
        navItems = navItems,
        pagerState = pagerState,
        snackbarHostState = snackbarHostState,
        onNavigateToAddExpense = onNavigateToAddExpense,
        onNavigateToNotifications = onNavigateToNotifications
    )
}

@Composable
fun MainScreenContent(
    navItems: List<NavItems>,
    pagerState: PagerState,
    snackbarHostState: SnackbarHostState,
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            WiseNavigationBar(
                items = navItems,
                pagePosition = pagerState.currentPage + pagerState.currentPageOffsetFraction,
                onSelect = { idx ->
                    scope.launch { pagerState.animateScrollToPage(idx) }
                },
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Box(
            Modifier.fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
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
                                onSetBudget = {},
                                onDeleteExpense = {},
                                onUndoDelete = {},
                                snackbarHostState = snackbarHostState,
                                scope = scope
                            )
                        } else {
                            HomeScreen(
                                modifier = Modifier,
                                onNavigateToNotifications = onNavigateToNotifications,
                                snackbarHostState = snackbarHostState
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
                                onCurrencyChange = {},
                                onBackup = {},
                                onRestore = {}
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
                    .padding(end = 20.dp, bottom = 16.dp)
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
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF673AB7, device = "id:pixel_10_pro")
@Composable
fun MainScreenPreview() {
    val navItems = listOf(
        NavItems("Home", Icons.Default.Home),
        NavItems("Analytics", Icons.Default.BarChart),
        NavItems("Settings", Icons.Default.Settings),
    )
    val pagerState = rememberPagerState { navItems.size }
    val snackbarHostState = remember { SnackbarHostState() }

    WiseSpendTheme {
        MainScreenContent(
            navItems = navItems,
            pagerState = pagerState,
            snackbarHostState = snackbarHostState,
            onNavigateToAddExpense = {},
            onNavigateToNotifications = {}
        )
    }
}
