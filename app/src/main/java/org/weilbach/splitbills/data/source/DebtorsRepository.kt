/*
package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.DebtorData
import org.weilbach.splitbills.util.EspressoIdlingResource
import java.util.*

class DebtorsRepository private constructor(
        private val debtorsLocalDataSource: DebtorsDataSource
) : DebtorsDataSource {

    var cachedDebtorData: LinkedList<DebtorData> = LinkedList()
    var cachedDebtorsByBillId: LinkedHashMap<String, List<DebtorData>> = LinkedHashMap()

    var cacheIsDirty = false
    var cacheByBillIdIsDirty = false


    override fun getDebtors(callback: DebtorsDataSource.GetDebtorsCallback) {
        if (cachedDebtorData.isNotEmpty() && !cacheIsDirty) {
            callback.onDebtorsLoaded(cachedDebtorData)
            return
        }

        EspressoIdlingResource.increment()

        debtorsLocalDataSource.getDebtors(object : DebtorsDataSource.GetDebtorsCallback {
            override fun onDebtorsLoaded(debtorData: List<DebtorData>) {
                refreshCache(debtorData)
                EspressoIdlingResource.decrement()
                callback.onDebtorsLoaded(cachedDebtorData)
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getDebtorsByBillId(billId: String, callback: DebtorsDataSource.GetDebtorsCallback) {
        val debtorsInCache = getDebtorsWithBillId(billId)

        if (debtorsInCache != null
                && debtorsInCache.isNotEmpty()
                && !cacheByBillIdIsDirty) {
            callback.onDebtorsLoaded(debtorsInCache)
            return
        }

        EspressoIdlingResource.increment()

        debtorsLocalDataSource.getDebtorsByBillId(billId, object : DebtorsDataSource.GetDebtorsCallback {
            override fun onDebtorsLoaded(debtorData: List<DebtorData>) {
                cacheByBillIdAndPerform(debtorData) {
                    EspressoIdlingResource.decrement()
                    callback.onDebtorsLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getDebtorsByBillIdSync(billId: String): List<DebtorData> {
        val debtorsInCache = getDebtorsWithBillId(billId)

        if (debtorsInCache != null
                && debtorsInCache.isNotEmpty()
                && !cacheByBillIdIsDirty) {
            return debtorsInCache
        }

        val debtors = debtorsLocalDataSource.getDebtorsByBillIdSync(billId)

        if (debtors.isNotEmpty()) {
            cacheByBillIdAndPerform(debtors) {}
        }

        return debtors
    }

    override fun saveDebtor(debtorData: DebtorData) {
        cacheAndPerform(debtorData) {
            debtorsLocalDataSource.saveDebtor(debtorData)
        }
    }

    override fun saveDebtorSync(debtorData: DebtorData) {
        cacheAndPerform(debtorData) {
            debtorsLocalDataSource.saveDebtorSync(debtorData)
        }
    }

    override fun deleteAllDebtors() {
        debtorsLocalDataSource.deleteAllDebtors()
        cachedDebtorData.clear()
        cachedDebtorsByBillId.clear()
    }

    override fun deleteDebtorsByBillId(billId: String) {
        debtorsLocalDataSource.deleteDebtorsByBillId(billId)
        cachedDebtorData.clear()
        cachedDebtorsByBillId.clear()
    }

    override fun deleteDebtorsByBillIdSync(billId: String) {
        debtorsLocalDataSource.deleteDebtorsByBillIdSync(billId)
        cachedDebtorData.clear()
        cachedDebtorsByBillId.clear()
    }

    override fun refreshDebtors() {
        cacheIsDirty = true
        cacheByBillIdIsDirty = true
    }

    private fun refreshCache(debtorData: List<DebtorData>) {
        cachedDebtorData.clear()
        cachedDebtorsByBillId.clear()
        debtorData.forEach {
            cacheAndPerform(it) { }
        }
        cacheIsDirty = false
    }

    private inline fun cacheAndPerform(debtorData: DebtorData, perform: (DebtorData) -> Unit) {
        val cachedDebtor = DebtorData(debtorData.billId, debtorData.memberEmail)
        cachedDebtorData.add(cachedDebtor)
        perform(cachedDebtor)
    }

    private inline fun cacheByBillIdAndPerform(debtorData: List<DebtorData>, perform: (List<DebtorData>) -> Unit) {
        val cachedDebtors = LinkedList<DebtorData>()
        debtorData.forEach { debtor ->
            cachedDebtors.add(DebtorData(debtor.billId, debtor.memberEmail))
        }
        cachedDebtorsByBillId[cachedDebtors[0].billId] = cachedDebtors
        perform(cachedDebtors)
    }

    private fun getDebtorsWithBillId(id: String) = cachedDebtorsByBillId[id]

    companion object {
        private var INSTANCE: DebtorsRepository? = null

        @JvmStatic
        fun getInstance(debtorsLocalDataSource: DebtorsDataSource) =
                INSTANCE ?: synchronized(DebtorsRepository::class.java) {
                    INSTANCE ?: DebtorsRepository(debtorsLocalDataSource)
                            .also { INSTANCE = it }
                }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}*/
