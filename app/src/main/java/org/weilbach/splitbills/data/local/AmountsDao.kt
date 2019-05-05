package org.weilbach.splitbills.data.local

import androidx.room.*
import org.weilbach.splitbills.data.Amount

@Dao
interface AmountsDao {

    @Query("SELECT * FROM amounts")
    fun getAmounts(): List<Amount>

    @Query("SELECT * FROM amounts WHERE bill_id = :billId")
    fun getAmountsByBillId(billId: String): List<Amount>

    @Query("SELECT * FROM amounts WHERE bill_id = :billId AND valid = 1")
    fun getValidAmountByBillId(billId: String): Amount?

    @Update
    fun updateAmount(amount: Amount): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAmount(amount: Amount)

    @Query("DELETE FROM amounts")
    fun deleteAmounts()
}