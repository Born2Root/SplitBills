package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "groups_members",
        foreignKeys = [
            ForeignKey(
                    entity = Group::class,
                    parentColumns = ["name"],
                    childColumns = ["group_name"]
            ),
            ForeignKey(
                    entity = Member::class,
                    parentColumns = ["id"],
                    childColumns = ["member_id"]
            )
        ]
)
data class GroupMember constructor(
        @PrimaryKey @ColumnInfo(name = "group_name") val groupName : String,
        @PrimaryKey @ColumnInfo(name = "member_id") val memberId : String
){
}