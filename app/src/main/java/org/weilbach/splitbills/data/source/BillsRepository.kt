/*
package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.BillData
import org.weilbach.splitbills.util.EspressoIdlingResource
import java.util.*

class BillsRepository private constructor(
        private val billsLocalDataSource: BillsDataSource
) : BillsDataSource {

    var cachedBills: LinkedHashMap<String, BillData> = LinkedHashMap()
    var cachedBillsByGroupName: LinkedHashMap<String, List<BillData>> = LinkedHashMap()

    var cacheIsDirty = false


    override fun getBills(callback: BillsDataSource.GetBillsCallback) {
        if (cachedBills.isNotEmpty() && !cacheIsDirty) {
            callback.onBillsLoaded(ArrayList(cachedBills.values))
            return
        }

        EspressoIdlingResource.increment()

        billsLocalDataSource.getBills(object : BillsDataSource.GetBillsCallback {
            override fun onBillsLoaded(bill: List<BillData>) {
                refreshCache(bill)
                EspressoIdlingResource.decrement()
                callback.onBillsLoaded(ArrayList(cachedBills.values))
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getBillsByGroupName(groupName: String, callback: BillsDataSource.GetBillsCallback) {
        val cachedBills = getBillsWithGroupName(groupName)
        if (cachedBills != null
                && cachedBills.isNotEmpty()
                && !cacheIsDirty) {
            callback.onBillsLoaded(cachedBills)
            return
        }

        EspressoIdlingResource.increment()

        billsLocalDataSource.getBillsByGroupName(groupName, object : BillsDataSource.GetBillsCallback {
            override fun onBillsLoaded(bill: List<BillData>) {
                cacheByGroupNameAndPerform(bill) {
                    EspressoIdlingResource.decrement()
                    callback.onBillsLoaded(bill)
                }
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getBillsByGroupNameSync(groupName: String): List<BillData> {
        val cachedBills = getBillsWithGroupName(groupName)
        if (cachedBills != null
                && cachedBills.isNotEmpty()
                && !cacheIsDirty) {
            return cachedBills
        }

        val bills = billsLocalDataSource.getBillsByGroupNameSync(groupName)

        if (bills.isNotEmpty()) {
            cacheByGroupNameAndPerform(bills) { }
        }

        return bills
    }

    override fun getBill(billId: String, callback: BillsDataSource.GetBillCallback) {
        val billInCache = getBillWithId(billId)

        if (billInCache != null) {
            callback.onBillLoaded(billInCache)
            return
        }

        EspressoIdlingResource.increment()

        billsLocalDataSource.getBill(billId, object : BillsDataSource.GetBillCallback {
            override fun onBillLoaded(bill: BillData) {
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

    override fun getBillSync(billId: String): BillData? {
        val billInCache = getBillWithId(billId)

        if (billInCache != null) {
            return billInCache
        }

        val bill = billsLocalDataSource.getBillSync(billId)
        bill?.let {
            cacheAndPerform(it) {}
        }

        return bill
    }

    override fun saveBill(bill: BillData) {
        cacheAndPerform(bill) {
            billsLocalDataSource.saveBill(it)
        }
    }

    override fun saveBillSync(bill: BillData) {
        cacheAndPerform(bill) {
            billsLocalDataSource.saveBillSync(it)
        }
    }

    override fun deleteBill(billId: String) {
        billsLocalDataSource.deleteBill(billId)
        cachedBills.remove(billId)
    }

    override fun deleteBillSync(billId: String) {
        billsLocalDataSource.deleteBillSync(billId)
        cachedBills.remove(billId)
    }

    override fun deleteAllBills() {
        billsLocalDataSource.deleteAllBills()
        cachedBills.clear()
    }

    override fun refreshBills() {
        cacheIsDirty = true
    }

    private fun refreshCache(bill: List<BillData>) {
        cachedBillsByGroupName.clear()
        cachedBills.clear()
        bill.forEach {
            cacheAndPerform(it) { }
        }
        cacheIsDirty = false
    }

    private inline fun cacheAndPerform(bill: BillData, perform: (BillData) -> Unit) {
        val cachedBill = BillData(
                bill.dateTime,
                bill.description,
                bill.amount,
                bill.currency,
                bill.creditorEmail,
                bill.groupName,
                bill.valid)
        cachedBills[cachedBill.id] = cachedBill
        perform(cachedBill)
    }

    private inline fun cacheByGroupNameAndPerform(bill: List<BillData>, perform: (List<BillData>) -> Unit) {
        val cachedBills = LinkedList<BillData>()
        bill.forEach { bill ->
            cachedBills.add(BillData(
                    bill.dateTime,
                    bill.description,
                    bill.amount,
                    bill.currency,
                    bill.creditorEmail,
                    bill.groupName,
                    bill.valid))
        }
        cachedBillsByGroupName[cachedBills[0].groupName] = cachedBills
        perform(cachedBills)
    }

    private fun getBillWithId(id: String) = cachedBills[id]

    private fun getBillsWithGroupName(groupName: String) = cachedBillsByGroupName[groupName]

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
}*/
