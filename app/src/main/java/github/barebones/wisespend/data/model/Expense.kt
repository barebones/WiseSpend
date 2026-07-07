package github.barebones.wisespend.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val categoryName: String,
    val date: String,          // stored as "yyyy-MM-dd"
    val time: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)