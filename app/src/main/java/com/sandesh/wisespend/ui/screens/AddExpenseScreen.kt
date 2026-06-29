package com.sandesh.wisespend.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Clapperboard
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.UtensilsCrossed
import com.composables.icons.lucide.X
import com.sandesh.wisespend.ui.components.CalendarWidget
import com.sandesh.wisespend.ui.theme.WiseSpendTheme
import com.sandesh.wisespend.viewmodel.ExpenseViewModel
import java.time.LocalDate

val TextGray = Color(0xFF7E8A97)

data class ExpenseCategory(val name: String, val icon: ImageVector)

val defaultCategories = listOf(
    ExpenseCategory("Grocery", Lucide.ShoppingCart),
    ExpenseCategory("Food", Lucide.UtensilsCrossed),
    ExpenseCategory("Entertainment", Lucide.Clapperboard),
    ExpenseCategory("Borrow/Lend", Icons.Default.Add),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBack: () -> Unit, expenseViewModel: ExpenseViewModel = viewModel()
) {
    val vmCategories by expenseViewModel.categories.collectAsStateWithLifecycle()

    AddExpenseContent(
        vmCategories = vmCategories,
        onBack = onBack,
        onSaveExpense = { title, amount, categoryName, date ->
            expenseViewModel.addExpense(title, amount, categoryName, date)
            onBack()
        },
        onCategoryAdded = { expenseViewModel.addCategory(it) },
        onCategoryDeleted = {
            expenseViewModel.deleteCategory(it)
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseContent(
    vmCategories: List<ExpenseCategory>,
    onBack: () -> Unit,
    onSaveExpense: (String, Double, String, LocalDate) -> Unit,
    onCategoryAdded: (ExpenseCategory) -> Unit,
    onCategoryDeleted: (ExpenseCategory) -> Unit
) {
    var expenseTitle by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var categories by remember { mutableStateOf(defaultCategories) }

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background), topBar = {
        CenterAlignedTopAppBar(
            title = {
            Text(
                text = "Add Expense",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }, navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Lucide.ChevronLeft,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
        )
    }, bottomBar = {
        Button(
            enabled = expenseTitle.isNotBlank() && amount.toDoubleOrNull() != null && selectedCategory != null,
            onClick = {
                val parsedAmount = amount.toDoubleOrNull()
                if (expenseTitle.isNotBlank() && parsedAmount != null && selectedCategory != null) {
                    onSaveExpense(
                        expenseTitle.trim(), parsedAmount, selectedCategory!!.name, selectedDate
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(8.dp)
                .imePadding()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "ADD EXPENSE",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }, containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CalendarWidget(
                selectedDate = selectedDate, onDateSelected = { selectedDate = it })

            CustomInputField(
                label = "Expense Title",
                value = expenseTitle,
                onValueChange = { expenseTitle = it })

            CustomInputField(
                label = "Amount", value = amount, onValueChange = { amount = it }, isAmount = true
            )

            ExpenseCategorySection(
                categories = vmCategories,
                selectedCategory = selectedCategory,
                onCategorySelect = { selectedCategory = it },
                onCategoryAdded = onCategoryAdded,
                onCategoryDeleted = { cat ->
                    onCategoryDeleted(cat)
                    if (selectedCategory == cat) selectedCategory = null
                })

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}


@Composable
fun CustomInputField(
    label: String, value: String, onValueChange: (String) -> Unit, isAmount: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label, color = MaterialTheme.colorScheme.onSurface.copy(0.7f), fontSize = 14.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(
                    2.dp, MaterialTheme.colorScheme.onSurface.copy(0.15f), RoundedCornerShape(16.dp)
                )
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = false
                )
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isAmount) {
                Text(text = "$", color = TextGray, fontSize = 16.sp)
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        text = if (isAmount) "0.00" else "Enter $label"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isAmount) KeyboardType.Number else KeyboardType.Text
                ),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,

                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}


@Composable
fun ExpenseCategorySection(
    categories: List<ExpenseCategory>,
    selectedCategory: ExpenseCategory?,
    onCategorySelect: (ExpenseCategory) -> Unit,
    onCategoryAdded: (ExpenseCategory) -> Unit,
    onCategoryDeleted: (ExpenseCategory) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    var deletableCategory by remember { mutableStateOf<ExpenseCategory?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Expense Category", color = TextGray, fontSize = 14.sp)
            if (selectedCategory != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = selectedCategory.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = selectedCategory.name,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        val allItems: List<Any> = categories + listOf("ADD_BUTTON")

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp),
            userScrollEnabled = false
        ) {
            items(allItems) { item ->
                if (item == "ADD_BUTTON") {
                    AddCategoryButton(onClick = {
                        showAddDialog = true
                        deletableCategory = null
                    })
                } else {
                    val cat = item as ExpenseCategory
                    CategoryChip(
                        category = cat,
                        isSelected = selectedCategory == cat,
                        showDeleteButton = deletableCategory == cat,
                        onClick = {
                            if (deletableCategory == cat) {
                                deletableCategory = null
                            } else {

                                onCategorySelect(cat)
                            }
                        },
                        onLongClick = { deletableCategory = cat },
                        onDeleteClick = {
                            onCategoryDeleted(cat)
                            deletableCategory = null // clear state
                        })
                }
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(onDismiss = { showAddDialog = false }, onConfirm = { newCat ->
            onCategoryAdded(newCat)
            showAddDialog = false
        })
    }
}

@Composable
fun CategoryChip(
    category: ExpenseCategory,
    isSelected: Boolean,
    showDeleteButton: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurface.copy(0.08f)

    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .combinedClickable(onClick = { onClick() }, onLongClick = { onLongClick() }),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.name,
                    color = contentColor,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                AnimatedVisibility(
                    visible = isSelected && !showDeleteButton,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // Overlay delete button
        AnimatedVisibility(
            visible = showDeleteButton,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(1.dp, 1.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(MaterialTheme.colorScheme.error)
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = "Delete Category",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun AddCategoryButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.5.dp, TextGray.copy(alpha = 0.35f), RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add category",
                tint = TextGray,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Add", color = TextGray, fontSize = 11.sp)
        }
    }
}

// ouch, need to refactor this
val iconOptions = listOf(
    Icons.Default.Home,
    Icons.Default.Flight,
    Icons.Default.SportsEsports,
    Icons.Default.LocalMall,
    Icons.Default.School,
    Icons.Default.FitnessCenter,
    Icons.Default.Lightbulb,
    Icons.Default.Receipt,
    Icons.Default.ShoppingCart,
    Icons.Default.LocalHospital,
    Icons.Default.LocalDining,
    Icons.Default.LocalShipping,
    Icons.Default.LocalActivity,
    Icons.Default.Straighten,
    Icons.Default.Add
)

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit, onConfirm: (ExpenseCategory) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(Icons.AutoMirrored.Filled.Label) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "New Category",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = selectedIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Text(
                        text = name.ifEmpty { "Category name" },
                        color = if (name.isEmpty()) TextGray
                        else MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                BasicTextField(
                    value = name,
                    onValueChange = { if (it.length <= 16) name = it },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.5.dp,
                            MaterialTheme.colorScheme.onSurface.copy(0.15f),
                            RoundedCornerShape(12.dp)
                        )
                        .background(
                            MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    decorationBox = { inner ->
                        if (name.isEmpty()) {
                            Text("Category name", color = TextGray, fontSize = 15.sp)
                        }
                        inner()
                    })

                Text(text = "Pick an icon", color = TextGray, fontSize = 13.sp)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5), // adjusted grid count for readability
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 120.dp),
                    userScrollEnabled = true
                ) {
                    items(iconOptions) { icon ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (icon == selectedIcon) MaterialTheme.colorScheme.primary.copy(
                                        0.2f
                                    )
                                    else Color.Transparent
                                )
                                .clickable { selectedIcon = icon },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (icon == selectedIcon) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = TextGray)
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(ExpenseCategory(name.trim(), selectedIcon))
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Add", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddExpenseScreenPreview() {

    val defaultCategories = listOf(
        ExpenseCategory("Grocery", Lucide.ShoppingCart),
        ExpenseCategory("Food", Lucide.UtensilsCrossed),
        ExpenseCategory("Entertainment", Lucide.Clapperboard),
        ExpenseCategory("Borrow/Lend", Icons.Default.Add),
    )
    WiseSpendTheme {
        AddExpenseContent(
            onBack = { }, onSaveExpense = { _, _, _, _ -> },
            vmCategories =defaultCategories,
            onCategoryAdded = {},
            onCategoryDeleted = {}
        )
    }
}