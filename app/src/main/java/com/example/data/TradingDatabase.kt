package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "calculation_history")
data class CalculationHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val assetClass: String, // "FOREX" or "CRYPTO"
    val symbol: String, // e.g. "EUR/USD", "BTC/USDT"
    val accountBalance: Double,
    val riskPercentage: Double,
    val riskAmount: Double,
    val stopLossValue: Double, // Pips for Forex, Stop Loss Distance for Crypto
    val entryPrice: Double?, // For Crypto
    val stopLossPrice: Double?, // For Crypto
    val calculatedPositionSize: Double, // Lots for Forex, Coins for Crypto
    val positionValue: Double, // Total exposure in USD
    val isLong: Boolean = true,
    val notes: String = ""
)

@Dao
interface CalculationHistoryDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(history: CalculationHistory)

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteCalculationById(id: Int)

    @Query("DELETE FROM calculation_history")
    suspend fun clearAllHistory()
}

@Database(entities = [CalculationHistory::class], version = 1, exportSchema = false)
abstract class TradingDatabase : RoomDatabase() {
    abstract fun calculationHistoryDao(): CalculationHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: TradingDatabase? = null

        fun getDatabase(context: Context): TradingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TradingDatabase::class.java,
                    "trading_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class TradingRepository(private val dao: CalculationHistoryDao) {
    val allHistory: Flow<List<CalculationHistory>> = dao.getAllHistory()

    suspend fun insert(calculation: CalculationHistory) {
        dao.insertCalculation(calculation)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteCalculationById(id)
    }

    suspend fun clearHistory() {
        dao.clearAllHistory()
    }
}
