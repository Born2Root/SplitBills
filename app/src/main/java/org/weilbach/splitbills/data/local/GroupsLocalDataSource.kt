package org.weilbach.splitbills.data.local

import androidx.annotation.VisibleForTesting
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.source.GroupsDataSource
import org.weilbach.splitbills.util.AppExecutors

class GroupsLocalDataSource private constructor(
        val appExecutors: AppExecutors,
        val groupsDao: GroupsDao
) : GroupsDataSource {

    override fun getGroups(callback: GroupsDataSource.GetGroupsCallback) {
        appExecutors.diskIO.execute {
            val groups = groupsDao.getGroups()
            appExecutors.mainThread.execute {
                if (groups.isEmpty()) {
                    // This will be called if the table is new or just empty.
                    callback.onDataNotAvailable()
                } else {
                    callback.onGroupsLoaded(groups)
                }
            }
        }
    }

    override fun getGroup(groupName: String, callback: GroupsDataSource.GetGroupCallback) {
        val group = groupsDao.getGroupByName(groupName)
        appExecutors.mainThread.execute {
            if (group != null) {
                callback.onGroupLoaded(group)
            } else {
                callback.onDataNotAvailable()
            }
        }
    }

    override fun saveGroup(group: Group, callback: GroupsDataSource.SaveGroupCallback) {
        appExecutors.diskIO.execute { groupsDao.insertGroup(group) }
    }

    override fun deleteGroup(groupName: String, callback: GroupsDataSource.DeleteGroupCallback) {
        appExecutors.diskIO.execute { groupsDao.deleteGroupByName(groupName) }
    }

    override fun deleteAllGroups(callback: GroupsDataSource.DeleteGroupsCallback) {
        appExecutors.diskIO.execute { groupsDao.deleteGroups() }
    }

    override fun refreshGroups() {
        //To change body of created functions use File | Settings | File Templates.
        // Not required because the {@link GroupsRepository} handles the logic of refreshing the
        // groups from all the available data sources.
    }

    companion object {
        private var INSTANCE: GroupsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, groupsDao: GroupsDao): GroupsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(GroupsLocalDataSource::javaClass) {
                    INSTANCE = GroupsLocalDataSource(appExecutors, groupsDao)
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