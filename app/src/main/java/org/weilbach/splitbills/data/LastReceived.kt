package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "last_received",
        primaryKeys = ["member_id", "group_name"],
        foreignKeys = [
            ForeignKey(
                    entity = Member::class,
                    parentColumns = ["id"],
                    childColumns = ["member_id"]
            ),
            ForeignKey(
                    entity = Group::class,
                    parentColumns = ["name"],
                    childColumns = ["group_name"]
            )
        ]
)
class LastReceived constructor(
        @ColumnInfo(name = "member_id") val memberId: String,
        @ColumnInfo(name = "group_name", index = true) val groupName: String,
        @ColumnInfo(name = "date_time") val dateTime: String
) {
}