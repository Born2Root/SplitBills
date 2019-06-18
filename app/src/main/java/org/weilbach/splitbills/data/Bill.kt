package org.weilbach.splitbills.data

import androidx.room.*
import org.weilbach.splitbills.data.local.Converter
import java.math.BigDecimal
import java.util.*

@Entity(tableName = "bills",
        foreignKeys = [
            ForeignKey(
                    entity = Group::class,
                    parentColumns = ["name"],
                    childColumns = ["group_name"],
                    onUpdate = ForeignKey.CASCADE,
                    onDelete = ForeignKey.CASCADE
            ),
            ForeignKey(
                    entity = Member::class,
                    parentColumns = ["email"],
                    childColumns = ["creditor_email"],
                    onUpdate = ForeignKey.CASCADE,
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
@TypeConverters(Converter::class)
data class Bill constructor(
        @ColumnInfo(name = "date_time")
        val dateTime: Date,

        @ColumnInfo(name = "description")
        val description: String,
        @ColumnInfo(name = "amount")
        val amount: BigDecimal,

        @ColumnInfo(name = "currency")
        val currency: String,

        @ColumnInfo(name = "creditor_email", index = true)
        val creditorEmail: String,

        @ColumnInfo(name = "group_name", index = true)
        val groupName: String,

        @ColumnInfo(name = "valid")
        val valid: Boolean
) {
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id = dateTime.time.toString() + description + groupName

    companion object {
        /*const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz"
        val formatter = SimpleDateFormat(DATE_TIME_FORMAT).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }*/
    }
}