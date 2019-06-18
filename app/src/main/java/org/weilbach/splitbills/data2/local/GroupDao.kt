package org.weilbach.splitbills.data2.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import org.weilbach.splitbills.data2.Group
import org.weilbach.splitbills.data2.GroupBillsDebtors
import org.weilbach.splitbills.data2.GroupMembersBillsDebtors
import org.weilbach.splitbills.data2.Member

@Dao
abstract class GroupDao : BaseDao<Group> {

    @Query("SELECT * FROM groups")
    abstract fun getGroups(): LiveData<List<Group>>

    @Query("SELECT * FROM groups WHERE name = :groupName")
    abstract fun getGroupByName(groupName: String): LiveData<Group>

    @Query("SELECT * FROM groups WHERE name = :groupName")
    abstract fun getGroupByNameSync(groupName: String): Group?

    @Transaction
    @Query("SELECT * FROM groups WHERE name = :groupName")
    abstract fun getGroupWithBillsWithDebtorsByName(groupName: String): LiveData<GroupBillsDebtors>

    @Transaction
    @Query("SELECT * FROM groups WHERE name = :groupName")
    abstract fun getGroupWithMembersAndBillsWithDebtorsByName(groupName: String): LiveData<GroupMembersBillsDebtors>

    @Transaction
    @Query("SELECT * FROM groups WHERE name = :groupName")
    abstract fun getGroupWithMembersAndBillsWithDebtorsByNameSync(groupName: String): GroupMembersBillsDebtors

    @Transaction
    @Query("SELECT * FROM groups")
    abstract fun getGroupsWithMembersAndBillsWithDebtors(): LiveData<List<GroupMembersBillsDebtors>>

    @Query("DELETE FROM groups")
    abstract fun deleteGroups()
}