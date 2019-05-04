package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "bills",
        foreignKeys = [
            ForeignKey(
                    entity = Group::class,
                    parentColumns = ["name"],
                    childColumns = ["group_name"]
            ),
            ForeignKey(
                    entity = Member::class,
                    parentColumns = ["email"],
                    childColumns = ["creditor_email"]
            )
        ]
)
data class Bill constructor(
        @ColumnInfo(name = "date_time") val dateTime: String,
        @ColumnInfo(name = "description") val description: String,
        @ColumnInfo(name = "creditor_email", index = true) val creditorEmail: String,
        @ColumnInfo(name = "group_name", index = true) val groupName: String,
        @ColumnInfo(name = "valid") var valid: Boolean
) {
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id = dateTime + description
}