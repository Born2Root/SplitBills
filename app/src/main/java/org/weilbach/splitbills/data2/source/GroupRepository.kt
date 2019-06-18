package org.weilbach.splitbills.data2.source

import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data2.Group
import org.weilbach.splitbills.data2.GroupBillsDebtors
import org.weilbach.splitbills.data2.GroupMembersBillsDebtors
import org.weilbach.splitbills.data2.Member

class GroupRepository private constructor(
        val groupLocalDataSource: GroupDataSource
) : GroupDataSource {

    override fun saveGroupSync(group: Group) {
        groupLocalDataSource.saveGroupSync(group)
    }

    override fun getGroupSync(groupName: String): Group? {
        return groupLocalDataSource.getGroupSync(groupName)
    }

    override fun getGroupWithMembersAndBillsWithDebtorsByNameSync(groupName: String): GroupMembersBillsDebtors {
        return groupLocalDataSource.getGroupWithMembersAndBillsWithDebtorsByNameSync(groupName)
    }

    override fun getGroupsWithMembersAndBillsWithDebtors(): LiveData<List<GroupMembersBillsDebtors>> {
        return groupLocalDataSource.getGroupsWithMembersAndBillsWithDebtors()
    }

    override fun getGroupWithMembersAndBillsWithDebtorsByName(groupName: String): LiveData<GroupMembersBillsDebtors> {
        return groupLocalDataSource.getGroupWithMembersAndBillsWithDebtorsByName(groupName)
    }

    override fun getGroupWithBillsWithDebtorsByName(groupName: String): LiveData<GroupBillsDebtors> {
        return groupLocalDataSource.getGroupWithBillsWithDebtorsByName(groupName)
    }

    override fun deleteGroup(groupName: String) {
        groupLocalDataSource.deleteGroup(groupName)
    }

    override fun getAll(): LiveData<List<Group>> {
        return groupLocalDataSource.getAll()
    }

    override fun save(item: Group) {
        groupLocalDataSource.save(item)
    }

    override fun deleteAll() {
        groupLocalDataSource.deleteAll()
    }

    override fun refresh() { }

    companion object {

        private var INSTANCE: GroupRepository? = null

        @JvmStatic
        fun getInstance(groupsLocalDataSource: GroupDataSource) =
                INSTANCE ?: synchronized(GroupRepository::class.java) {
                    INSTANCE ?: GroupRepository(groupsLocalDataSource)
                            .also { INSTANCE = it }
                }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}