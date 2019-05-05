package org.weilbach.splitbills.data.local

import androidx.room.*
import org.weilbach.splitbills.data.Debtor

@Dao
interface DebtorsDao {

    @Query("SELECT * FROM debtors")
    fun getDebtors(): List<Debtor>

    @Query("SELECT * FROM debtors WHERE bill_id = :billId")
    fun getDebtorsByBillId(billId: String): List<Debtor>

    @Update
    fun updateDebtor(debtor: Debtor): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDebtor(debtor: Debtor)

    @Query("DELETE FROM debtors")
    fun deleteDebtors()
}