package io.vextabit.xrateskit.rates

import io.vextabit.xrateskit.core.ILatestRateProvider
import io.vextabit.xrateskit.scheduler.Scheduler

class LatestRatesSchedulerFactory(
    private val manager: LatestRatesManager,
    private val provider: ILatestRateProvider,
    private val expirationInterval: Long,
    private val retryInterval: Long) {

    fun getScheduler(currencyCode: String, coinTypeDataSource: ILatestRatesCoinTypeDataSource): Scheduler {
        val schedulerProvider = LatestRatesSchedulerProvider(
            manager,
            provider,
            currencyCode,
            expirationInterval,
            retryInterval
        )
        schedulerProvider.dataSource = coinTypeDataSource

        return Scheduler(schedulerProvider, 5)
    }
}
