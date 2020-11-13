package io.horizontalsystems.xrateskit.api

import io.horizontalsystems.xrateskit.api.uniswapgraph.UniswapGraphProvider
import io.horizontalsystems.xrateskit.core.IMarketInfoProvider
import io.horizontalsystems.xrateskit.entities.Coin
import io.horizontalsystems.xrateskit.entities.CoinType
import io.horizontalsystems.xrateskit.entities.MarketInfoEntity
import io.reactivex.Single
import io.reactivex.functions.BiFunction

class MarketInfoBaseProvider(
    private val cryptoCompareProvider: CryptoCompareProvider,
    private val uniswapGraphProvider: UniswapGraphProvider
) : IMarketInfoProvider {

    override fun getMarketInfo(coins: List<Coin>, currency: String): Single<List<MarketInfoEntity>> {

        val (ethBasedCoins, baseCoins) = coins.partition {
                coin -> coin.type == CoinType.ETHEREUM || coin.type == CoinType.ERC20
        }

        return Single.zip(
            cryptoCompareProvider.getMarketInfo(baseCoins, currency),
            uniswapGraphProvider.getMarketInfo(ethBasedCoins, currency),
            BiFunction<List<MarketInfoEntity>,List<MarketInfoEntity>, List<MarketInfoEntity>> {
                    t1, t2 -> t1 + t2
            })
    }
}
