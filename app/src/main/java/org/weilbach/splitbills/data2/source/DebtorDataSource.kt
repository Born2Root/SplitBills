package org.weilbach.splitbills.data2.source

import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data2.BillDebtors
import org.weilbach.splitbills.data2.Debtor
import org.weilbach.splitbills.data2.Member

interface DebtorDataSource : BaseDataSource<Debtor> {

    fun getDebtorsByBillId(billId: String): LiveData<List<Debtor>>

    fun getDebtorsMembersByBillId(billId: String): LiveData<List<Member>>

    fun deleteDebtorsByBillId(billId: String)

    fun saveDebtorSync(debtor: Debtor)

    fun createNewBill(billDebtors: BillDebtors)
}