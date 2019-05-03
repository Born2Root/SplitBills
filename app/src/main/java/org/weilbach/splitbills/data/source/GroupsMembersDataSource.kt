package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.GroupMember

interface GroupsMembersDataSource {

    fun saveGroupMember(groupMember: GroupMember)

    fun refreshGroupsMembers()
}