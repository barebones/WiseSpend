package github.barebones.wisespend.data.local

import androidx.room.*
import github.barebones.wisespend.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: UserSettings)

    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getSettings(): Flow<UserSettings?>

    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getSettingsSync(): UserSettings?

    @Query("UPDATE user_settings SET userName = :name Where id = 1")
    suspend fun updateUsername(name: String)

    @Query("UPDATE user_settings SET currencyCode = :code Where id = 1")
    suspend fun updateCurrencyCode(code: String)
}