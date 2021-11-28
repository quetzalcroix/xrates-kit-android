package io.vextabit.xrateskit.coins.provider

import io.vextabit.xrateskit.entities.ProviderCoinsResource
import io.vextabit.xrateskit.entities.ResourceInfo
import io.vextabit.xrateskit.utils.OkHttpUtils

class DataProviderCoinExternalIdsRemote(private val path: String) : DataProvider<ProviderCoinsResource> {

    override fun getDataNewerThan(resourceInfo: ResourceInfo?): Data<ProviderCoinsResource>? {
        val response = OkHttpUtils.get(path, resourceInfo?.versionId)

        return when (response.code) {
            200 -> response.body?.byteStream()?.use {
                val etag = response.header("etag") ?: ""
                Data(etag, ProviderCoinsResource.parseFile(false, it))
            }
            else -> null
        }
    }

}
