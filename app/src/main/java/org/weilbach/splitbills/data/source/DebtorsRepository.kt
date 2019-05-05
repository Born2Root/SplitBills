package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.Debtor
import org.weilbach.splitbills.util.EspressoIdlingResource
import java.util.*

class DebtorsRepository private constructor(
        private val debtorsLocalDataSource: DebtorsDataSource
) : DebtorsDataSource {

    var cachedDebtors: LinkedList<Debtor> = LinkedList()
    var cachedDebtorsByBillId: LinkedHashMap<String, List<Debtor>> = LinkedHashMap()

    var cacheIsDirty = false
    var cacheByBillIdIsDirty = false


    override fun getDebtors(callback: DebtorsDataSource.GetDebtorsCallback) {
        if (cachedDebtors.isNotEmpty() && !cacheIsDirty) {
            callback.onDebtorsLoaded(cachedDebtors)
            return
        }

        EspressoIdlingResource.increment()

        debtorsLocalDataSource.getDebtors(object : DebtorsDataSource.GetDebtorsCallback {
            override fun onDebtorsLoaded(debtors: List<Debtor>) {
                refreshCache(debtors)
                EspressoIdlingResource.decrement()
                callback.onDebtorsLoaded(cachedDebtors)
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
        }

        EspressoIdlingResource.increment()

        debtorsLocalDataSource.getDebtorsByBillId(billId, object : DebtorsDataSource.GetDebtorsCallback {
            override fun onDebtorsLoaded(debtors: List<Debtor>) {
                cacheByBillIdAndPerform(debtors) {
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

    override fun saveDebtor(debtor: Debtor) {
        cacheAndPerform(debtor) {
            debtorsLocalDataSource.saveDebtor(debtor)
        }
    }

    override fun deleteAllDebtors() {
        debtorsLocalDataSource.deleteAllDebtors()
        cachedDebtors.clear()
        cachedDebtorsByBillId.clear()
    }

    override fun refreshDebtors() {
        cacheIsDirty = true
        cacheByBillIdIsDirty = true
    }

    private fun refreshCache(debtors: List<Debtor>) {
        cachedDebtors.clear()
        debtors.forEach {
            cacheAndPerform(it) { }
        }
        cacheIsDirty = false
    }

    private inline fun cacheAndPerform(debtor: Debtor, perform: (Debtor) -> Unit) {
        val cachedDebtor = Debtor(debtor.billId, debtor.memberEmail)
        cachedDebtors.add(cachedDebtor)
        perform(cachedDebtor)
    }

    private inline fun cacheByBillIdAndPerform(debtors: List<Debtor>, perform: (List<Debtor>) -> Unit) {
        val cachedDebtors = LinkedList<Debtor>()
        debtors.forEach { debtor ->
            cachedDebtors.add(Debtor(debtor.billId, debtor.memberEmail))
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
}