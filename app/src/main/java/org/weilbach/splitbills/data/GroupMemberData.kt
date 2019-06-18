/*
package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "groups_members",
        primaryKeys = ["group_name", "member_email"],
        foreignKeys = [
            ForeignKey(
                    entity = GroupData::class,
                    parentColumns = ["name"],
                    childColumns = ["group_name"]
            ),
            ForeignKey(
                    entity = MemberData::class,
                    parentColumns = ["email"],
                    childColumns = ["member_email"]
            )
        ]
)
data class GroupMemberData constructor(
        @ColumnInfo(name = "group_name") val groupName: String,
        @ColumnInfo(name = "member_email", index = true) val memberEmail: String
) {
}*/
