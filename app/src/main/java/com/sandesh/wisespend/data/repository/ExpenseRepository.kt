package com.sandesh.wisespend.data.repository

import com.sandesh.wisespend.data.local.CategoryDao
import com.sandesh.wisespend.data.local.ExpenseDao
import com.sandesh.wisespend.data.local.SettingsDao
import com.sandesh.wisespend.data.model.Category
import com.sandesh.wisespend.data.model.Expense
import com.sandesh.wisespend.data.model.UserSettings
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


    // Categories
    fun getCategories(): Flow<List<Category>>   = categoryDao.getAll()
    suspend fun insertCategory(c: Category)     = categoryDao.insert(c)
    suspend fun deleteCategory(c: Category)     = categoryDao.delete(c)
    suspend fun categoryCount(): Int            = categoryDao.count()


    // Settings
    fun getSettings(): Flow<UserSettings?>      = settingsDao.getSettings()
    suspend fun setBudget(amount: Double)       = settingsDao.upsert(UserSettings(budget = amount))

    suspend fun updateUsername(name: String) = settingsDao.updateUsername(name)

    suspend fun updateCurrencyCode(code: String) = settingsDao.updateCurrencyCode(code)

    suspend fun upsert(settings: UserSettings) = settingsDao.upsert(settings)
}