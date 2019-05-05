package org.weilbach.splitbills.data.local

import androidx.room.*
import org.weilbach.splitbills.data.Bill

@Dao
interface BillDao {

    @Query("SELECT * FROM bills")
    fun getBills(): List<Bill>

    @Query("SELECT * FROM bills WHERE id = :billId")
    fun getBillById(billId: String): Bill?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBill(bill: Bill)

    @Update
    fun updateBill(bill: Bill): Int

    @Query("DELETE FROM bills WHERE id = :billId")
    fun deleteBillById(billId: String): Int

    @Query("DELETE FROM bills")
    fun deleteBills()
}