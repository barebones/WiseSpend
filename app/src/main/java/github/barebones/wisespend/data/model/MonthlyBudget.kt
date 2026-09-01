package github.barebones.wisespend.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_budgets")
data class MonthlyBudget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val budgetAmount: Double,
    val spentAmount: Double = 0.0,
    val startDate: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
