package org.weilbach.splitbills.data.local

import androidx.annotation.VisibleForTesting
import org.weilbach.splitbills.data.Bill
import org.weilbach.splitbills.data.source.BillsDataSource
import org.weilbach.splitbills.util.AppExecutors

class BillsLocalDataSource private constructor(
        private val appExecutors: AppExecutors,
        private val billsDao: BillDao
) : BillsDataSource {

    override fun getBills(callback: BillsDataSource.GetBillsCallback) {
        appExecutors.diskIO.execute {
            val bills = billsDao.getBills()
            appExecutors.mainThread.execute {
                if (bills.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onBillsLoaded(bills)
                }
            }
        }
    }

    override fun getBill(billId: String, callback: BillsDataSource.GetBillCallback) {
        val bill = billsDao.getBillById(billId)
        appExecutors.mainThread.execute {
            if (bill != null) {
                callback.onBillLoaded(bill)
            } else {
                callback.onDataNotAvailable()
            }
        }
    }

    override fun saveBill(bill: Bill) {
        appExecutors.diskIO.execute { billsDao.insertBill(bill) }
    }

    override fun deleteBill(billId: String) {
        appExecutors.diskIO.execute { billsDao.deleteBillById(billId) }
    }

    override fun deleteAllBills() {
        appExecutors.diskIO.execute { billsDao.deleteBills() }
    }

    override fun refreshBills() {
        // Handled by {@link BillsRepository}
    }

    companion object {
        private var INSTANCE: BillsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, billsDao: BillDao): BillsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(BillsLocalDataSource::javaClass) {
                    INSTANCE = BillsLocalDataSource(appExecutors, billsDao)
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