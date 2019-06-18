package org.weilbach.splitbills.data.local

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data.BillDebtors
import org.weilbach.splitbills.data.Debtor
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.data.source.DebtorDataSource
import org.weilbach.splitbills.util.AppExecutors

class DebtorLocalDataSource private constructor(
        private val appExecutors: AppExecutors,
        private val debtorDao: DebtorDao
) : DebtorDataSource {

    override fun saveDebtorSync(debtor: Debtor) {
        debtorDao.insert(debtor)
    }

    override fun createNewBill(billDebtors: BillDebtors) {
        appExecutors.diskIO.execute { debtorDao.createNewBill(billDebtors) }
    }

    override fun getDebtorsByBillId(billId: String): LiveData<List<Debtor>> {
        return debtorDao.getDebtorsByBillId(billId)
    }

    override fun getDebtorsMembersByBillId(billId: String): LiveData<List<Member>> {
        return debtorDao.getDebtorsMembersByBillId(billId)
    }

    override fun deleteDebtorsByBillId(billId: String) {
        appExecutors.diskIO.execute { debtorDao.deleteDebtorsByBillId(billId) }
    }

    override fun getAll(): LiveData<List<Debtor>> {
        return debtorDao.getDebtors()
    }

    override fun save(item: Debtor) {
        appExecutors.diskIO.execute { debtorDao.insert(item) }
    }

    override fun deleteAll() {
        appExecutors.diskIO.execute { debtorDao.deleteDebtors() }
    }

    override fun refresh() { }

    companion object {
        private var INSTANCE: DebtorLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, debtorsDao: DebtorDao): DebtorLocalDataSource {
            if (INSTANCE == null) {
                synchronized(DebtorLocalDataSource::javaClass) {
                    INSTANCE = DebtorLocalDataSource(appExecutors, debtorsDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}