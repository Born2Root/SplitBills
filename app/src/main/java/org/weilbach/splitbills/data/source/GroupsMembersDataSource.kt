/*
package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.GroupMemberData

interface GroupsMembersDataSource {

    interface GetGroupsMembersCallback {
        fun onGroupsMembersLoaded(groupsMemberData: List<GroupMemberData>)
        fun onDataNotAvailable()
    }

    fun saveGroupMember(groupMemberData: GroupMemberData)

    fun saveGroupMemberSync(groupMemberData: GroupMemberData)

    fun getGroupMembersByGroupName(groupName: String, callback: GetGroupsMembersCallback)

    fun getGroupMembersByGroupNameSync(groupName: String): List<GroupMemberData>

    // fun getGroupMembersByGroupNameSync(groupName: String)

    fun refreshGroupsMembers()

    fun deleteAllGroupsMembers()

    fun deleteGroupMembersByGroupNameSync(groupName: String)
}*/
