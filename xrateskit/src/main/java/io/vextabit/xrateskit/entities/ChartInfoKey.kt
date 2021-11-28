package io.vextabit.xrateskit.entities

import io.vextabit.coinkit.models.CoinType

data class ChartInfoKey(
        val coinType: CoinType,
        val currency: String,
        val chartType: ChartType
)

