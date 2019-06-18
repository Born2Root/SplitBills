/*
package org.weilbach.splitbills.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.weilbach.splitbills.data.GroupMemberData

@Dao
interface GroupsMembersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupMember(groupMemberData: GroupMemberData)

    @Query("SELECT * FROM groups_members")
    fun getGroupsMembers(): List<GroupMemberData>

    @Query("DELETE FROM groups_members")
    fun deleteAllGroupsMembers()

    @Query("DELETE FROM groups_members WHERE group_name = :groupName")
    fun deleteGroupMembersByGroupName(groupName: String)

    @Query("SELECT * FROM groups_members WHERE group_name = :groupName")
    fun getGroupMembersByGroupName(groupName: String): List<GroupMemberData>
}*/
