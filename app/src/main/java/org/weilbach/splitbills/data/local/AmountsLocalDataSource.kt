// package org.weilbach.splitbills.data.local

/*import androidx.annotation.VisibleForTesting
import org.weilbach.splitbills.data.AmountData
import org.weilbach.splitbills.data.source.AmountsDataSource
import org.weilbach.splitbills.util.AppExecutors*/

/*
class AmountsLocalDataSource private constructor(
        private val appExecutors: AppExecutors,
        private val amountsDao: AmountsDao
) : AmountsDataSource {

    override fun getAmounts(callback: AmountsDataSource.GetAmountsCallback) {
        appExecutors.diskIO.execute {
            val amounts = amountsDao.getAmounts()
            appExecutors.mainThread.execute {
                if (amounts.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onAmountsLoaded(amounts)
                }
            }
        }
    }

    override fun getAmountsByBillId(billId: String, callback: AmountsDataSource.GetAmountsCallback) {
        appExecutors.diskIO.execute {
            val amounts = amountsDao.getAmountsByBillId(billId)
            appExecutors.mainThread.execute {
                if (amounts.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onAmountsLoaded(amounts)
                }
            }
        }
    }

    override fun getAmountsByBillIdSync(billId: String): List<AmountData> {
        return amountsDao.getAmountsByBillId(billId)
    }

    override fun getValidAmountByBillId(billId: String, callback: AmountsDataSource.GetAmountCallback) {
        appExecutors.diskIO.execute {
            val amount = amountsDao.getValidAmountByBillId(billId)
            appExecutors.mainThread.execute {
                if (amount == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onAmountLoaded(amount)
                }
            }
        }
    }

    override fun getValidAmountByBillIdSync(billId: String): AmountData? {
        return amountsDao.getValidAmountByBillId(billId)
    }

    override fun saveAmount(amountData: AmountData) {
        appExecutors.diskIO.execute { amountsDao.insertAmount(amountData) }
    }

    override fun saveAmountSync(amountData: AmountData) {
        amountsDao.insertAmount(amountData)
    }

    override fun deleteAllAmounts() {
        appExecutors.diskIO.execute { amountsDao.deleteAmounts() }
    }

    override fun deleteAmountsByBillId(billId: String) {
        appExecutors.diskIO.execute { amountsDao.deleteAmountsByBillId(billId) }
    }

    override fun deleteAmountsByBillIdSync(billId: String) {
        amountsDao.deleteAmountsByBillId(billId)
    }

    override fun refreshAmounts() {
        // Handled by {@link AmountsRepository}
    }

    companion object {
        private var INSTANCE: AmountsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, amountsDao: AmountsDao): AmountsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(AmountsLocalDataSource::javaClass) {
                    INSTANCE = AmountsLocalDataSource(appExecutors, amountsDao)
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
