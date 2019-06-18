/*
package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "last_received",
        primaryKeys = ["member_email", "group_name"],
        foreignKeys = [
            ForeignKey(
                    entity = MemberData::class,
                    parentColumns = ["email"],
                    childColumns = ["member_email"]
            ),
            ForeignKey(
                    entity = GroupData::class,
                    parentColumns = ["name"],
                    childColumns = ["group_name"]
            )
        ]
)
class LastReceivedData constructor(
        @ColumnInfo(name = "member_email") val memberEmail: String,
        @ColumnInfo(name = "group_name", index = true) val groupName: String,
        @ColumnInfo(name = "date_time") val dateTime: String
) {
}*/
