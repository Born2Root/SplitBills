package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.Amount
import org.weilbach.splitbills.util.EspressoIdlingResource
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class AmountsRepository private constructor(
        private val amountsLocalDataSource: AmountsDataSource
) : AmountsDataSource {

    var cachedAmounts: LinkedHashMap<String, Amount> = LinkedHashMap()
    var cachedAmountsByBillId: LinkedHashMap<String, List<Amount>> = LinkedHashMap()
    var cachedValidAmountsByBillId: LinkedHashMap<String, Amount> = LinkedHashMap()

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
            override fun onAmountsLoaded(amounts: List<Amount>) {
                refreshCache(amounts)
                EspressoIdlingResource.decrement()
                callback.onAmountsLoaded(ArrayList(cachedAmounts.values))
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getAmountByBillId(billId: String, callback: AmountsDataSource.GetAmountsCallback) {
        val amountsInCache = getAmountsWithBillId(billId)

        if (amountsInCache != null
                && amountsInCache.isNotEmpty()
                && !cacheByBillIdIsDirty) {
            callback.onAmountsLoaded(amountsInCache)
        }

        EspressoIdlingResource.increment()

        amountsLocalDataSource.getAmountByBillId(billId, object : AmountsDataSource.GetAmountsCallback {
            override fun onAmountsLoaded(amounts: List<Amount>) {
                cacheByBillIdAndPerform(amounts) {
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

    override fun getValidAmountByBillId(billId: String, callback: AmountsDataSource.GetAmountCallback) {
        val amountInCache = getValidAmountsWithBillId(billId)

        if (amountInCache != null) {
            callback.onAmountLoaded(amountInCache)
        }

        EspressoIdlingResource.increment()

        amountsLocalDataSource.getValidAmountByBillId(billId, object : AmountsDataSource.GetAmountCallback {
            override fun onAmountLoaded(amount: Amount) {
                cacheValidByBillIdAndPerform(amount) {
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

    override fun saveAmount(amount: Amount) {
        cacheAndPerform(amount) {
            amountsLocalDataSource.saveAmount(amount)
        }
    }

    override fun deleteAllAmounts() {
        amountsLocalDataSource.deleteAllAmounts()
        cachedAmounts.clear()
        cachedAmountsByBillId.clear()
        cachedValidAmountsByBillId.clear()
    }

    override fun refreshAmounts() {
        cacheIsDirty = true
        cacheByBillIdIsDirty = true
        cacheValidByBillIdIsDirty = true
    }

    private fun refreshCache(amounts: List<Amount>) {
        cachedAmounts.clear()
        amounts.forEach {
            cacheAndPerform(it) { }
        }
        cacheIsDirty = false
    }

    private fun refreshCacheByBillId(amountsList: List<List<Amount>>) {
        cachedAmountsByBillId.clear()
        amountsList.forEach {
            cacheByBillIdAndPerform(it) { }
        }
        cacheByBillIdIsDirty = false
    }

    private fun refreshCacheValidByBillId(amounts: List<Amount>) {
        cachedValidAmountsByBillId.clear()
        amounts.forEach {
            cacheValidByBillIdAndPerform(it) { }
        }
        cacheValidByBillIdIsDirty = false
    }

    private inline fun cacheAndPerform(amount: Amount, perform: (Amount) -> Unit) {
        val cachedAmount = Amount(amount.billId, amount.amount, amount.valid)
        cachedAmounts[cachedAmount.id] = cachedAmount
        perform(cachedAmount)
    }

    private inline fun cacheByBillIdAndPerform(amounts: List<Amount>, perform: (List<Amount>) -> Unit) {
        val cachedAmounts = LinkedList<Amount>()
        amounts.forEach { amount ->
            cachedAmounts.add(Amount(amount.billId, amount.amount, amount.valid))
        }
        cachedAmountsByBillId[cachedAmounts[0].id] = cachedAmounts
        perform(cachedAmounts)
    }

    private inline fun cacheValidByBillIdAndPerform(amount: Amount, perform: (Amount) -> Unit) {
        val cachedAmount = Amount(amount.billId, amount.amount, amount.valid)
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
}