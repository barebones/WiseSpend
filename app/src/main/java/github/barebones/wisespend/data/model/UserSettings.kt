package github.barebones.wisespend.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


// Info related to user
// for eg: username, maybe others in future
@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 1,
    val budget: Double = 0.0,
    val userName: String = "User",
    val currencyCode: String = "NPR",
    val activeBudgetId: Int = 0
)
