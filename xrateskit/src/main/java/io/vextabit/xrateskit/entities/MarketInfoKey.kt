package io.vextabit.xrateskit.entities

import io.vextabit.coinkit.models.CoinType

data class MarketInfoKey(
        val coinType: CoinType,
        val currency: String
)

