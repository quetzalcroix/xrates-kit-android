package io.vextabit.xrateskit.rates

import io.vextabit.coinkit.models.CoinType

interface ILatestRatesCoinTypeDataSource {
    fun getCoinTypes(currencyCode: String): List<CoinType>
}
