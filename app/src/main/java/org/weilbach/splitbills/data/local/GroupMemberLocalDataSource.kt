package org.weilbach.splitbills.data.local

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.GroupMember
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.data.source.GroupMemberDataSource
import org.weilbach.splitbills.util.AppExecutors

class GroupMemberLocalDataSource private constructor(
        val appExecutors: AppExecutors,
        val groupMemberDao: GroupMemberDao
) : GroupMemberDataSource {

    override fun saveGroupMemberSync(groupMember: GroupMember) {
        groupMemberDao.insert(groupMember)
    }

    override fun getGroupMembersMembersByGroupNameSync(groupName: String): List<Member> {
        return groupMemberDao.getGroupMembersMembersByGroupNameSync(groupName)
    }

    override fun createNewGroup(group: Group, members: List<Member>) {
        appExecutors.diskIO.execute { groupMemberDao.createNewGroup(group, members) }
    }

    override fun deleteGroupMembersByGroupName(groupName: String) {
        appExecutors.diskIO.execute { groupMemberDao.deleteGroupMembersByGroupName(groupName) }
    }

    override fun getGroupMembersByGroupName(groupName: String): LiveData<List<GroupMember>> {
        return groupMemberDao.getGroupMembersByGroupName(groupName)
    }

    override fun getGroupMembersMembersByGroupName(groupName: String): LiveData<List<Member>> {
        return groupMemberDao.getGroupMembersMembersByGroupName(groupName)
    }

    override fun getAll(): LiveData<List<GroupMember>> {
        return groupMemberDao.getGroupsMembers()
    }

    override fun save(item: GroupMember) {
        appExecutors.diskIO.execute { groupMemberDao.insert(item) }
    }

    override fun deleteAll() {
        appExecutors.diskIO.execute { groupMemberDao.deleteAllGroupsMembers() }
    }

    override fun refresh() {}

    companion object {
        private var INSTANCE: GroupMemberLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, groupsMembersDao: GroupMemberDao): GroupMemberLocalDataSource {
            if (INSTANCE == null) {
                synchronized(GroupMemberLocalDataSource::javaClass) {
                    INSTANCE = GroupMemberLocalDataSource(appExecutors, groupsMembersDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}