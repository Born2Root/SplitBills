/*
package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "bills",
        foreignKeys = [
            ForeignKey(
                    entity = GroupData::class,
                    parentColumns = ["name"],
                    childColumns = ["group_name"]
            ),
            ForeignKey(
                    entity = MemberData::class,
                    parentColumns = ["email"],
                    childColumns = ["creditor_email"]
            )
        ]
)
data class BillData constructor(
        @ColumnInfo(name = "date_time") val dateTime: String,
        @ColumnInfo(name = "description") val description: String,
        @ColumnInfo(name = "amount") val amount: String,
        @ColumnInfo(name = "currency") val currency: String,
        @ColumnInfo(name = "creditor_email", index = true) val creditorEmail: String,
        @ColumnInfo(name = "group_name", index = true) val groupName: String,
        @ColumnInfo(name = "valid") var valid: Boolean
) {
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id = dateTime + description + groupName

    companion object {
        const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz"
        val formatter = SimpleDateFormat(DATE_TIME_FORMAT).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
}*/
