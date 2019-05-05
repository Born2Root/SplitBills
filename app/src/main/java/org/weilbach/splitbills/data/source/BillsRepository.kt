package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.Bill
import org.weilbach.splitbills.util.EspressoIdlingResource

class BillsRepository private constructor(
        private val billsLocalDataSource: BillsDataSource
) : BillsDataSource {

    var cachedBills: LinkedHashMap<String, Bill> = LinkedHashMap()

    var cacheIsDirty = false


    override fun getBills(callback: BillsDataSource.GetBillsCallback) {
        if (cachedBills.isNotEmpty() && !cacheIsDirty) {
            callback.onBillsLoaded(ArrayList(cachedBills.values))
            return
        }

        EspressoIdlingResource.increment()

        billsLocalDataSource.getBills(object : BillsDataSource.GetBillsCallback {
            override fun onBillsLoaded(members: List<Bill>) {
                refreshCache(members)
                EspressoIdlingResource.decrement()
                callback.onBillsLoaded(ArrayList(cachedBills.values))
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getBill(billId: String, callback: BillsDataSource.GetBillCallback) {
        val billInCache = getBillWithId(billId)

        if (billInCache != null) {
            callback.onBillLoaded(billInCache)
        }

        EspressoIdlingResource.increment()

        billsLocalDataSource.getBill(billId, object : BillsDataSource.GetBillCallback {
            override fun onBillLoaded(bill: Bill) {
                cacheAndPerform(bill) {
                    EspressoIdlingResource.decrement()
                    callback.onBillLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun saveBill(bill: Bill) {
        cacheAndPerform(bill) {
            billsLocalDataSource.saveBill(it)
        }
    }

    override fun deleteBill(billId: String) {
        billsLocalDataSource.deleteBill(billId)
        cachedBills.remove(billId)
    }

    override fun deleteAllBills() {
        billsLocalDataSource.deleteAllBills()
        cachedBills.clear()
    }

    override fun refreshBills() {
        cacheIsDirty = true
    }

    private fun refreshCache(bills: List<Bill>) {
        cachedBills.clear()
        bills.forEach {
            cacheAndPerform(it) { }
        }
        cacheIsDirty = false
    }

    private inline fun cacheAndPerform(bill: Bill, perform: (Bill) -> Unit) {
        val cachedBill = Bill(
                bill.dateTime,
                bill.description,
                bill.creditorEmail,
                bill.groupName,
                bill.valid)
        cachedBills[cachedBill.id] = cachedBill
        perform(cachedBill)
    }

    private fun getBillWithId(id: String) = cachedBills[id]

    companion object {
        private var INSTANCE: BillsRepository? = null

        @JvmStatic
        fun getInstance(billsLocalDataSource: BillsDataSource) =
                INSTANCE ?: synchronized(BillsRepository::class.java) {
                    INSTANCE ?: BillsRepository(billsLocalDataSource)
                            .also { INSTANCE = it }
                }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}