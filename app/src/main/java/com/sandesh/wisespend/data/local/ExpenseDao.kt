package com.sandesh.wisespend.data.local

import androidx.room.*
import com.sandesh.wisespend.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<Expense>)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses")
    suspend fun getAllSync(): List<Expense>

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC LIMIT 10")
    fun getRecentExpenses(): Flow<List<Expense>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses")
    fun getTotalSpent(): Flow<Double>
}