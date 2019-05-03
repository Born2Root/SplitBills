package org.weilbach.splitbills.data.local

import androidx.annotation.VisibleForTesting
import org.weilbach.splitbills.data.GroupMember
import org.weilbach.splitbills.data.source.GroupsMembersDataSource
import org.weilbach.splitbills.util.AppExecutors

class GroupsMembersLocalDataSource private constructor(
        val appExecutors: AppExecutors,
        val groupsMembersDao: GroupsMembersDao
) : GroupsMembersDataSource {

    override fun saveGroupMember(groupMember: GroupMember) {
        appExecutors.diskIO.execute {
            groupsMembersDao.insertGroupMember(groupMember)
        }
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
}