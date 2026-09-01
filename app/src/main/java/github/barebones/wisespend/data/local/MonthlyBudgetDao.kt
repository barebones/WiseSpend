package github.barebones.wisespend.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import github.barebones.wisespend.data.model.MonthlyBudget
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyBudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: MonthlyBudget): Long

    @Query("SELECT * FROM monthly_budgets WHERE id = :id")
    fun getBudgetById(id: Int): Flow<MonthlyBudget?>

    @Query("SELECT * FROM monthly_budgets WHERE id = :id")
    suspend fun getBudgetByIdSync(id: Int): MonthlyBudget?

    @Query("SELECT * FROM monthly_budgets ORDER BY startDate DESC")
    fun getAllMonthlyBudgets(): Flow<List<MonthlyBudget>>

    @Query("SELECT * FROM monthly_budgets ORDER BY startDate DESC LIMIT 1")
    suspend fun getLatestBudgetSync(): MonthlyBudget?
}
