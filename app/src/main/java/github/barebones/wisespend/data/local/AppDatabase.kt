package github.barebones.wisespend.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import github.barebones.wisespend.data.model.Category
import github.barebones.wisespend.data.model.Expense
import github.barebones.wisespend.data.model.MonthlyBudget
import github.barebones.wisespend.data.model.UserSettings

@Database(
    entities = [Expense::class, Category::class, UserSettings::class, MonthlyBudget::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun settingsDao(): SettingsDao
    abstract fun monthlyBudgetDao(): MonthlyBudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_settings ADD COLUMN currencyCode TEXT NOT NULL DEFAULT 'USD'")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE expenses ADD COLUMN time TEXT")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `monthly_budgets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `month` INTEGER NOT NULL, `year` INTEGER NOT NULL, `budgetAmount` REAL NOT NULL, `spentAmount` REAL NOT NULL)"
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Update monthly_budgets
                db.execSQL("DROP TABLE IF EXISTS `monthly_budgets`")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `monthly_budgets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `label` TEXT NOT NULL, `budgetAmount` REAL NOT NULL, `spentAmount` REAL NOT NULL, `startDate` INTEGER NOT NULL, `isArchived` INTEGER NOT NULL)"
                )
                // Update user_settings
                db.execSQL("ALTER TABLE user_settings ADD COLUMN activeBudgetId INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wisespend_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
