package com.sandesh.wisespend.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sandesh.wisespend.data.model.Category
import com.sandesh.wisespend.data.model.Expense
import com.sandesh.wisespend.data.model.UserSettings

@Database(
entities = [Expense::class, Category::class, UserSettings::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wisespend_db"
                )
                    .fallbackToDestructiveMigration(false) // TODO swap for real migration before release
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}