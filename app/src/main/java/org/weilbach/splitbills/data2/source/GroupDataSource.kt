package org.weilbach.splitbills.data2.source

import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data2.Group
import org.weilbach.splitbills.data2.GroupBillsDebtors
import org.weilbach.splitbills.data2.GroupMembersBillsDebtors
import org.weilbach.splitbills.data2.Member

interface GroupDataSource : BaseDataSource<Group> {

    fun saveGroupSync(group: Group)

    fun deleteGroup(groupName: String)

    fun getGroupSync(groupName: String): Group?

    fun getGroupWithBillsWithDebtorsByName(groupName: String): LiveData<GroupBillsDebtors>

    fun getGroupWithMembersAndBillsWithDebtorsByName(groupName: String): LiveData<GroupMembersBillsDebtors>

    fun getGroupWithMembersAndBillsWithDebtorsByNameSync(groupName: String): GroupMembersBillsDebtors

    fun getGroupsWithMembersAndBillsWithDebtors(): LiveData<List<GroupMembersBillsDebtors>>
}