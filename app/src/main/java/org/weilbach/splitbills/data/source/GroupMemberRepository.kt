package org.weilbach.splitbills.data.source

import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.GroupMember
import org.weilbach.splitbills.data.Member

class GroupMemberRepository private constructor(
        private val groupMemberLocalDataSource: GroupMemberDataSource
) : GroupMemberDataSource {

    override fun addMemberToGroup(group: Group, member: Member) {
        groupMemberLocalDataSource.addMemberToGroup(group, member)
    }

    override fun saveGroupMemberSync(groupMember: GroupMember) {
        groupMemberLocalDataSource.saveGroupMemberSync(groupMember)
    }

    override fun getGroupMembersMembersByGroupNameSync(groupName: String): List<Member> {
        return groupMemberLocalDataSource.getGroupMembersMembersByGroupNameSync(groupName)
    }

    override fun createNewGroup(group: Group, members: List<Member>) {
        groupMemberLocalDataSource.createNewGroup(group, members)
    }

    override fun deleteGroupMembersByGroupName(groupName: String) {
        groupMemberLocalDataSource.deleteGroupMembersByGroupName(groupName)
    }

    override fun getGroupMembersByGroupName(groupName: String): LiveData<List<GroupMember>> {
        return groupMemberLocalDataSource.getGroupMembersByGroupName(groupName)
    }

    override fun getGroupMembersMembersByGroupName(groupName: String): LiveData<List<Member>> {
        return groupMemberLocalDataSource.getGroupMembersMembersByGroupName(groupName)
    }

    override fun getAll(): LiveData<List<GroupMember>> {
        return groupMemberLocalDataSource.getAll()
    }

    override fun save(item: GroupMember) {
        groupMemberLocalDataSource.save(item)
    }

    override fun deleteAll() {
        groupMemberLocalDataSource.deleteAll()
    }

    override fun refresh() {}

    companion object {
        private var INSTANCE: GroupMemberRepository? = null

        @JvmStatic
        fun getInstance(groupsMembersLocalDataSource: GroupMemberDataSource) =
                INSTANCE ?: synchronized(GroupMemberRepository::class.java) {
                    INSTANCE ?: GroupMemberRepository(groupsMembersLocalDataSource)
                            .also { INSTANCE = it }
                }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}