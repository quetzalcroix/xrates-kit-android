package io.vextabit.xrateskit.coinmarkets

import io.vextabit.coinkit.models.CoinType
import io.vextabit.xrateskit.core.IDefiMarketsProvider
import io.vextabit.xrateskit.core.IInfoManager
import io.vextabit.xrateskit.entities.CoinMarket
import io.vextabit.xrateskit.entities.DefiTvl
import io.vextabit.xrateskit.entities.DefiTvlPoint
import io.vextabit.xrateskit.entities.TimePeriod
import io.vextabit.xrateskit.providers.coingecko.CoinGeckoProvider
import io.reactivex.Single

class DefiMarketsManager(
    private val coinGeckoProvider: CoinGeckoProvider,
    private val defiMarketsProvider: IDefiMarketsProvider
) : IInfoManager {

    fun getTopDefiMarketsAsync(currency: String, fetchDiffPeriod: TimePeriod, itemsCount: Int): Single<List<CoinMarket>> {
        return coinGeckoProvider.getTopCoinMarketsAsync(currency, fetchDiffPeriod, itemsCount, true)
    }

    fun getTopDefiTvlAsync(currency: String, fetchDiffPeriod: TimePeriod, itemsCount: Int, chain: String?): Single<List<DefiTvl>> {
        return defiMarketsProvider.getTopDefiTvlAsync(currency, fetchDiffPeriod, itemsCount, chain)
    }

    fun getDefiTvlPointsAsync(coinType: CoinType, currency: String, fetchDiffPeriod: TimePeriod): Single<List<DefiTvlPoint>> {
        return defiMarketsProvider.getDefiTvlPointsAsync(coinType, currency, fetchDiffPeriod)
    }

    fun getDefiTvlAsync(coinType: CoinType, currency: String): Single<DefiTvl> {
        return defiMarketsProvider.getDefiTvlAsync(coinType, currency)
    }

    override fun destroy() {
        defiMarketsProvider.destroy()
    }
}
