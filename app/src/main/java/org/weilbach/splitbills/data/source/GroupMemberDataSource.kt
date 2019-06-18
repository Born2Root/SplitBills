package org.weilbach.splitbills.data.source

import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.GroupMember
import org.weilbach.splitbills.data.Member

interface GroupMemberDataSource : BaseDataSource<GroupMember> {

    fun getGroupMembersByGroupName(groupName: String): LiveData<List<GroupMember>>

    fun getGroupMembersMembersByGroupName(groupName: String): LiveData<List<Member>>

    fun getGroupMembersMembersByGroupNameSync(groupName: String): List<Member>

    fun deleteGroupMembersByGroupName(groupName: String)

    fun createNewGroup(group: Group, members: List<Member>)

    fun saveGroupMemberSync(groupMember: GroupMember)
}