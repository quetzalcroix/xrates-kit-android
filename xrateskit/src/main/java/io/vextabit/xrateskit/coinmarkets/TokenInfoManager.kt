package io.vextabit.xrateskit.coinmarkets

import io.vextabit.coinkit.models.CoinType
import io.vextabit.xrateskit.core.*
import io.vextabit.xrateskit.entities.*
import io.reactivex.Single

class TokenInfoManager(
    private val storage: IStorage,
    private val tokenInfoProvider: ITokenInfoProvider,
    private val auditInfoProvider: IAuditInfoProvider
) : IInfoManager {

    private val AUDIT_DATA_LIFETIME_SECONDS = 144000 // 24 hours

    fun getTopTokenHoldersAsync(coinType: CoinType, itemsCount: Int): Single<List<TokenHolder>> {
        return tokenInfoProvider.getTopTokenHoldersAsync(coinType, itemsCount)
    }

    fun getAuditReportsAsync(coinType: CoinType): Single<List<Auditor>> {

        val currentTimestamp = System.currentTimeMillis()/1000
        val auditTimestamp = storage.getResourceInfo(ResourceType.AUDIT_INFO_TIMESTAMP)?.updatedAt ?: 0

        storage.getAuditReports(coinType).let { data ->
            if (data.isNotEmpty()) {
                if((currentTimestamp - auditTimestamp ) <= AUDIT_DATA_LIFETIME_SECONDS )
                    return Single.just(data)
                else
                    storage.deleteCoinAuditReports(coinType)
            }
        }

        return auditInfoProvider.getAuditReportsAsync(coinType).map { response ->

            storage.saveAuditReports(coinType, response)
            storage.saveResourceInfo(ResourceInfo(ResourceType.AUDIT_INFO_TIMESTAMP, "", System.currentTimeMillis()/1000))

            response
        }
    }

    override fun destroy() {}
}
