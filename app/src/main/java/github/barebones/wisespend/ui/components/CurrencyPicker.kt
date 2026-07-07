package github.barebones.wisespend.ui.components


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Lucide
import com.murgupluoglu.flagkit.FlagKit
import github.barebones.wisespend.data.model.CurrencyItem
import github.barebones.wisespend.ui.theme.WiseSpendTheme
import github.barebones.wisespend.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPicker(
    selected: CurrencyItem,
    budget: Double,
    onSelected: (CurrencyItem) -> Unit
) {

    var showSheet by remember {
        mutableStateOf(false)
    }

    Surface(
        onClick = { showSheet = true },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        val context = LocalContext.current
        ListItem(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            headlineContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = FlagKit.getResId(
                                context,
                                selected.countryCode
                            )
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Text(selected.code)
                    Icon(
                        Lucide.ChevronDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            trailingContent = {
                Text(
                    text = budget.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }

    if (showSheet) {

        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) {

            CurrencyPickerSheet(
                selected = selected,
                onSelect = {

                    onSelected(it)
                    showSheet = false

                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyPickerSheet(
    selected: CurrencyItem?,
    onSelect: (CurrencyItem) -> Unit
) {
    val context = LocalContext.current

    var search by remember { mutableStateOf("") }

    val filtered = remember(search) {
        CurrencyUtils.currencies.filter {
            it.country.contains(search, true) ||
                    it.currency.contains(search, true) ||
                    it.code.contains(search, true)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = search,
                    onQueryChange = { search = it },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(32)
                    ),
                    placeholder = {
                        Text("Search currencies")
                    }
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    Color.Transparent
                )
        ) {}

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            items(
                items = filtered,
                key = { it.code }
            ) { item ->

                val isSelected = item.code == selected?.code

                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "selectedBg"
                )

                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    else Color.Transparent,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "selectedBorder"
                )

                Surface(
                    onClick = { onSelect(item) },
                    shape = RoundedCornerShape(24.dp),
                    color = bgColor,
                    tonalElevation = if (isSelected) 4.dp else 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .animateItem()
                ) {

                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.border(1.dp, borderColor, RoundedCornerShape(24.dp)),

                        leadingContent = {
                            Icon(
                                painter = painterResource(
                                    FlagKit.getResId(
                                        context,
                                        item.countryCode
                                    )
                                ),
                                contentDescription = item.country,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(32.dp)
                            )
                        },

                        headlineContent = {
                            Text(
                                item.country,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },

                        supportingContent = {
                            Text(
                                "${item.currency} • ${item.code}"
                            )
                        },

                        trailingContent = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Lucide.CircleCheck,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Text(
                                    item.symbol,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyPickerPreview() {
    WiseSpendTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CurrencyPicker(
                selected = CurrencyItem(
                    country = "Nepal",
                    currency = "Nepalese Rupee",
                    code = "NPR",
                    symbol = "रू",
                    flag = "🇳🇵",
                    countryCode = "np"
                ),
                onSelected = {},
                budget = 100.0
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyPickerSheetPreview() {
    WiseSpendTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CurrencyPickerSheet(
                selected = null,
                onSelect = {}
            )
        }
    }
}

