package org.weilbach.splitbills.data.source

import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data.Bill
import org.weilbach.splitbills.data.BillDebtors

class BillRepository private constructor(
        private val billLocalDataSource: BillDataSource
) : BillDataSource {

    override fun getBillWithDebtorsById(billId: String): LiveData<BillDebtors> {
        return billLocalDataSource.getBillWithDebtorsById(billId)
    }

    override fun updateBillSync(bill: Bill) {
        billLocalDataSource.updateBillSync(bill)
    }

    override fun getBillByIdSync(billId: String): Bill? {
        return billLocalDataSource.getBillByIdSync(billId)
    }

    override fun saveBillSync(bill: Bill) {
        billLocalDataSource.saveBillSync(bill)
    }

    override fun updateBill(bill: Bill) {
        billLocalDataSource.updateBill(bill)
    }

    override fun getBillsWithDebtorsByGroupName(groupName: String): LiveData<List<BillDebtors>> {
        return billLocalDataSource.getBillsWithDebtorsByGroupName(groupName)
    }

    override fun getBillsByGroupNameOrdered(groupName: String): LiveData<List<Bill>> {
        return billLocalDataSource.getBillsByGroupNameOrdered(groupName)
    }

    override fun getBillById(billId: String): LiveData<Bill> {
        return billLocalDataSource.getBillById(billId)
    }

    override fun deleteBill(billId: String) {
        billLocalDataSource.deleteBill(billId)
    }

    override fun getAll(): LiveData<List<Bill>> {
        return billLocalDataSource.getAll()
    }

    override fun save(item: Bill) {
        billLocalDataSource.save(item)
    }

    override fun deleteAll() {
        billLocalDataSource.deleteAll()
    }

    override fun refresh() {}

    companion object {
        private var INSTANCE: BillRepository? = null

        @JvmStatic
        fun getInstance(billsLocalDataSource: BillDataSource) =
                INSTANCE ?: synchronized(BillRepository::class.java) {
                    INSTANCE ?: BillRepository(billsLocalDataSource)
                            .also { INSTANCE = it }
                }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}