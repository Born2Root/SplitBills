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

    interface SaveAmountCallback {
        fun onAmountSaved()
        fun onDataNotAvailable()
    }

    interface SetAmountValidCallback {
        fun onAmountUpdated()
        fun onDataNotAvailable()
    }

    interface DeleteAmountCallback {
        fun onAmountDeleted()
        fun onDataNotAvailable()
    }

    interface DeleteAllAmountsCallback {
        fun onAmountsDeleted()
        fun onDataNotAvailable()
    }

    fun getAmounts(callback: GetAmountsCallback)

    fun getAmount(amountId: String, callback: GetAmountCallback)

    fun saveAmount(amount: Amount, callback: SaveAmountCallback)

    fun setAmountValid(amountId: String, value: Boolean, callback: SetAmountValidCallback)

    fun deleteAmount(amountId: String, callback: DeleteAmountCallback)

    fun deleteAllAmounts(callback: DeleteAllAmountsCallback)
}