package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.Bill

interface BillsDataSource {
    interface GetBillsCallback {
        fun onBillsLoaded(bills: List<Bill>)
        fun onDataNotAvailable()
    }

    interface GetBillCallback {
        fun onBillLoaded(bill: Bill)
        fun onDataNotAvailable()
    }

    fun getBills(callback: GetBillsCallback)

    fun getBill(billId: String, callback: GetBillCallback)

    fun saveBill(bill: Bill)

    fun deleteBill(billId: String)

    fun deleteAllBills()

    fun refreshBills()
}