package org.weilbach.splitbills

/*
class Group (
        val name: String,
        val members: MemberContainer,
        val bills: BillContainer
) : Item(name)

fun mergeGroups(
        group: Group,
        groupToMerge: Group,
        groupMergeCallback: GroupMergeCallback) {

    if (group.id != groupToMerge.id) {
        groupMergeCallback.error(group)
        return
    }

    groupToMerge.members.forEach { member ->
        if (!group.members.contains(member)) {
            group.members.add(member)
            groupMergeCallback.memberAdded(group, member)
        }
    }

    groupToMerge.bills.forEach { bill ->

        val oldBill = group.bills.get(bill.id)

        if (oldBill != null) {

            if (oldBill.valid && !bill.valid) {
                group.bills.remove(oldBill.id)
                val newBill = Bill(
                        oldBill.dateTime,
                        oldBill.description,
                        oldBill.amount,
                        oldBill.currency,
                        oldBill.debtors,
                        oldBill.creditorEmail,
                        oldBill.groupName,
                        false)
                group.bills.add(newBill)
                groupMergeCallback.billRemoved(group, newBill)
            }
        } else {
            group.bills.add(bill)
            groupMergeCallback.billAdded(group, bill)
        }
    }
    groupMergeCallback.success(group)
}*/
