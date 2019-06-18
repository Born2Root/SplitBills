package org.weilbach.splitbills.data.source

import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data.BillDebtors
import org.weilbach.splitbills.data.Debtor
import org.weilbach.splitbills.data.Member

class DebtorRepository private constructor(
        private val debtorLocalDataSource: DebtorDataSource
) : DebtorDataSource {

    override fun saveDebtorSync(debtor: Debtor) {
        debtorLocalDataSource.saveDebtorSync(debtor)
    }

    override fun createNewBill(billDebtors: BillDebtors) {
        debtorLocalDataSource.createNewBill(billDebtors)
    }

    override fun getDebtorsByBillId(billId: String): LiveData<List<Debtor>> {
        return debtorLocalDataSource.getDebtorsByBillId(billId)
    }

    override fun getDebtorsMembersByBillId(billId: String): LiveData<List<Member>> {
        return debtorLocalDataSource.getDebtorsMembersByBillId(billId)
    }

    override fun deleteDebtorsByBillId(billId: String) {
        debtorLocalDataSource.deleteDebtorsByBillId(billId)
    }

    override fun getAll(): LiveData<List<Debtor>> {
        return debtorLocalDataSource.getAll()
    }

    override fun save(item: Debtor) {
        debtorLocalDataSource.save(item)
    }

    override fun deleteAll() {
        debtorLocalDataSource.deleteAll()
    }

    override fun refresh() { }

    companion object {
        private var INSTANCE: DebtorRepository? = null

        @JvmStatic
        fun getInstance(debtorsLocalDataSource: DebtorDataSource) =
                INSTANCE ?: synchronized(DebtorRepository::class.java) {
                    INSTANCE ?: DebtorRepository(debtorsLocalDataSource)
                            .also { INSTANCE = it }
                }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}