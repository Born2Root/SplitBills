package org.weilbach.splitbills.data2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group constructor(
        @PrimaryKey @ColumnInfo(name = "name") val name: String) {
}

/*
fun addGroup(merge: Triple<Group, List<MemberData>, List<Pair<Bill, List<DebtorData>>>>,
             groupRepository: GroupsRepository,
             memberRepository: MembersRepository,
             groupMemberRepository: GroupsMembersRepository,
             billRepository: BillsRepository,
             debtorRepository: DebtorsRepository,
             success: () -> (Unit)) {
    val group = merge.first
    val members = merge.second
    val bills = merge.third

    AppExecutors().diskIO.execute {
        groupRepository.saveGroup(group, object : GroupsDataSource.SaveGroupCallback {
            override fun onGroupSaved() {
                members.forEach { member ->
                    memberRepository.saveMemberSync(member)
                    groupMemberRepository.saveGroupMemberSync(GroupMemberData(group.name, member.email))
                }
                bills.forEach {
                    val bill = it.first
                    val debtors = it.second
                    billRepository.saveBillSync(bill)
                    debtors.forEach { debtor ->
                        debtorRepository.saveDebtorSync(debtor)
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
               groupRepository: GroupsRepository,
               memberRepository: MembersRepository,
               groupMemberRepository: GroupsMembersRepository,
               billRepository: BillsRepository,
               debtorRepository: DebtorsRepository,
               success: () -> (Unit),
               fail: () -> (Unit)) {
    val group = merge.first
    val members = merge.second
    val bills = merge.third

    groupRepository.getGroup(group.name, object : GroupsDataSource.GetGroupCallback {
        override fun onGroupLoaded(group: GroupData) {
            members.forEach { member ->
                memberRepository.saveMemberSync(member)
                groupMemberRepository.saveGroupMemberSync(GroupMemberData(group.name, member.email))
            }
            bills.forEach {
                val bill = it.first
                val debtors = it.second
                val oldBill = billRepository.getBillSync(bill.id)
                if (oldBill == null) {
                    billRepository.saveBillSync(bill)
                } else {
                    if (!oldBill.valid) {
                        billRepository.saveBillSync(BillData(
                                oldBill.dateTime,
                                oldBill.description,
                                oldBill.amount,
                                oldBill.currency,
                                oldBill.creditorEmail,
                                oldBill.groupName,
                                oldBill.valid))
                    } else {
                        billRepository.saveBillSync(BillData(
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
                    debtorRepository.saveDebtorSync(debtor)
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
