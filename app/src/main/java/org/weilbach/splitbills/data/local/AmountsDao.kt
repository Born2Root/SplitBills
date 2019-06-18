// package org.weilbach.splitbills.data.local

/*import androidx.room.*
import org.weilbach.splitbills.data.AmountData*/

/*
@Dao
interface AmountsDao {

    @Query("SELECT * FROM amounts")
    fun getAmounts(): List<AmountData>

    @Query("SELECT * FROM amounts WHERE bill_id = :billId")
    fun getAmountsByBillId(billId: String): List<AmountData>

    @Query("SELECT * FROM amounts WHERE bill_id = :billId AND valid = 1")
    fun getValidAmountByBillId(billId: String): AmountData?

    @Update
    fun updateAmount(amountData: AmountData): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAmount(amountData: AmountData)

    @Query("DELETE FROM amounts")
    fun deleteAmounts()

    @Query("DELETE FROM amounts WHERE bill_id = :billId")
    fun deleteAmountsByBillId(billId: String)
}*/
