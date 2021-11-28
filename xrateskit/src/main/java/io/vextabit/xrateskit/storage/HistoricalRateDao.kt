package io.vextabit.xrateskit.storage

import androidx.room.*
import io.vextabit.coinkit.models.CoinType
import io.vextabit.xrateskit.entities.HistoricalRate

@Dao
interface HistoricalRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rate: HistoricalRate)

    @Delete
    fun delete(rate: HistoricalRate)

    @Query("SELECT * FROM HistoricalRate WHERE coinType = :coinType AND currencyCode = :currency AND timestamp = :timestamp LIMIT 1")
    fun getRate(coinType: CoinType, currency: String, timestamp: Long): HistoricalRate?
}
