package github.barebones.wisespend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import github.barebones.wisespend.ui.theme.ThemeState
import github.barebones.wisespend.ui.theme.WiseSpendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ThemeState.init(this)

        setContent {
            WiseSpendTheme {
                WiseSpendNavHost()
            }
        }
    }
}
