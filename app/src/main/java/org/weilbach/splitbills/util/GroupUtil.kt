// package org.weilbach.splitbills.util

/*
fun mergeGroup(merge: Triple<GroupData, List<MemberData>, List<Triple<BillData, List<DebtorData>, List<AmountData>>>>,
               groupRepository: GroupsRepository,
               memberRepository: MembersRepository,
               groupMemberRepository: GroupsMembersRepository,
               billRepository: BillsRepository,
               debtorRepository: DebtorsRepository,
               amountsRepository: AmountsRepository,
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
                val amounts = it.third
                val oldBill = billRepository.getBillSync(bill.id)
                if (oldBill == null) {
                    billRepository.saveBillSync(bill)
                } else {
                    if (!oldBill.valid) {
                        billRepository.saveBillSync(BillData(
                                oldBill.dateTime,
                                oldBill.description,
                                oldBill.creditorEmail,
                                oldBill.groupName,
                                oldBill.valid))
                    } else {
                        billRepository.saveBillSync(BillData(
                                oldBill.dateTime,
                                oldBill.description,
                                oldBill.creditorEmail,
                                oldBill.groupName,
                                bill.valid))
                    }
                }
                debtors.forEach { debtor ->
                    debtorRepository.saveDebtorSync(debtor)
                }
                amounts.forEach { amount ->
                    amountsRepository.saveAmountSync(amount)
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
