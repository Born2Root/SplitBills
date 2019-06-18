/*
package org.weilbach.splitbills.data.local

import androidx.room.*
import org.weilbach.splitbills.data.BillData

@Dao
interface BillDao {

    @Query("SELECT * FROM bills")
    fun getBills(): List<BillData>

    @Query("SELECT * FROM bills WHERE id = :billId")
    fun getBillById(billId: String): BillData?

    @Query("SELECT * FROM bills WHERE group_name = :groupName")
    fun getBillsByGroupName(groupName: String): List<BillData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBill(bill: BillData)

    @Update
    fun updateBill(bill: BillData): Int

    @Query("DELETE FROM bills WHERE id = :billId")
    fun deleteBillById(billId: String): Int

    @Query("DELETE FROM bills")
    fun deleteBills()
}*/
