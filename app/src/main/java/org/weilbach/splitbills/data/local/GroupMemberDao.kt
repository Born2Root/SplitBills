package org.weilbach.splitbills.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.GroupMember
import org.weilbach.splitbills.data.Member

@Dao
abstract class GroupMemberDao : BaseDao<GroupMember> {

    @Query("SELECT * FROM groups_members")
    abstract fun getGroupsMembers(): LiveData<List<GroupMember>>

    @Query("DELETE FROM groups_members")
    abstract fun deleteAllGroupsMembers()

    @Query("DELETE FROM groups_members WHERE group_name = :groupName")
    abstract fun deleteGroupMembersByGroupName(groupName: String)

    @Query("SELECT * FROM groups_members WHERE group_name = :groupName")
    abstract fun getGroupMembersByGroupName(groupName: String): LiveData<List<GroupMember>>

    @Query("SELECT members.name, members.email FROM groups_members INNER JOIN members ON members.email = groups_members.member_email WHERE group_name = :groupName")
    abstract fun getGroupMembersMembersByGroupName(groupName: String): LiveData<List<Member>>

    @Query("SELECT members.name, members.email FROM groups_members INNER JOIN members ON members.email = groups_members.member_email WHERE group_name = :groupName")
    abstract fun getGroupMembersMembersByGroupNameSync(groupName: String): List<Member>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertGroup(group: Group)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertMember(member: Member)

    fun createNewGroup(group: Group, members: List<Member>) {
        insertGroup(group)
        members.forEach { member ->
            insertMember(member)
            insert(GroupMember(group.name, member.email))
        }
    }
}