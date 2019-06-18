/*
package org.weilbach.splitbills.data.local

import androidx.annotation.VisibleForTesting
import org.weilbach.splitbills.data.GroupMemberData
import org.weilbach.splitbills.data.source.GroupsMembersDataSource
import org.weilbach.splitbills.util.AppExecutors

class GroupsMembersLocalDataSource private constructor(
        val appExecutors: AppExecutors,
        val groupsMembersDao: GroupsMembersDao
) : GroupsMembersDataSource {

    override fun deleteGroupMembersByGroupNameSync(groupName: String) {
        groupsMembersDao.deleteGroupMembersByGroupName(groupName)
    }

    override fun deleteAllGroupsMembers() {
        appExecutors.diskIO.execute {
            groupsMembersDao.deleteAllGroupsMembers()
        }
    }

    override fun saveGroupMember(groupMemberData: GroupMemberData) {
        appExecutors.diskIO.execute {
            groupsMembersDao.insertGroupMember(groupMemberData)
        }
    }

    override fun saveGroupMemberSync(groupMemberData: GroupMemberData) {
        groupsMembersDao.insertGroupMember(groupMemberData)
    }

    override fun getGroupMembersByGroupName(groupName: String, callback: GroupsMembersDataSource.GetGroupsMembersCallback){
        appExecutors.diskIO.execute {
            val groupsMembers = groupsMembersDao.getGroupMembersByGroupName(groupName)
            appExecutors.mainThread.execute {
                if (groupsMembers.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onGroupsMembersLoaded(groupsMembers)
                }
            }
        }
    }

    override fun getGroupMembersByGroupNameSync(groupName: String): List<GroupMemberData> {
        return groupsMembersDao.getGroupMembersByGroupName(groupName)
    }

    override fun refreshGroupsMembers() {
    }

    companion object {
        private var INSTANCE: GroupsMembersLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, groupsMembersDao: GroupsMembersDao): GroupsMembersLocalDataSource {
            if (INSTANCE == null) {
                synchronized(GroupsMembersLocalDataSource::javaClass) {
                    INSTANCE = GroupsMembersLocalDataSource(appExecutors, groupsMembersDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}*/
