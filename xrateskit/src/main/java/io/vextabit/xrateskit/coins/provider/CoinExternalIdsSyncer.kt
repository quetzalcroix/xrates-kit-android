package io.vextabit.xrateskit.coins.provider

import io.vextabit.xrateskit.entities.ProviderCoinsResource
import io.vextabit.xrateskit.entities.ResourceInfo
import io.vextabit.xrateskit.entities.ResourceType
import io.vextabit.xrateskit.storage.Storage

class CoinExternalIdsSyncer(
    private val dataProvider: DataProvider<ProviderCoinsResource>,
    private val storage: Storage
) {
    fun sync() {
        val resourceInfo = storage.getResourceInfo(ResourceType.PROVIDER_COINS)

        dataProvider.getDataNewerThan(resourceInfo)?.let {
            storage.saveProviderCoins(it.value.providerCoins)
            storage.saveResourceInfo(ResourceInfo(ResourceType.PROVIDER_COINS, it.versionId))
        }
    }

}
