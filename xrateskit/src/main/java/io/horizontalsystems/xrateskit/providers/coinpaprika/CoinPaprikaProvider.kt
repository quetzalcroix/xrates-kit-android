package io.horizontalsystems.xrateskit.providers.coinpaprika

import io.horizontalsystems.xrateskit.core.IGlobalCoinMarketProvider
import io.horizontalsystems.xrateskit.entities.GlobalCoinMarket
import io.horizontalsystems.xrateskit.providers.InfoProvider
import io.horizontalsystems.xrateskit.utils.RetrofitUtils
import io.reactivex.Single

class CoinPaprikaProvider : IGlobalCoinMarketProvider {

    override val provider: InfoProvider = InfoProvider.CoinPaprika()
    private val BTC_ID = "btc-bitcoin"
    private val HOURS_24_IN_SECONDS = 86400

    private val coinPaprikaService: CoinPaprikaService by lazy {
        RetrofitUtils.build(provider.baseUrl).create(CoinPaprikaService::class.java)
    }


    override fun initProvider() {}
    override fun destroy() {}


    override fun getGlobalCoinMarketsAsync(currencyCode: String): Single<GlobalCoinMarket> {

        return Single.zip(
            getMarketOverviewData(currencyCode),
            getMarketCap(BTC_ID, (System.currentTimeMillis() / 1000) - HOURS_24_IN_SECONDS),
            { globalMarketInfo, btcMarketCap ->
                val openingMarketCap = (globalMarketInfo.marketCap.multiply(100.toBigDecimal())) / globalMarketInfo.marketCapDiff24h.plus(100.toBigDecimal())
                val openingBtcDominanceDiff = (btcMarketCap * 100) / openingMarketCap.toDouble()
                globalMarketInfo.btcDominanceDiff24h = globalMarketInfo.btcDominance - openingBtcDominanceDiff.toBigDecimal()

                globalMarketInfo
            })
    }

    private fun getMarketOverviewData(currency: String): Single<GlobalCoinMarket> {
        return coinPaprikaService.global()
            .map {
                GlobalCoinMarket(
                    currency,
                    it.volume_24h_usd,
                    it.volume_24h_change_24h,
                    it.market_cap_usd,
                    it.market_cap_change_24h,
                    it.bitcoin_dominance_percentage
                )
            }
    }

    private fun getMarketCap(coinId: String = BTC_ID, timeStamp: Long): Single<Double> {
        return coinPaprikaService.historicalOhlc(coinId, timeStamp)
            .map {
                it.first().market_cap
            }
    }
}
