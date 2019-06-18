//package org.weilbach.splitbills.data.source
/*

import org.weilbach.splitbills.util.EspressoIdlingResource
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
*/

/*
class AmountsRepository private constructor(
        private val amountsLocalDataSource: AmountsDataSource
) : AmountsDataSource {

    var cachedAmounts: LinkedHashMap<String, AmountData> = LinkedHashMap()
    var cachedAmountsByBillId: LinkedHashMap<String, List<AmountData>> = LinkedHashMap()
    var cachedValidAmountsByBillId: LinkedHashMap<String, AmountData> = LinkedHashMap()

    var cacheIsDirty = false
    var cacheByBillIdIsDirty = false
    var cacheValidByBillIdIsDirty = false

    override fun getAmounts(callback: AmountsDataSource.GetAmountsCallback) {
        if (cachedAmounts.isNotEmpty() && !cacheIsDirty) {
            callback.onAmountsLoaded(ArrayList(cachedAmounts.values))
            return
        }

        EspressoIdlingResource.increment()

        amountsLocalDataSource.getAmounts(object : AmountsDataSource.GetAmountsCallback {
            override fun onAmountsLoaded(amountData: List<AmountData>) {
                refreshCache(amountData)
                EspressoIdlingResource.decrement()
                callback.onAmountsLoaded(ArrayList(cachedAmounts.values))
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getAmountsByBillId(billId: String, callback: AmountsDataSource.GetAmountsCallback) {
        val amountsInCache = getAmountsWithBillId(billId)

        if (amountsInCache != null
                && amountsInCache.isNotEmpty()
                && !cacheByBillIdIsDirty) {
            callback.onAmountsLoaded(amountsInCache)
            return
        }

        EspressoIdlingResource.increment()

        amountsLocalDataSource.getAmountsByBillId(billId, object : AmountsDataSource.GetAmountsCallback {
            override fun onAmountsLoaded(amountData: List<AmountData>) {
                cacheByBillIdAndPerform(amountData) {
                    EspressoIdlingResource.decrement()
                    callback.onAmountsLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getAmountsByBillIdSync(billId: String): List<AmountData> {
        val amountsInCache = getAmountsWithBillId(billId)

        if (amountsInCache != null
                && amountsInCache.isNotEmpty()
                && !cacheByBillIdIsDirty) {
            return amountsInCache
        }

        val amounts = amountsLocalDataSource.getAmountsByBillIdSync(billId)

        if (amounts.isNotEmpty()) {
            cacheByBillIdAndPerform(amounts) {}
        }

        return amounts
    }

    override fun getValidAmountByBillId(billId: String, callback: AmountsDataSource.GetAmountCallback) {
        val amountInCache = getValidAmountsWithBillId(billId)

        if (amountInCache != null) {
            callback.onAmountLoaded(amountInCache)
            return
        }

        EspressoIdlingResource.increment()

        amountsLocalDataSource.getValidAmountByBillId(billId, object : AmountsDataSource.GetAmountCallback {
            override fun onAmountLoaded(amountData: AmountData) {
                cacheValidByBillIdAndPerform(amountData) {
                    EspressoIdlingResource.decrement()
                    callback.onAmountLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getValidAmountByBillIdSync(billId: String): AmountData? {
        val amountInCache = getValidAmountsWithBillId(billId)

        if (amountInCache != null) {
            return amountInCache
        }

        val amount = amountsLocalDataSource.getValidAmountByBillIdSync(billId)

        if (amount != null) {
            cacheValidByBillIdAndPerform(amount) {}
        }

        return amount
    }

    override fun saveAmount(amountData: AmountData) {
        cacheAndPerform(amountData) {
            amountsLocalDataSource.saveAmount(amountData)
        }
    }

    override fun saveAmountSync(amountData: AmountData) {
        cacheAndPerform(amountData) {
            amountsLocalDataSource.saveAmountSync(amountData)
        }
    }

    override fun deleteAllAmounts() {
        amountsLocalDataSource.deleteAllAmounts()
        cachedAmounts.clear()
        cachedAmountsByBillId.clear()
        cachedValidAmountsByBillId.clear()
    }

    override fun deleteAmountsByBillId(billId: String) {
        amountsLocalDataSource.deleteAmountsByBillId(billId)
        cachedAmounts.clear()
        cachedAmountsByBillId.clear()
        cachedValidAmountsByBillId.clear()
    }

    override fun deleteAmountsByBillIdSync(billId: String) {
        amountsLocalDataSource.deleteAmountsByBillIdSync(billId)
        cachedAmounts.clear()
        cachedAmountsByBillId.clear()
        cachedValidAmountsByBillId.clear()
    }

    override fun refreshAmounts() {
        cacheIsDirty = true
        cacheByBillIdIsDirty = true
        cacheValidByBillIdIsDirty = true
    }

    private fun refreshCache(amountData: List<AmountData>) {
        cachedAmountsByBillId.clear()
        cachedValidAmountsByBillId.clear()
        cachedAmounts.clear()
        amountData.forEach {
            cacheAndPerform(it) { }
        }
        cacheIsDirty = false
    }

    private fun refreshCacheByBillId(amountsList: List<List<AmountData>>) {
        cachedAmountsByBillId.clear()
        amountsList.forEach {
            cacheByBillIdAndPerform(it) { }
        }
        cacheByBillIdIsDirty = false
    }

    private fun refreshCacheValidByBillId(amountData: List<AmountData>) {
        cachedValidAmountsByBillId.clear()
        amountData.forEach {
            cacheValidByBillIdAndPerform(it) { }
        }
        cacheValidByBillIdIsDirty = false
    }

    private inline fun cacheAndPerform(amountData: AmountData, perform: (AmountData) -> Unit) {
        val cachedAmount = AmountData(amountData.billId, amountData.amount, amountData.currency, amountData.valid)
        cachedAmounts[cachedAmount.id] = cachedAmount
        perform(cachedAmount)
    }

    private inline fun cacheByBillIdAndPerform(amountData: List<AmountData>, perform: (List<AmountData>) -> Unit) {
        val cachedAmounts = LinkedList<AmountData>()
        amountData.forEach { amount ->
            cachedAmounts.add(AmountData(amount.billId, amount.amount, amount.currency, amount.valid))
        }
        cachedAmountsByBillId[cachedAmounts[0].id] = cachedAmounts
        perform(cachedAmounts)
    }

    private inline fun cacheValidByBillIdAndPerform(amountData: AmountData, perform: (AmountData) -> Unit) {
        val cachedAmount = AmountData(amountData.billId, amountData.amount, amountData.currency, amountData.valid)
        cachedValidAmountsByBillId[cachedAmount.id] = cachedAmount
        perform(cachedAmount)
    }

    private fun getAmountWithId(id: String) = cachedAmounts[id]

    private fun getAmountsWithBillId(id: String) = cachedAmountsByBillId[id]

    private fun getValidAmountsWithBillId(id: String) = cachedValidAmountsByBillId[id]

    companion object {
        private var INSTANCE: AmountsRepository? = null

        @JvmStatic
        fun getInstance(amountsLocalDataSource: AmountsDataSource) =
                INSTANCE ?: synchronized(AmountsRepository::class.java) {
                    INSTANCE ?: AmountsRepository(amountsLocalDataSource)
                            .also { INSTANCE = it }
                }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}*/
