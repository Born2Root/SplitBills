package org.weilbach.splitbills.data2.source

import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data2.Group
import org.weilbach.splitbills.data2.GroupMember
import org.weilbach.splitbills.data2.Member

interface GroupMemberDataSource : BaseDataSource<GroupMember> {

    fun getGroupMembersByGroupName(groupName: String): LiveData<List<GroupMember>>

    fun getGroupMembersMembersByGroupName(groupName: String): LiveData<List<Member>>

    fun getGroupMembersMembersByGroupNameSync(groupName: String): List<Member>

    fun deleteGroupMembersByGroupName(groupName: String)

    fun createNewGroup(group: Group, members: List<Member>)

    fun saveGroupMemberSync(groupMember: GroupMember)
}