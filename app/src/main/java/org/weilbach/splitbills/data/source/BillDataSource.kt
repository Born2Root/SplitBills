package org.weilbach.splitbills.data.source

import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data.Bill
import org.weilbach.splitbills.data.BillDebtors

interface BillDataSource : BaseDataSource<Bill> {

    fun getBillById(billId: String): LiveData<Bill>

    fun getBillByIdSync(billId: String): Bill?

    fun saveBillSync(bill: Bill)

    fun updateBillSync(bill: Bill)

    fun getBillsByGroupNameOrdered(groupName: String): LiveData<List<Bill>>

    fun deleteBill(billId: String)

    fun updateBill(bill: Bill)

    fun getBillsWithDebtorsByGroupName(groupName: String): LiveData<List<BillDebtors>>

    fun getBillWithDebtorsById(billId: String): LiveData<BillDebtors>
}