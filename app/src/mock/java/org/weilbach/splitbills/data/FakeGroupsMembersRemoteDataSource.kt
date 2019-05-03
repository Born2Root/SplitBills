package org.weilbach.splitbills.data

import org.weilbach.splitbills.data.source.GroupsMembersDataSource

class FakeGroupsMembersRemoteDataSource : GroupsMembersDataSource {

    private var GROUP_MEMBER_SERVICE_DATA: LinkedHashMap<String, GroupMember> = LinkedHashMap()

    override fun saveGroupMember(groupMember: GroupMember) {
        GROUP_MEMBER_SERVICE_DATA
    }

    override fun refreshGroupsMembers() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}