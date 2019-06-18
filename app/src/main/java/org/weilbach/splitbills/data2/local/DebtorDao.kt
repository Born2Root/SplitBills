package org.weilbach.splitbills.data2.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.weilbach.splitbills.data2.Bill
import org.weilbach.splitbills.data2.BillDebtors
import org.weilbach.splitbills.data2.Debtor
import org.weilbach.splitbills.data2.Member

@Dao
abstract class DebtorDao : BaseDao<Debtor> {

    @Query("SELECT * FROM debtors")
    abstract fun getDebtors(): LiveData<List<Debtor>>

    @Query("SELECT * FROM debtors WHERE bill_id = :billId")
    abstract fun getDebtorsByBillId(billId: String): LiveData<List<Debtor>>

    @Query("SELECT members.name, members.email FROM debtors INNER JOIN members ON members.email = debtors.member_email WHERE bill_id = :billId")
    abstract fun getDebtorsMembersByBillId(billId: String): LiveData<List<Member>>

    @Query("DELETE FROM debtors")
    abstract fun deleteDebtors()

    @Query("DELETE FROM debtors WHERE bill_id = :billId")
    abstract fun deleteDebtorsByBillId(billId: String)

    @Insert
    abstract fun insertBill(bill: Bill)

    fun createNewBill(billDebtors: BillDebtors) {
        insertBill(billDebtors.bill)
        billDebtors.debtors.forEach { debtor ->
            insert(debtor)
        }
    }
}