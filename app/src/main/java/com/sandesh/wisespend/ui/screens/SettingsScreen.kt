package com.sandesh.wisespend.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.CloudDownload
import com.composables.icons.lucide.CloudUpload
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Moon
import com.composables.icons.lucide.Palette
import com.composables.icons.lucide.Settings
import com.sandesh.wisespend.BuildConfig
import com.sandesh.wisespend.R
import com.sandesh.wisespend.ui.components.CurrencyPicker
import com.sandesh.wisespend.ui.components.ExpandableCard
import com.sandesh.wisespend.ui.theme.AppColorScheme
import com.sandesh.wisespend.ui.theme.AppThemeMode
import com.sandesh.wisespend.ui.theme.ThemeState
import com.sandesh.wisespend.ui.theme.WiseSpendTheme
import com.sandesh.wisespend.util.CurrencyUtils
import com.sandesh.wisespend.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    expenseViewModel: ExpenseViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val availableBalance by expenseViewModel.availableBalance.collectAsStateWithLifecycle()
    val currencyCode by expenseViewModel.currencyCode.collectAsStateWithLifecycle()

    val currentMode = ThemeState.themeMode.value
    val currentScheme = ThemeState.colorScheme.value

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val jsonData = expenseViewModel.exportData()
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(jsonData.toByteArray())
                    }
                    Toast.makeText(context, "Backup created successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to create backup: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val jsonData = reader.use { r -> r.readText() }
                    
                    val result = expenseViewModel.importData(jsonData)
                    if (result.isSuccess) {
                        Toast.makeText(context, "Data imported successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Import failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to read file: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    SettingsScreenContent(
        modifier = modifier,
        budget = availableBalance,
        currencyCode = currencyCode,
        currentMode = currentMode,
        currentScheme = currentScheme,
        onModeChange = { mode -> ThemeState.setMode(context, mode) },
        onSchemeChange = { scheme -> ThemeState.setScheme(context, scheme) },
        onCurrencyChange = { code -> expenseViewModel.setCurrencyCode(code) },
        onBackup = {
            val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
            createDocumentLauncher.launch("WiseSpend_Backup_$date.json")
        },
        onRestore = {
            openDocumentLauncher.launch(arrayOf("application/json"))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    budget: Double,
    currencyCode: String,
    currentMode: AppThemeMode,
    currentScheme: AppColorScheme,
    onModeChange: (AppThemeMode) -> Unit,
    onSchemeChange: (AppColorScheme) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit
) {
    val context = LocalContext.current

    val selectedCurrency = remember(currencyCode) {
        CurrencyUtils.getCurrencyByCode(currencyCode)
    }

    Scaffold(
        modifier = modifier, topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings", fontWeight = FontWeight.Bold
                    )
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Transparent, shape = RoundedCornerShape(20.dp)),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        ) {
            item(key = "general") {
                ExpandableCard(
                    header = {
                        SectionHeader(
                            icon = Lucide.Settings, title = "General"
                        )
                    }) {
                    SettingsCard {
                        Column {
                            Text(
                                text = "Currency",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )

                            CurrencyPicker(
                                selected = selectedCurrency,
                                onSelected = {
                                    onCurrencyChange(it.code)
                                },
                                budget = budget
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item(key = "general_spacer") {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item(key = "appearance") {
                ExpandableCard(
                    header = {
                        SectionHeader(
                            icon = Lucide.Moon, title = "Appearance"
                        )
                    }) {
                    SettingsCard {
                        Column {
                            Text(
                                text = "Theme Mode",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AppThemeMode.entries.forEach { mode ->
                                    ThemeModeChip(
                                        label = when (mode) {
                                            AppThemeMode.LIGHT -> "Light"
                                            AppThemeMode.DARK -> "Dark"
                                            AppThemeMode.AMOLED -> "AMOLED"
                                            AppThemeMode.SYSTEM -> "Auto"
                                        },
                                        selected = currentMode == mode,
                                        onClick = { onModeChange(mode) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    SectionHeader(
                        icon = Lucide.Palette, title = "Color Scheme"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SettingsCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                AppColorScheme.entries.forEach { scheme ->
                                    ColorDot(
                                        scheme = scheme,
                                        selected = currentScheme == scheme,
                                        onClick = { onSchemeChange(scheme) })
                                }
                            }

                            Text(
                                text = currentScheme.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }

            item(key = "appearance_spacer") {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item(key = "backup_restore") {
                ExpandableCard(
                    header = {
                        SectionHeader(
                            icon = Lucide.CloudDownload,
                            title = "Backup and restore"
                        )
                    }
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        SettingsCard(
                            onClick = onBackup
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.CloudUpload,
                                    contentDescription = "Backup data",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(8.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Local Backup",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Create a local backup of your data",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        SettingsCard(
                            onClick = onRestore
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.CloudDownload,
                                    contentDescription = "Restore data",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(8.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Import Data",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Restore data from a saved JSON file",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item(key = "backup_spacer") {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item(key = "about") {
                ExpandableCard(
                    header = {
                        SectionHeader(
                            icon = Icons.Outlined.Info, title = "About"
                        )
                    }
                ) {
                    SettingsCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoRow(label = "App Name", value = stringResource(R.string.app_name))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                            InfoRow(label = "Package Name", value = context.packageName)
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                            InfoRow(label = "Version", value = BuildConfig.VERSION_NAME)
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                            InfoRow(
                                label = "Build",
                                value = if (BuildConfig.DEBUG) "debug" else "release"
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                            InfoRow(label = "Developer", value = "Sandesh")
                        }
                    }
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector, title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick ?: {},
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(
            1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
        )
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        )
        {
            content()
        }
    }
}

@Composable
private fun ThemeModeChip(
    label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "chipBg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "chipBorder"
    )

    val textColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "chipText"
    )

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ), contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun ColorDot(
    scheme: AppColorScheme, selected: Boolean, onClick: () -> Unit
) {
    val dotColor = when (scheme) {
        AppColorScheme.MONO2 -> Color(0xFF2C2C2C)
        AppColorScheme.OCEAN -> Color(0xFF0066CC)
        AppColorScheme.FOREST -> Color(0xFF2E7D32)
        AppColorScheme.SUNSET -> Color(0xFFE65100)
        AppColorScheme.LAVENDER -> Color(0xFF6A4FC7)
        AppColorScheme.ROSE -> Color(0xFFC2185B)
        AppColorScheme.EMBER -> Color(0xFFD84315)
    }

    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
        else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "dotBorder"
    )

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .border(2.5.dp, borderColor, CircleShape)
            .padding(4.dp)
            .clip(CircleShape)
            .background(dotColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ), contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: String, value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    WiseSpendTheme {
        SettingsScreenContent(
            budget = 1000.0,
            currencyCode = "USD",
            currentMode = AppThemeMode.SYSTEM,
            currentScheme = AppColorScheme.MONO2,
            onModeChange = {},
            onSchemeChange = {},
            onCurrencyChange = {},
            onBackup = {},
            onRestore = {}
        )
    }
}