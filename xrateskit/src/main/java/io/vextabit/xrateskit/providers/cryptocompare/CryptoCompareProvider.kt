package io.vextabit.xrateskit.providers.cryptocompare

import io.vextabit.xrateskit.core.Factory
import io.vextabit.xrateskit.core.ICryptoNewsProvider
import io.vextabit.xrateskit.core.IInfoProvider
import io.vextabit.xrateskit.entities.CryptoNews
import io.vextabit.xrateskit.providers.InfoProvider
import io.vextabit.xrateskit.utils.RetrofitUtils
import io.reactivex.Single


class CryptoCompareProvider(
    private val factory: Factory,
    private val apiKey: String
) : IInfoProvider, ICryptoNewsProvider {

    private val NEWS_FEEDS = "cointelegraph,theblock,decrypt"
    private val EXTRA_PARAMS = "Blocksdecoded"

    override val provider: InfoProvider = InfoProvider.CryptoCompare()

    private val cryptoCompareService: CryptoCompareService by lazy {
        RetrofitUtils.build(provider.baseUrl).create(CryptoCompareService::class.java)
    }

    override fun initProvider() {}
    override fun destroy() {}

    //  CryptoNews
    override fun getNewsAsync(latestTimestamp: Long?): Single<List<CryptoNews>> {

        return cryptoCompareService.news(apiKey, NEWS_FEEDS, EXTRA_PARAMS, latestTimestamp)
            .map {
                it.Data.map {
                    factory.createCryptoNews(
                        it.id,
                        it.source,
                        it.published_on,
                        it.imageurl,
                        it.title,
                        it.url,
                        it.body,
                        it.categories.split("|")
                    )
                }
            }
    }
}
