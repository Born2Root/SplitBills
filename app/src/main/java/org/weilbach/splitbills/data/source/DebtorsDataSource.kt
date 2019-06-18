/*
package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.DebtorData

interface DebtorsDataSource {

    interface GetDebtorsCallback {
        fun onDebtorsLoaded(debtorData: List<DebtorData>)
        fun onDataNotAvailable()
    }

    fun getDebtors(callback: GetDebtorsCallback)

    fun getDebtorsByBillId(billId: String, callback: GetDebtorsCallback)

    fun getDebtorsByBillIdSync(billId: String): List<DebtorData>

    fun saveDebtor(debtorData: DebtorData)

    fun saveDebtorSync(debtorData: DebtorData)

    fun deleteAllDebtors()

    fun deleteDebtorsByBillId(billId: String)

    fun deleteDebtorsByBillIdSync(billId: String)

    fun refreshDebtors()
}*/
