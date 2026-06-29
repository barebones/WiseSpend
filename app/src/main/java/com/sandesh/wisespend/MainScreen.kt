package com.sandesh.wisespend

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sandesh.wisespend.ui.components.WiseNavigationBar
import com.sandesh.wisespend.ui.screens.HomeScreen
import com.sandesh.wisespend.ui.screens.SettingsScreen
import com.sandesh.wisespend.ui.theme.WiseSpendTheme
import kotlinx.coroutines.launch

data class NavItems(
    val label: String,
    val icon: ImageVector,
)

@Composable
fun MainScreen(
    onNavigateToAddExpense:() -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val navItems = listOf(
        NavItems("Home", Icons.Default.Home),
        NavItems("Settings", Icons.Default.Settings),
    )

    val pagerState = rememberPagerState{ navItems.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            WiseNavigationBar(
                items = navItems,
                pagePosition = pagerState.currentPage + pagerState.currentPageOffsetFraction,
                onSelect = { idx ->
                    scope.launch { pagerState.animateScrollToPage(idx) }

                },
                modifier = Modifier.navigationBarsPadding()
            )
        },
        floatingActionButton = {
            // Check if the user is on the home page or actively swiping away from/to it
            val isHomeVisible = pagerState.currentPage == 0

            AnimatedVisibility(
                visible = isHomeVisible,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f)
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
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            beyondViewportPageCount = 0
        ) {
            page ->
            run {
                when (page) {
                    0 -> HomeScreen(
                        Modifier,
                        onNavigateToNotifications = onNavigateToNotifications,
                    )
                    1 -> SettingsScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    WiseSpendTheme {
        MainScreen({},{})
    }
}

