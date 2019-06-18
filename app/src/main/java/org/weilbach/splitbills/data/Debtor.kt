package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.TypeConverters
import org.weilbach.splitbills.data.local.Converter
import java.math.BigDecimal

@Entity(tableName = "debtors",
        primaryKeys = ["bill_id", "member_email"],
        foreignKeys = [
            ForeignKey(
                    entity = Bill::class,
                    parentColumns = ["id"],
                    childColumns = ["bill_id"],
                    onUpdate = ForeignKey.CASCADE,
                    onDelete = ForeignKey.CASCADE
            ),
            ForeignKey(
                    entity = Member::class,
                    parentColumns = ["email"],
                    childColumns = ["member_email"],
                    onUpdate = ForeignKey.CASCADE,
                    onDelete = ForeignKey.CASCADE
            )
        ])
@TypeConverters(Converter::class)
data class Debtor constructor(
        @ColumnInfo(name = "bill_id") val billId: String,
        @ColumnInfo(name = "member_email", index = true) val memberEmail: String,
        @ColumnInfo(name = "amount") val amount: BigDecimal
) {
}