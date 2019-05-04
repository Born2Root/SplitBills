package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "groups_members",
        primaryKeys = ["group_name", "member_email"],
        foreignKeys = [
            ForeignKey(
                    entity = Group::class,
                    parentColumns = ["name"],
                    childColumns = ["group_name"]
            ),
            ForeignKey(
                    entity = Member::class,
                    parentColumns = ["email"],
                    childColumns = ["member_email"]
            )
        ]
)
data class GroupMember constructor(
        @ColumnInfo(name = "group_name") val groupName: String,
        @ColumnInfo(name = "member_email") val memberEmail: String
) {
}