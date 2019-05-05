package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.Amount

interface AmountsDataSource {
    interface GetAmountsCallback {
        fun onAmountsLoaded(amounts: List<Amount>)
        fun onDataNotAvailable()
    }

    interface GetAmountCallback {
        fun onAmountLoaded(amount: Amount)
        fun onDataNotAvailable()
    }
    fun getAmounts(callback: GetAmountsCallback)

    fun getAmountByBillId(billId: String, callback: GetAmountsCallback)

    fun getValidAmountByBillId(billId: String, callback: GetAmountCallback)

    fun saveAmount(amount: Amount)

    fun deleteAllAmounts()

    fun refreshAmounts()
}