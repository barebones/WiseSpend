package com.sandesh.wisespend

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.sandesh.wisespend.ui.components.WiseNavigationBar
import com.sandesh.wisespend.ui.screens.HomeScreen
import com.sandesh.wisespend.ui.screens.SettingsScreen
import kotlinx.coroutines.launch

data class NavItems(
    val label: String,
    val icon: ImageVector,
)

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
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
                onSelect ={ idx ->
                    scope.launch { pagerState.animateScrollToPage(idx) }

                },
                modifier = Modifier.navigationBarsPadding()
            )
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
                    0 -> HomeScreen()
                    1 -> SettingsScreen()
                }
            }
        }
    }
}
