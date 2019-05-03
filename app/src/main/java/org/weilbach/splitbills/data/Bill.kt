package org.weilbach.splitbills.data

import androidx.room.*

@Entity(tableName = "bills",
        foreignKeys = [
            ForeignKey(
                    entity = Group::class,
                    parentColumns = ["name"],
                    childColumns = ["group_name"]
            )
        ]
)
data class Bill constructor(
        @ColumnInfo(name = "date_time") val dateTime: String,
        @ColumnInfo(name = "description") val description: String,
        @ColumnInfo(name = "creditor_email") val creditorEmail: String,
        @ColumnInfo(name = "group_name", index = true) val groupName: String,
        @ColumnInfo(name = "valid") var valid: Boolean
) {
    @PrimaryKey @ColumnInfo(name = "id") var id = dateTime + description
}