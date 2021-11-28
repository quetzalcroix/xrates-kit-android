package io.vextabit.xrateskit.rates

import io.horizontalsystems.coinkit.models.CoinType

interface ILatestRatesCoinTypeDataSource {
    fun getCoinTypes(currencyCode: String): List<CoinType>
}
