package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "debtors",
        primaryKeys = ["bill_id", "member_id"],
        foreignKeys = [
            ForeignKey(
                    entity = Bill::class,
                    parentColumns = ["id"],
                    childColumns = ["bill_id"]
            ),
            ForeignKey(
                    entity = Member::class,
                    parentColumns = ["id"],
                    childColumns = ["member_id"]
            )
        ])
data class Debtor constructor(
        @ColumnInfo(name = "bill_id") val billId : String,
        @ColumnInfo(name = "member_id", index = true) val memberId : String
) {
}