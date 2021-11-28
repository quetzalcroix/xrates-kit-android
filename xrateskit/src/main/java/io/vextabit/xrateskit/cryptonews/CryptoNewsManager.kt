package io.vextabit.xrateskit.cryptonews

import io.vextabit.xrateskit.providers.cryptocompare.CryptoCompareProvider
import io.vextabit.xrateskit.entities.CryptoNews
import io.reactivex.Single

class CryptoNewsManager(private val newsProvider: CryptoCompareProvider) {

    fun getNewsAsync(latestTimestamp: Long?): Single<List<CryptoNews>> {
        return newsProvider.getNewsAsync(latestTimestamp)
    }
}