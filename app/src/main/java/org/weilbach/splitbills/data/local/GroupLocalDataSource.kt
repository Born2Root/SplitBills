package org.weilbach.splitbills.data.local

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.GroupBillsDebtors
import org.weilbach.splitbills.data.GroupMembersBillsDebtors
import org.weilbach.splitbills.data.source.GroupDataSource
import org.weilbach.splitbills.util.AppExecutors

class GroupLocalDataSource private constructor(
        val appExecutors: AppExecutors,
        val groupDao: GroupDao
) : GroupDataSource {

    override fun getGroupSync(groupName: String): Group? {
        return groupDao.getGroupByNameSync(groupName)
    }

    override fun saveGroupSync(group: Group) {
        groupDao.insert(group)
    }

    override fun getGroupWithMembersAndBillsWithDebtorsByNameSync(groupName: String): GroupMembersBillsDebtors {
        return groupDao.getGroupWithMembersAndBillsWithDebtorsByNameSync(groupName)
    }

    override fun getGroupsWithMembersAndBillsWithDebtors(): LiveData<List<GroupMembersBillsDebtors>> {
        return groupDao.getGroupsWithMembersAndBillsWithDebtors()
    }

    override fun getGroupWithMembersAndBillsWithDebtorsByName(groupName: String): LiveData<GroupMembersBillsDebtors> {
        return groupDao.getGroupWithMembersAndBillsWithDebtorsByName(groupName)
    }

    override fun getGroupWithBillsWithDebtorsByName(groupName: String): LiveData<GroupBillsDebtors> {
        return groupDao.getGroupWithBillsWithDebtorsByName(groupName)
    }

    override fun deleteGroup(groupName: String) {
        appExecutors.diskIO.execute { groupDao.delete(Group(groupName)) }
    }

    override fun getAll(): LiveData<List<Group>> {
        return groupDao.getGroups()
    }

    override fun save(item: Group) {
        appExecutors.diskIO.execute { groupDao.insert(item) }
    }

    override fun deleteAll() {
        appExecutors.diskIO.execute { groupDao.deleteGroups() }
    }

    override fun refresh() {}

    companion object {
        private var INSTANCE: GroupLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, groupsDao: GroupDao): GroupLocalDataSource {
            if (INSTANCE == null) {
                synchronized(GroupLocalDataSource::javaClass) {
                    INSTANCE = GroupLocalDataSource(appExecutors, groupsDao)
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