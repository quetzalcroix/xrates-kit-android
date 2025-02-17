package io.vextabit.xrateskit.coinmarkets

import io.vextabit.coinkit.models.CoinType
import io.vextabit.xrateskit.providers.coingecko.CoinGeckoProvider
import io.vextabit.xrateskit.core.*
import io.vextabit.xrateskit.entities.*
import io.reactivex.Single
import java.math.BigDecimal
import java.math.RoundingMode

class CoinMarketsManager(
    private val coinGeckoProvider: CoinGeckoProvider,
    private val defiMarketsProvider: IDefiMarketsProvider
): IInfoManager, ICoinMarketManager {

    override fun getTopCoinMarketsAsync(currency: String, fetchDiffPeriod: TimePeriod, itemsCount: Int): Single<List<CoinMarket>> {
        return coinGeckoProvider.getTopCoinMarketsAsync(currency, fetchDiffPeriod, itemsCount)
    }

    override fun getCoinMarketsAsync(coinTypes: List<CoinType>, currencyCode: String, fetchDiffPeriod: TimePeriod): Single<List<CoinMarket>> {
        return coinGeckoProvider.getCoinMarketsAsync(coinTypes, currencyCode, fetchDiffPeriod)
    }

    override fun getCoinMarketPointsAsync(coinType: CoinType, currencyCode: String, fetchDiffPeriod: TimePeriod): Single<List<CoinMarketPoint>> {
        return coinGeckoProvider.getCoinMarketPointsAsync(coinType, currencyCode, fetchDiffPeriod)
    }

    override fun getCoinMarketDetailsAsync(coinType: CoinType, currencyCode: String, rateDiffCoinCodes: List<String>, rateDiffPeriods: List<TimePeriod>): Single<CoinMarketDetails> {
        return Single.zip(
            coinGeckoProvider.getCoinMarketDetailsAsync(coinType, currencyCode, rateDiffCoinCodes, rateDiffPeriods),
            // on Error return empty object with ZERO TVL
            defiMarketsProvider.getDefiTvlAsync(coinType, currencyCode).onErrorReturnItem(DefiTvl(CoinData(coinType,"",""), BigDecimal.ZERO, BigDecimal.ZERO, 0, null))
        ){ coinMarketDetails, defiTvlDetails ->

            if(defiTvlDetails.tvl.compareTo(BigDecimal.ZERO) != 0) {
                coinMarketDetails.defiTvlInfo = DefiTvlInfo(
                    defiTvlDetails.tvl,
                    defiTvlDetails.tvlRank,
                    coinMarketDetails.marketCap.divide(defiTvlDetails.tvl, 2, RoundingMode.HALF_UP)
                )
            }
            coinMarketDetails
        }
    }

    override fun destroy() {
        coinGeckoProvider.destroy()
    }
}
