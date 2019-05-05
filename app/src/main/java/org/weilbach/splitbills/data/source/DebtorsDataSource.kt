package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.Debtor

interface DebtorsDataSource {

    interface GetDebtorsCallback {
        fun onDebtorsLoaded(debtors: List<Debtor>)
        fun onDataNotAvailable()
    }

    fun getDebtors(callback: GetDebtorsCallback)

    fun getDebtorsByBillId(billId: String, callback: GetDebtorsCallback)

    fun saveDebtor(debtor: Debtor)

    fun deleteAllDebtors()

    fun refreshDebtors()
}