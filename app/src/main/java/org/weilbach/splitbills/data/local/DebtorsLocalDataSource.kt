/*
package org.weilbach.splitbills.data.local

import androidx.annotation.VisibleForTesting
import org.weilbach.splitbills.data.DebtorData
import org.weilbach.splitbills.data.source.DebtorsDataSource
import org.weilbach.splitbills.util.AppExecutors

class DebtorsLocalDataSource private constructor(
        private val appExecutors: AppExecutors,
        private val debtorsDao: DebtorsDao
) : DebtorsDataSource {

    override fun getDebtors(callback: DebtorsDataSource.GetDebtorsCallback) {
        appExecutors.diskIO.execute {
            val debtors = debtorsDao.getDebtors()
            appExecutors.mainThread.execute {
                if (debtors.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onDebtorsLoaded(debtors)
                }
            }
        }
    }

    override fun getDebtorsByBillId(billId: String, callback: DebtorsDataSource.GetDebtorsCallback) {
        appExecutors.diskIO.execute {
            val debtors = debtorsDao.getDebtorsByBillId(billId)
            appExecutors.mainThread.execute {
                if (debtors.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onDebtorsLoaded(debtors)
                }
            }
        }
    }

    override fun getDebtorsByBillIdSync(billId: String): List<DebtorData> {
        return debtorsDao.getDebtorsByBillId(billId)
    }

    override fun saveDebtor(debtorData: DebtorData) {
        appExecutors.diskIO.execute { debtorsDao.insertDebtor(debtorData) }
    }

    override fun saveDebtorSync(debtorData: DebtorData) {
        debtorsDao.insertDebtor(debtorData)
    }

    override fun deleteAllDebtors() {
        appExecutors.diskIO.execute { debtorsDao.deleteDebtors() }
    }

    override fun deleteDebtorsByBillId(billId: String) {
        appExecutors.diskIO.execute { debtorsDao.deleteDebtorsByBillId(billId) }
    }

    override fun deleteDebtorsByBillIdSync(billId: String) {
        debtorsDao.deleteDebtorsByBillId(billId)
    }

    override fun refreshDebtors() {
        // Handled from {@link DebtorsRepository}
    }

    companion object {
        private var INSTANCE: DebtorsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, debtorsDao: DebtorsDao): DebtorsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(DebtorsLocalDataSource::javaClass) {
                    INSTANCE = DebtorsLocalDataSource(appExecutors, debtorsDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}*/
