package io.vextabit.xrateskit

import android.content.Context
import io.horizontalsystems.coinkit.models.CoinType
import io.vextabit.xrateskit.chartpoint.ChartInfoManager
import io.vextabit.xrateskit.chartpoint.ChartInfoSchedulerFactory
import io.vextabit.xrateskit.chartpoint.ChartInfoSyncManager
import io.vextabit.xrateskit.coinmarkets.CoinMarketsManager
import io.vextabit.xrateskit.coinmarkets.DefiMarketsManager
import io.vextabit.xrateskit.coinmarkets.GlobalMarketInfoManager
import io.vextabit.xrateskit.coinmarkets.TokenInfoManager
import io.vextabit.xrateskit.coins.CoinInfoManager
import io.vextabit.xrateskit.coins.CoinSyncer
import io.vextabit.xrateskit.coins.ProviderCoinsManager
import io.vextabit.xrateskit.coins.provider.*
import io.vextabit.xrateskit.core.Factory
import io.vextabit.xrateskit.cryptonews.CryptoNewsManager
import io.vextabit.xrateskit.entities.*
import io.vextabit.xrateskit.providers.*
import io.vextabit.xrateskit.providers.coingecko.CoinGeckoProvider
import io.vextabit.xrateskit.providers.cryptocompare.CryptoCompareProvider
import io.vextabit.xrateskit.providers.defiyield.DefiYieldProvider
import io.vextabit.xrateskit.providers.horsys.HorsysProvider
import io.vextabit.xrateskit.rates.HistoricalRateManager
import io.vextabit.xrateskit.rates.LatestRatesManager
import io.vextabit.xrateskit.rates.LatestRatesSchedulerFactory
import io.vextabit.xrateskit.rates.LatestRatesSyncManager
import io.vextabit.xrateskit.storage.Database
import io.vextabit.xrateskit.storage.Storage
import io.reactivex.Observable
import io.reactivex.Single
import java.math.BigDecimal

class XRatesKit(
    private val latestRatesManager: LatestRatesManager,
    private val latestRatesSyncManager: LatestRatesSyncManager,
    private val chartInfoManager: ChartInfoManager,
    private val chartInfoSyncManager: ChartInfoSyncManager,
    private val historicalRateManager: HistoricalRateManager,
    private val cryptoNewsManager: CryptoNewsManager,
    private val coinMarketManager: CoinMarketsManager,
    private val globalMarketInfoManager: GlobalMarketInfoManager,
    private val defiMarketsManager: DefiMarketsManager,
    private val coinInfoManager: CoinInfoManager,
    private val providerCoinsManager: ProviderCoinsManager,
    private val tokenInfoManager: TokenInfoManager,

    coinSyncer: CoinSyncer
) {

    init {
        coinSyncer.sync()
    }

    fun getNotificationCoinCode(coinType: CoinType): String? {
        return providerCoinsManager.getProviderIds(listOf(coinType), InfoProvider.CryptoCompare()).firstOrNull()
    }

    fun refresh(currencyCode: String) {
        latestRatesSyncManager.refresh(currencyCode)
    }

    fun getLatestRate(coinType: CoinType, currencyCode: String): LatestRate? {
        return latestRatesManager.getLatestRate(coinType, currencyCode)
    }

    fun getLatestRateMap(coinTypes: List<CoinType>, currencyCode: String): Map<CoinType, LatestRate> {
        return latestRatesManager.getLatestRates(coinTypes, currencyCode)
    }

    fun getLatestRateAsync(coinType: CoinType, currencyCode: String): Observable<LatestRate> {
        return latestRatesSyncManager.getLatestRateAsync(PairKey(coinType, currencyCode))
    }

    fun latestRateMapObservable(coinTypes: List<CoinType>, currencyCode: String): Observable<Map<CoinType, LatestRate>> {
        return latestRatesSyncManager.getLatestRatesAsync(coinTypes, currencyCode)
    }

    fun getChartInfo(coinType: CoinType, currencyCode: String, chartType: ChartType): ChartInfo? {
        return chartInfoManager.getChartInfo(ChartInfoKey(coinType, currencyCode, chartType))
    }

    fun chartInfoObservable(coinType: CoinType, currencyCode: String, chartType: ChartType): Observable<ChartInfo> {
        return chartInfoSyncManager.chartInfoObservable(ChartInfoKey(coinType, currencyCode, chartType))
    }

    fun getHistoricalRate(coinType: CoinType, currencyCode: String, timestamp: Long): BigDecimal? {
        return historicalRateManager.getHistoricalRate(coinType, currencyCode, timestamp)
    }

    fun getHistoricalRateAsync(coinType: CoinType, currencyCode: String, timestamp: Long): Single<BigDecimal> {
        return historicalRateManager.getHistoricalRateAsync(coinType, currencyCode, timestamp)
    }

    fun cryptoNewsAsync(latestTimestamp: Long? = null): Single<List<CryptoNews>> {
        return cryptoNewsManager.getNewsAsync(latestTimestamp)
    }

    fun getTopTokenHoldersAsync(coinType: CoinType, itemsCount: Int = 20): Single<List<TokenHolder>> {
        return tokenInfoManager.getTopTokenHoldersAsync(coinType, itemsCount)
    }

    fun getAuditReportsAsync(coinType: CoinType): Single<List<Auditor>> {
        return tokenInfoManager.getAuditReportsAsync(coinType)
    }

    fun getTopCoinMarketsAsync(currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24, itemsCount: Int = 200): Single<List<CoinMarket>> {
        return coinMarketManager.getTopCoinMarketsAsync(currencyCode, fetchDiffPeriod, itemsCount)
    }

    fun getTopDefiMarketsAsync(currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24, itemsCount: Int = 200): Single<List<CoinMarket>> {
        return defiMarketsManager.getTopDefiMarketsAsync(currencyCode, fetchDiffPeriod, itemsCount)
    }

    fun getTopDefiTvlAsync(currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24, itemsCount: Int = 200, chain: String? = null): Single<List<DefiTvl>> {
        return defiMarketsManager.getTopDefiTvlAsync(currencyCode, fetchDiffPeriod, itemsCount, chain)
    }

    fun getDefiTvlPointsAsync(coinType: CoinType, currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24): Single<List<DefiTvlPoint>> {
        return defiMarketsManager.getDefiTvlPointsAsync(coinType, currencyCode, fetchDiffPeriod)
    }

    fun getCoinMarketPointsAsync(coinType: CoinType, currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24): Single<List<CoinMarketPoint>> {
        return coinMarketManager.getCoinMarketPointsAsync(coinType, currencyCode, fetchDiffPeriod)
    }

    fun getDefiTvlAsync(coinType: CoinType, currencyCode: String): Single<DefiTvl> {
        return defiMarketsManager.getDefiTvlAsync(coinType, currencyCode)
    }

    fun getCoinMarketsAsync(coinTypes: List<CoinType>, currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24): Single<List<CoinMarket>> {
        return coinMarketManager.getCoinMarketsAsync(coinTypes , currencyCode, fetchDiffPeriod)
    }

    fun getCoinRatingsAsync(): Single<Map<CoinType, String>> {
        return coinInfoManager.getCoinRatingsAsync()
    }

    fun getCoinMarketsByCategoryAsync(categoryId: String, currencyCode: String, fetchDiffPeriod: TimePeriod = TimePeriod.HOUR_24): Single<List<CoinMarket>> {
        val coinCodes = coinInfoManager.getCoinCodesByCategory(categoryId)
        return coinMarketManager.getCoinMarketsAsync(coinCodes , currencyCode, fetchDiffPeriod)
    }

    fun getCoinMarketDetailsAsync(coinType: CoinType, currencyCode: String, rateDiffCoinCodes: List<String>, rateDiffPeriods: List<TimePeriod>): Single<CoinMarketDetails> {
        return coinMarketManager.getCoinMarketDetailsAsync(coinType, currencyCode, rateDiffCoinCodes, rateDiffPeriods)
    }

    fun getGlobalCoinMarketsAsync(currencyCode: String, timePeriod: TimePeriod = TimePeriod.HOUR_24): Single<GlobalCoinMarket> {
        return globalMarketInfoManager.getGlobalMarketInfo(currencyCode, timePeriod)
    }

    fun getGlobalCoinMarketPointsAsync(currencyCode: String, timePeriod: TimePeriod = TimePeriod.HOUR_24): Single<List<GlobalCoinMarketPoint>> {
        return globalMarketInfoManager.getGlobalMarketPoints(currencyCode, timePeriod)
    }

    fun searchCoins(searchText: String): List<CoinData> {
        return providerCoinsManager.searchCoins(searchText)
    }

    fun clear(){
        coinMarketManager.destroy()
        globalMarketInfoManager.destroy()
    }

    companion object {
        fun create(context: Context, rateExpirationInterval: Long = 60L, retryInterval: Long = 30, cryptoCompareApiKey: String = "", defiyieldProviderApiKey: String, coinsRemoteUrl: String?, providerCoinsRemoteUrl: String?): io.vextabit.xrateskit.XRatesKit {
            val factory = Factory(rateExpirationInterval)
            val storage = Storage(Database.create(context))

            val coinInfoResourceProvider =
                io.vextabit.xrateskit.XRatesKit.Companion.buildCoinInfoResourceProvider(
                    context,
                    coinsRemoteUrl
                )
            val coinInfoSyncer = CoinInfoSyncer(coinInfoResourceProvider, storage)
            val coinInfoManager = CoinInfoManager(storage, coinInfoSyncer)

            val providerCoinsResourceProvider =
                io.vextabit.xrateskit.XRatesKit.Companion.buildProviderCoinsResourceProviderImpl(
                    context,
                    providerCoinsRemoteUrl
                )
            val coinExternalIdsSyncer = CoinExternalIdsSyncer(providerCoinsResourceProvider, storage)
            val providerCoinsManager = ProviderCoinsManager(storage, coinExternalIdsSyncer)

            val coinGeckoProvider = CoinGeckoProvider(factory, coinInfoManager, providerCoinsManager)
            providerCoinsManager.coinGeckoProvider = coinGeckoProvider
            val cryptoCompareProvider = CryptoCompareProvider(factory, cryptoCompareApiKey)
            val horsysProvider = HorsysProvider(providerCoinsManager)
            val defiYieldProvider = DefiYieldProvider(defiyieldProviderApiKey)
            val globalMarketInfoManager = GlobalMarketInfoManager(horsysProvider, storage)
            val defiMarketInfoManager = DefiMarketsManager(coinGeckoProvider, horsysProvider)

            val historicalRateManager = HistoricalRateManager(storage, coinGeckoProvider)
            val cryptoNewsManager = CryptoNewsManager(cryptoCompareProvider)

            val latestRatesManager = LatestRatesManager(storage, factory)
            val latestRatesSchedulerFactory = LatestRatesSchedulerFactory(latestRatesManager, coinGeckoProvider, rateExpirationInterval, retryInterval)
            val latestRatesSyncManager = LatestRatesSyncManager(latestRatesSchedulerFactory).also {
                latestRatesManager.listener = it
            }

            val chartInfoManager = ChartInfoManager(storage, factory)
            val chartInfoSchedulerFactory = ChartInfoSchedulerFactory(chartInfoManager, coinGeckoProvider, retryInterval)
            val chartInfoSyncManager = ChartInfoSyncManager(chartInfoSchedulerFactory).also {
                chartInfoManager.listener = it
            }

            val topMarketsManager = CoinMarketsManager(coinGeckoProvider, horsysProvider)
            val tokenInfoManager = TokenInfoManager(storage, horsysProvider, defiYieldProvider)

            val coinSyncer = CoinSyncer(providerCoinsManager, coinInfoManager)

            return io.vextabit.xrateskit.XRatesKit(
                latestRatesManager,
                latestRatesSyncManager,
                chartInfoManager,
                chartInfoSyncManager,
                historicalRateManager,
                cryptoNewsManager,
                topMarketsManager,
                globalMarketInfoManager,
                defiMarketInfoManager,
                coinInfoManager,
                providerCoinsManager,
                tokenInfoManager,
                coinSyncer
            )
        }

        private fun buildProviderCoinsResourceProviderImpl(context: Context, providerCoinsRemoteUrl: String?): DataProvider<ProviderCoinsResource> {
            val provider = DataProviderChain<ProviderCoinsResource>()
            provider.addProvider(DataProviderCoinExternalIdsLocal(context))
            providerCoinsRemoteUrl?.let {
                provider.addProvider(DataProviderCoinExternalIdsRemote(providerCoinsRemoteUrl))
            }

            return provider
        }

        private fun buildCoinInfoResourceProvider(context: Context, coinsRemoteUrl: String?): DataProvider<CoinInfoResource> {
            val provider = DataProviderChain<CoinInfoResource>()
            provider.addProvider(DataProviderCoinsInfoLocal(context))
            coinsRemoteUrl?.let {
                provider.addProvider(DataProviderCoinsInfoRemote(coinsRemoteUrl))
            }
            return provider
        }
    }
}
