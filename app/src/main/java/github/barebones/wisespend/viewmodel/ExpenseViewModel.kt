package github.barebones.wisespend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import github.barebones.wisespend.data.local.AppDatabase
import github.barebones.wisespend.data.model.BackupData
import github.barebones.wisespend.data.model.Category
import github.barebones.wisespend.data.model.Expense
import github.barebones.wisespend.data.model.MonthlyBudget
import github.barebones.wisespend.data.model.UserSettings
import github.barebones.wisespend.data.model.toBackup
import github.barebones.wisespend.data.model.toEntity
import github.barebones.wisespend.data.repository.ExpenseRepository
import github.barebones.wisespend.ui.screens.ExpenseCategory
import github.barebones.wisespend.ui.screens.defaultCategories
import github.barebones.wisespend.util.IconMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

// View model to access expense data
class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository

    val recentExpenses: StateFlow<List<Expense>>
    val allExpenses: StateFlow<List<Expense>>
    val totalSpent: StateFlow<Double>
    val budget: StateFlow<Double>
    val availableBalance: StateFlow<Double>

    val userName: StateFlow<String>
    val currencyCode: StateFlow<String>

    val categories: StateFlow<List<ExpenseCategory>>
    val monthlyBudgets: StateFlow<List<MonthlyBudget>>

    val activeBudget: StateFlow<MonthlyBudget?>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ExpenseRepository(db.expenseDao(), db.categoryDao(), db.settingsDao(), db.monthlyBudgetDao())

        val settingsFlow = repository.getSettings()

        activeBudget = settingsFlow
            .flatMapLatest { settings ->
                if (settings == null || settings.activeBudgetId == 0) {
                    flowOf(null)
                } else {
                    repository.getMonthlyBudget(settings.activeBudgetId)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

        recentExpenses = activeBudget.flatMapLatest { budget ->
            if (budget == null) flowOf(emptyList())
            else repository.getExpensesFrom(budget.startDate)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        allExpenses = repository.getAllExpenses()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        totalSpent = activeBudget.flatMapLatest { budget ->
            if (budget == null) flowOf(0.0)
            else repository.getTotalSpentFrom(budget.startDate)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

        budget = activeBudget.map { it?.budgetAmount ?: 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

        availableBalance = combine(budget, totalSpent) { b, s ->
            (b - s).coerceAtLeast(0.0)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

        userName = settingsFlow
            .map { it?.userName ?: "User" }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "User")

        currencyCode = settingsFlow
            .map { it?.currencyCode ?: "NPR" }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "NPR")

        categories = repository.getCategories()
            .map { list ->
                list.map { cat ->
                    ExpenseCategory(cat.name, IconMapper.fromKey(cat.iconKey))
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        monthlyBudgets = repository.getAllMonthlyBudgets()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        viewModelScope.launch {
            if (repository.categoryCount() == 0) {
                defaultCategories.forEach { cat ->
                    repository.insertCategory(
                        Category(
                            name = cat.name,
                            iconKey = IconMapper.toKey(cat.icon),
                            isDefault = true
                        )
                    )
                }
            }
            
            // Check if we have an active budget, if not, create one for current month
            val currentSettings = repository.getSettings().first()
            if (currentSettings == null || currentSettings.activeBudgetId == 0) {
                val latestBudget = repository.getLatestBudgetSync()
                if (latestBudget != null) {
                    repository.updateActiveBudgetId(latestBudget.id)
                } else {
                    val now = LocalDate.now()
                    val label = now.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + now.year
                    val newId = repository.insertMonthlyBudget(
                        MonthlyBudget(
                            label = label,
                            budgetAmount = currentSettings?.budget ?: 0.0
                        )
                    )
                    repository.updateActiveBudgetId(newId.toInt())
                }
            }
        }

        // Live update the spentAmount in the budget snapshot
        viewModelScope.launch {
            combine(activeBudget, totalSpent) { budget, spent ->
                if (budget != null && budget.spentAmount != spent) {
                    repository.insertMonthlyBudget(budget.copy(spentAmount = spent))
                }
            }.collect {}
        }
    }

    fun startNewMonth(label: String, budgetAmount: Double? = null) {
        viewModelScope.launch {
            val currentBudget = activeBudget.value
            val settings = repository.getSettings().first()
            val amount = budgetAmount ?: settings?.budget ?: 0.0
            
            val newId = repository.insertMonthlyBudget(
                MonthlyBudget(
                    label = label,
                    budgetAmount = amount
                )
            )
            repository.updateActiveBudgetId(newId.toInt())
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }

    fun addExpense(title: String, amount: Double, categoryName: String, date: LocalDate, time: String) {
        viewModelScope.launch {
            repository.insertExpense(
                Expense(
                    title = title,
                    amount = amount,
                    categoryName = categoryName,
                    date = date.toString(),
                    time = time
                )
            )
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }

    fun addCategory(category: ExpenseCategory) {
        viewModelScope.launch {
            repository.insertCategory(
                Category(name = category.name, iconKey = IconMapper.toKey(category.icon))
            )
        }
    }

    fun deleteCategory(category: ExpenseCategory) {
        viewModelScope.launch {
            repository.deleteCategory(
                Category(name = category.name, iconKey = IconMapper.toKey(category.icon))
            )
        }
    }

    fun setBudget(amount: Double) {
        viewModelScope.launch {
            val current = activeBudget.value
            if (current != null) {
                repository.insertMonthlyBudget(current.copy(budgetAmount = amount))
            }
            repository.setBudget(amount)
        }
    }

    fun setUsername(name: String){
        viewModelScope.launch {
            val current = repository.getSettings().first()
            if (current != null) {
                repository.updateUsername(name)
            } else {
                repository.upsert(UserSettings(userName = name))
            }
        }
    }

    fun setCurrencyCode(code: String) {
        viewModelScope.launch {
            val current = repository.getSettings().first()
            if (current != null) {
                repository.updateCurrencyCode(code)
            } else {
                repository.upsert(UserSettings(currencyCode = code))
            }
        }
    }

    suspend fun exportData(): String = withContext(Dispatchers.IO) {
        val expenses = repository.getAllExpensesSync().map { it.toBackup() }
        val categories = repository.getAllCategoriesSync().map { it.toBackup() }
        val settings = repository.getSettingsSync()?.toBackup()

        val backupData = BackupData(
            expenses = expenses,
            categories = categories,
            settings = settings
        )

        Json.encodeToString(backupData)
    }

    suspend fun importData(jsonData: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val backupData = Json.decodeFromString<BackupData>(jsonData)
            if (backupData.version > 1) {
                return@withContext Result.failure(Exception("Unsupported backup version: ${backupData.version}"))
            }
            repository.deleteAllExpenses()
            repository.deleteAllCategories()
            repository.insertAllExpenses(backupData.expenses.map { it.toEntity() })
            repository.insertAllCategories(backupData.categories.map { it.toEntity() })
            backupData.settings?.let {
                repository.upsert(it.toEntity())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
