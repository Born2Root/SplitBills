package org.weilbach.splitbills.data2.source

import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data2.Bill
import org.weilbach.splitbills.data2.BillDebtors

interface BillDataSource : BaseDataSource<Bill> {

    fun getBillById(billId: String): LiveData<Bill>

    fun getBillByIdSync(billId: String): Bill?

    fun saveBillSync(bill: Bill)

    fun updateBillSync(bill: Bill)

    fun getBillsByGroupNameOrdered(groupName: String): LiveData<List<Bill>>

    fun deleteBill(billId: String)

    fun updateBill(bill: Bill)

    fun getBillsWithDebtorsByGroupName(groupName: String): LiveData<List<BillDebtors>>
}