package org.weilbach.splitbills.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.weilbach.splitbills.data.GroupMember

@Dao
interface GroupsMembersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupMember(groupMember: GroupMember)

    @Query("SELECT * FROM groups_members")
    fun getGroupsMembers(): List<GroupMember>

    @Query("DELETE FROM groups_members")
    fun deleteAllGroupsMembers()
}