package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "amounts",
        foreignKeys = [
            ForeignKey(
                    entity = Bill::class,
                    parentColumns = ["id"],
                    childColumns = ["bill_id"]
            )
        ])
data class Amount constructor(
        @ColumnInfo(name = "bill_id", index = true) val billId: String,
        @ColumnInfo(name = "amount") val amount: String,
        @ColumnInfo(name = "valid") val valid: Boolean
) {
    @PrimaryKey @ColumnInfo(name = "id") var id = billId + amount
}