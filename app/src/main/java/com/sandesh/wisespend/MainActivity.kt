package com.sandesh.wisespend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sandesh.wisespend.ui.theme.ThemeState
import com.sandesh.wisespend.ui.theme.WiseSpendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ThemeState.init(this)

        setContent {
            WiseSpendTheme {
                MainScreen()
            }
        }
    }
}
