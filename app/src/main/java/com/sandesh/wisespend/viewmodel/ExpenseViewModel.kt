package com.sandesh.wisespend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sandesh.wisespend.data.local.AppDatabase
import com.sandesh.wisespend.data.model.Category
import com.sandesh.wisespend.data.model.Expense
import com.sandesh.wisespend.data.model.UserSettings
import com.sandesh.wisespend.data.repository.ExpenseRepository
import com.sandesh.wisespend.ui.screens.ExpenseCategory
import com.sandesh.wisespend.ui.screens.defaultCategories
import com.sandesh.wisespend.util.IconMapper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    fun addExpense(title: String, amount: Double, categoryName: String, date: LocalDate) {
        viewModelScope.launch {
            repository.insertExpense(
                Expense(title = title, amount = amount, categoryName = categoryName, date = date.toString())
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
}