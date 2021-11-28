package io.vextabit.xrateskit.chartpoint

import io.vextabit.xrateskit.core.IChartInfoProvider
import io.vextabit.xrateskit.entities.ChartInfoKey
import io.vextabit.xrateskit.scheduler.Scheduler

class ChartInfoSchedulerFactory(
        private val manager: ChartInfoManager,
        private val provider: IChartInfoProvider,
        private val retryInterval: Long) {

    fun getScheduler(key: ChartInfoKey): Scheduler {
        return Scheduler(ChartInfoSchedulerProvider(retryInterval, key, provider, manager))
    }
}
