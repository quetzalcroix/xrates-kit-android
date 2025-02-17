package io.vextabit.xrateskit.storage

import androidx.room.*
import io.vextabit.coinkit.models.CoinType
import io.vextabit.xrateskit.entities.LatestRateEntity

@Dao
interface LatestRatesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(all: List<LatestRateEntity>)

    @Delete
    fun delete(stats: LatestRateEntity)

    @Query("SELECT * FROM LatestRateEntity WHERE coinType = :coinType AND currencyCode = :currency ORDER BY timestamp")
    fun getLatestRate(coinType: CoinType, currency: String): LatestRateEntity?

    @Query("SELECT * FROM LatestRateEntity WHERE coinType IN(:coinTypes) AND currencyCode = :currency ORDER BY timestamp DESC")
    fun getOldList(coinTypes: List<CoinType>, currency: String): List<LatestRateEntity>
}
