package github.barebones.wisespend.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val version: Int = 1,
    val expenses: List<ExpenseBackup>,
    val categories: List<CategoryBackup>,
    val settings: UserSettingsBackup? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class ExpenseBackup(
    val title: String,
    val amount: Double,
    val categoryName: String,
    val date: String,
    val time: String? = null,
    val createdAt: Long
)

@Serializable
data class CategoryBackup(
    val name: String,
    val iconKey: String,
    val isDefault: Boolean
)

@Serializable
data class UserSettingsBackup(
    val budget: Double,
    val userName: String,
    val currencyCode: String
)

fun Expense.toBackup() = ExpenseBackup(title, amount, categoryName, date, time, createdAt)
fun Category.toBackup() = CategoryBackup(name, iconKey, isDefault)
fun UserSettings.toBackup() = UserSettingsBackup(budget, userName, currencyCode)

fun ExpenseBackup.toEntity() = Expense(title = title, amount = amount, categoryName = categoryName, date = date, time = time, createdAt = createdAt)
fun CategoryBackup.toEntity() = Category(name = name, iconKey = iconKey, isDefault = isDefault)
fun UserSettingsBackup.toEntity() = UserSettings(budget = budget, userName = userName, currencyCode = currencyCode)
