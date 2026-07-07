package github.barebones.wisespend.data.repository

import github.barebones.wisespend.data.local.CategoryDao
import github.barebones.wisespend.data.local.ExpenseDao
import github.barebones.wisespend.data.local.SettingsDao
import github.barebones.wisespend.data.model.Category
import github.barebones.wisespend.data.model.Expense
import github.barebones.wisespend.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao
) {

    // Expenses
    fun getAllExpenses(): Flow<List<Expense>>    = expenseDao.getAllExpenses()
    fun getRecentExpenses(): Flow<List<Expense>> = expenseDao.getRecentExpenses()
    fun getTotalSpent(): Flow<Double>            = expenseDao.getTotalSpent()
    suspend fun insertExpense(e: Expense)        = expenseDao.insertExpense(e)
    suspend fun deleteExpense(e: Expense)        = expenseDao.deleteExpense(e)
    suspend fun getAllExpensesSync()             = expenseDao.getAllSync()
    suspend fun insertAllExpenses(list: List<Expense>) = expenseDao.insertAll(list)
    suspend fun deleteAllExpenses()              = expenseDao.deleteAll()


    // Categories
    fun getCategories(): Flow<List<Category>>   = categoryDao.getAll()
    suspend fun insertCategory(c: Category)     = categoryDao.insert(c)
    suspend fun deleteCategory(c: Category)     = categoryDao.delete(c)
    suspend fun categoryCount(): Int            = categoryDao.count()
    suspend fun getAllCategoriesSync()          = categoryDao.getAllSync()
    suspend fun insertAllCategories(list: List<Category>) = categoryDao.insertAll(list)
    suspend fun deleteAllCategories()           = categoryDao.deleteAll()


    // Settings
    fun getSettings(): Flow<UserSettings?>      = settingsDao.getSettings()
    suspend fun getSettingsSync()               = settingsDao.getSettingsSync()
    suspend fun setBudget(amount: Double)       = settingsDao.upsert(UserSettings(budget = amount))

    suspend fun updateUsername(name: String) = settingsDao.updateUsername(name)

    suspend fun updateCurrencyCode(code: String) = settingsDao.updateCurrencyCode(code)

    suspend fun upsert(settings: UserSettings) = settingsDao.upsert(settings)
}