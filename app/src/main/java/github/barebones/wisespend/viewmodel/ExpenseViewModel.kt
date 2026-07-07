package github.barebones.wisespend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import github.barebones.wisespend.data.local.AppDatabase
import github.barebones.wisespend.data.model.BackupData
import github.barebones.wisespend.data.model.Category
import github.barebones.wisespend.data.model.Expense
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDate

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

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ExpenseRepository(db.expenseDao(), db.categoryDao(), db.settingsDao())

        recentExpenses = repository.getRecentExpenses()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        allExpenses = repository.getAllExpenses()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        totalSpent = repository.getTotalSpent()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

        budget = repository.getSettings()
            .map { it?.budget ?: 0.0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

        availableBalance = combine(budget, totalSpent) { budget, spent ->
            (budget - spent).coerceAtLeast(0.0)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            0.0
        )

        userName = repository.getSettings()
            .map { it?.userName ?: "User" }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "User")

        currencyCode = repository.getSettings()
            .map { it?.currencyCode ?: "NPR" }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "NPR")

        categories = repository.getCategories()
            .map { list ->
                list.map { cat ->
                    ExpenseCategory(cat.name, IconMapper.fromKey(cat.iconKey))
                }
            }
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
        viewModelScope.launch { repository.setBudget(amount) }
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

            // Version check for future compatibility
            // TODO: add compatibility for old version of backup version
            //  if i changed something later in db
            if (backupData.version > 1) {
                return@withContext Result.failure(Exception("Unsupported backup version: ${backupData.version}"))
            }

            // Clear existing data
            // For a clean restore, we clear then insert
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