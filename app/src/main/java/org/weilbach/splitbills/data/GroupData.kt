/*
package org.weilbach.splitbills.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.weilbach.splitbills.data.source.*
import org.weilbach.splitbills.util.AppExecutors

@Entity(tableName = "groups")
data class GroupData constructor(
        @PrimaryKey @ColumnInfo(name = "name") val name: String) {
}

*/
/*
fun addGroup(merge: Triple<GroupData, List<MemberData>, List<Pair<BillData, List<DebtorData>>>>,
             groupsRepository: GroupsRepository,
             membersRepository: MembersRepository,
             groupsMembersRepository: GroupsMembersRepository,
             billsRepository: BillsRepository,
             debtorsRepository: DebtorsRepository,
             success: () -> (Unit)) {
    val group = merge.first
    val members = merge.second
    val bills = merge.third

    AppExecutors().diskIO.execute {
        groupsRepository.saveGroup(group, object : GroupsDataSource.SaveGroupCallback {
            override fun onGroupSaved() {
                members.forEach { member ->
                    membersRepository.saveMemberSync(member)
                    groupsMembersRepository.saveGroupMemberSync(GroupMemberData(group.name, member.email))
                }
                bills.forEach {
                    val bill = it.first
                    val debtors = it.second
                    billsRepository.saveBillSync(bill)
                    debtors.forEach { debtor ->
                        debtorsRepository.saveDebtorSync(debtor)
                    }
                }
                success()
            }
            override fun onDataNotAvailable() {
            }
        })
    }
}

fun mergeGroup(merge: Triple<GroupData, List<MemberData>, List<Pair<BillData, List<DebtorData>>>>,
               groupsRepository: GroupsRepository,
               membersRepository: MembersRepository,
               groupsMembersRepository: GroupsMembersRepository,
               billsRepository: BillsRepository,
               debtorsRepository: DebtorsRepository,
               success: () -> (Unit),
               fail: () -> (Unit)) {
    val group = merge.first
    val members = merge.second
    val bills = merge.third

    groupsRepository.getGroup(group.name, object : GroupsDataSource.GetGroupCallback {
        override fun onGroupLoaded(group: GroupData) {
            members.forEach { member ->
                membersRepository.saveMemberSync(member)
                groupsMembersRepository.saveGroupMemberSync(GroupMemberData(group.name, member.email))
            }
            bills.forEach {
                val bill = it.first
                val debtors = it.second
                val oldBill = billsRepository.getBillSync(bill.id)
                if (oldBill == null) {
                    billsRepository.saveBillSync(bill)
                } else {
                    if (!oldBill.valid) {
                        billsRepository.saveBillSync(BillData(
                                oldBill.dateTime,
                                oldBill.description,
                                oldBill.amount,
                                oldBill.currency,
                                oldBill.creditorEmail,
                                oldBill.groupName,
                                oldBill.valid))
                    } else {
                        billsRepository.saveBillSync(BillData(
                                oldBill.dateTime,
                                oldBill.description,
                                oldBill.amount,
                                oldBill.currency,
                                oldBill.creditorEmail,
                                oldBill.groupName,
                                bill.valid))
                    }
                }
                debtors.forEach { debtor ->
                    debtorsRepository.saveDebtorSync(debtor)
                }
            }
            success()
        }

        override fun onDataNotAvailable() {
            //TODO: new group, should add?
            fail()
        }
    })
}*/

