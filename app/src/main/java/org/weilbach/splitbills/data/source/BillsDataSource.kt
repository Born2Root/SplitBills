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

    interface SaveBillCallback {
        fun onBillSaved()
        fun onDataNotAvailable()
    }

    interface SetBillValidCallback {
        fun onBillUpdated()
        fun onDataNotAvailable()
    }

    interface DeleteBillCallback {
        fun onBillDeleted()
        fun onDataNotAvailable()
    }

    interface DeleteAllBillsCallback {
        fun onBillsDeleted()
        fun onDataNotAvailable()
    }

    fun getBills(callback: GetBillsCallback)

    fun getBill(billId: String, callback: GetBillCallback)

    fun saveBill(bill: Bill, callback: SaveBillCallback)

    fun setBillValid(billId: String, value: Boolean, callback: SetBillValidCallback)

    fun deleteBill(billId: String, callback: DeleteBillCallback)

    fun deleteAllBills(callback: DeleteAllBillsCallback)
}