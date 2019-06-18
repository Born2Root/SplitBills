package org.weilbach.splitbills.data2.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import org.weilbach.splitbills.data2.Bill
import org.weilbach.splitbills.data2.BillDebtors

@Dao
interface BillDao : BaseDao<Bill> {

    @Query("DELETE FROM bills WHERE id = :billId")
    fun deleteBillById(billId: String): Int

    @Query("SELECT * FROM bills")
    fun getBills(): LiveData<List<Bill>>

    @Query("SELECT * FROM bills WHERE id = :billId")
    fun getBillById(billId: String): LiveData<Bill>

    @Query("SELECT * FROM bills WHERE id = :billId")
    fun getBillByIdSync(billId: String): Bill?

    @Query("SELECT * FROM bills WHERE group_name = :groupName")
    fun getBillsByGroupName(groupName: String): LiveData<List<Bill>>

    @Query("SELECT * FROM bills WHERE group_name = :groupName ORDER BY date_time DESC")
    fun getBillsByGroupNameOrdered(groupName: String): LiveData<List<Bill>>

    @Transaction
    @Query("SELECT * FROM bills WHERE group_name = :groupName")
    fun getBillsWithDebtorsByGroupName(groupName: String): LiveData<List<BillDebtors>>

    @Query("DELETE FROM bills")
    fun deleteBills()

}