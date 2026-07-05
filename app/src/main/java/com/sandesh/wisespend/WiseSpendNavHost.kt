package com.sandesh.wisespend
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sandesh.wisespend.ui.screens.AddExpenseScreen
import com.sandesh.wisespend.ui.screens.NotificationScreen
import com.sandesh.wisespend.ui.screens.SplashScreen

@Composable
fun WiseSpendNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        composable("splash") {
            SplashScreen(
                onAnimationFinished = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "main",
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300))
            }
        ) {
            MainScreen(
                onNavigateToAddExpense = {
                    navController.navigate("add_expense")
                },
                onNavigateToNotifications = {
                    navController.navigate("notifications")
                }
            )
        }

        // Transitions to go to expense page
        composable(
            route= "add_expense",
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }

        ) {
            AddExpenseScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route= "notifications",
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }

        ) {
            NotificationScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}