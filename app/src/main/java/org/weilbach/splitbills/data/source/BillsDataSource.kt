/*
package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.BillData

interface BillsDataSource {
    interface GetBillsCallback {
        fun onBillsLoaded(bill: List<BillData>)
        fun onDataNotAvailable()
    }

    interface GetBillCallback {
        fun onBillLoaded(bill: BillData)
        fun onDataNotAvailable()
    }

    fun getBills(callback: GetBillsCallback)

    fun getBillsByGroupName(groupName: String, callback: GetBillsCallback)

    fun getBillsByGroupNameSync(groupName: String): List<BillData>

    fun getBill(billId: String, callback: GetBillCallback)

    fun getBillSync(billId: String): BillData?

    fun saveBill(bill: BillData)

    fun saveBillSync(bill: BillData)

    fun deleteBill(billId: String)

    fun deleteBillSync(billId: String)

    fun deleteAllBills()

    fun refreshBills()
}*/
