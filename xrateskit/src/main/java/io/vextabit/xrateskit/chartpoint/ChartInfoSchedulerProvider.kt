package io.vextabit.xrateskit.chartpoint

import io.vextabit.xrateskit.providers.ProviderError
import io.vextabit.xrateskit.core.IChartInfoProvider
import io.vextabit.xrateskit.entities.ChartInfoKey
import io.vextabit.xrateskit.scheduler.ISchedulerProvider
import io.reactivex.Single

class ChartInfoSchedulerProvider(
    override val retryInterval: Long,
    private val key: ChartInfoKey,
    private val provider: IChartInfoProvider,
    private val manager: ChartInfoManager
) : ISchedulerProvider {

    override val id = key.toString()

    override val lastSyncTimestamp: Long?
        get() = manager.getLastSyncTimestamp(key)

    override val expirationInterval: Long
        get() = key.chartType.seconds

    override val syncSingle: Single<Unit>
        get() = provider.getChartPointsAsync(key)
            .doOnSuccess { points ->
                manager.update(points, key)
            }
            .doOnError {
                if (it is ProviderError.NoDataForCoin) {
                    manager.handleNoChartPoints(key)
                }
            }
            .map { Unit }

    override fun notifyExpired() = Unit

}
