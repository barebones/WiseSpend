package github.barebones.wisespend.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import github.barebones.wisespend.ui.theme.WiseSpendTheme

@Composable
fun HistoryWidget(modifier: Modifier = Modifier) {
    Column() {
        Text(
            text = "History"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryWidgetPreview() {
    WiseSpendTheme {
        HistoryWidget()
    }
}