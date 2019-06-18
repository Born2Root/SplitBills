/*
package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "debtors",
        primaryKeys = ["bill_id", "member_email"],
        foreignKeys = [
            ForeignKey(
                    entity = BillData::class,
                    parentColumns = ["id"],
                    childColumns = ["bill_id"]
            ),
            ForeignKey(
                    entity = MemberData::class,
                    parentColumns = ["email"],
                    childColumns = ["member_email"]
            )
        ])
data class DebtorData constructor(
        @ColumnInfo(name = "bill_id") val billId: String,
        @ColumnInfo(name = "member_email", index = true) val memberEmail: String
) {
}*/
