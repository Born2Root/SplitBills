/*
package org.weilbach.splitbills.data.local

import androidx.room.*
import org.weilbach.splitbills.data.DebtorData
import org.weilbach.splitbills.data.MemberData

@Dao
interface DebtorsDao {

    @Query("SELECT * FROM debtors")
    fun getDebtors(): List<DebtorData>

    @Query("SELECT * FROM debtors WHERE bill_id = :billId")
    fun getDebtorsByBillId(billId: String): List<DebtorData>

    @Query("SELECT members.name, members.email FROM debtors INNER JOIN members ON members.email = debtors.member_email WHERE bill_id = :billId")
    fun getDebtorsMembersByBillId(billId: String): List<MemberData>

    @Update
    fun updateDebtor(debtorData: DebtorData): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDebtor(debtorData: DebtorData)

    @Query("DELETE FROM debtors")
    fun deleteDebtors()

    @Query("DELETE FROM debtors WHERE bill_id = :billId")
    fun deleteDebtorsByBillId(billId: String)
}*/
