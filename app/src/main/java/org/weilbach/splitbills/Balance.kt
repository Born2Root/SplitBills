package org.weilbach.splitbills

import org.weilbach.splitbills.data2.Debtor
import org.weilbach.splitbills.data2.GroupMember
import org.weilbach.splitbills.data2.GroupMembersBillsDebtors
import org.weilbach.splitbills.data2.Member
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*
import kotlin.collections.HashMap

private const val SCALE = 2
private val ROUNDING_MODE = RoundingMode.UP
private const val PRECISION = 34
val MATH_CONTEXT = MathContext(PRECISION, ROUNDING_MODE)

fun prettyPrintNum(num: BigDecimal): String {
    return num.setScale(SCALE, ROUNDING_MODE).toString()
}

/*fun memberGetsFromEachMember(memberData: MemberData,
                             billData: List<BillData>,
                             debtorsByBillId: HashMap<String, List<DebtorData>>
): HashMap<String, BigDecimal> {

    val balanceMap = HashMap<String, BigDecimal>()

    billData.filter { bill -> bill.creditorEmail == memberData.email }.forEach { bill ->
        val debtors = debtorsByBillId[bill.id] ?: LinkedList()

        val amount = BigDecimal(bill.amount)
        val debtorsSize = BigDecimal(debtors.size)
        if (debtorsSize.compareTo(BigDecimal.ZERO) == 0) return@forEach
        val res = amount.divide(debtorsSize, MATH_CONTEXT)

        debtors.filter { debtor -> debtor.memberEmail != memberData.email }.forEach { debtor ->
            if (balanceMap.containsKey(debtor.memberEmail)) {
                balanceMap[debtor.memberEmail] = balanceMap[debtor.memberEmail]!!.add(res)
            } else {
                balanceMap[debtor.memberEmail] = res
            }
                }
    }

    return balanceMap
}*/

fun memberGetsFromGroup(member: Member, group: GroupMembersBillsDebtors): HashMap<String, BigDecimal> {
    val balanceMap = HashMap<String, BigDecimal>()

    if (!group.members.contains(GroupMember(group.group.name, member.email))) {
        return balanceMap
    }

    group.bills.filter { billDebtors ->
        billDebtors.bill.creditorEmail == member.email && billDebtors.bill.valid
    }.forEach { billDebtors ->
        val debtors = billDebtors.debtors
        // val amount = BigDecimal(billDebtors.bill.amount)
        // val debtorsSize = BigDecimal(debtors.size)

        //if (debtorsSize.compareTo(BigDecimal.ZERO) != 0) {
           //  val res = amount.divide(debtorsSize, MATH_CONTEXT)

            debtors.filter { debtor -> debtor.memberEmail != member.email }.forEach { debtor ->
                if (balanceMap.containsKey(debtor.memberEmail)) {
                    balanceMap[debtor.memberEmail] = balanceMap[debtor.memberEmail]!!.add(debtor.amount)
                    // balanceMap[debtor.memberEmail] = balanceMap[debtor.memberEmail]!!.add(res)
                } else {
                    balanceMap[debtor.memberEmail] = debtor.amount
                    // balanceMap[debtor.memberEmail] = res
                }
            }
        // }
    }
    return balanceMap
}

/*fun memberGetsFromGroup(member: Member, group: Group): HashMap<String, BigDecimal> {
    val balanceMap = HashMap<String, BigDecimal>()

    group.bills.items.filter { bill -> bill.creditorEmail == member.email && bill.valid }.forEach { bill ->
        val debtors = bill.debtors.items

        val amount = BigDecimal(bill.amount)
        val debtorsSize = BigDecimal(debtors.size)
        if (debtorsSize.compareTo(BigDecimal.ZERO) == 0) return@forEach
        val res = amount.divide(debtorsSize, MATH_CONTEXT)

        debtors.filter { debtor -> debtor.email != member.email }.forEach { debtor ->
            if (balanceMap.containsKey(debtor.email)) {
                balanceMap[debtor.email] = balanceMap[debtor.email]!!.add(res)
            } else {
                balanceMap[debtor.email] = res
            }
        }
    }

    return balanceMap
}*/

/*fun memberGetsFromEachMemberSmart(memberData: MemberData,
                                  billData: List<BillData>,
                                  debtorsByBillId: HashMap<String, List<DebtorData>>
): HashMap<String, BigDecimal> {
    return memberGetsFromEachMember(memberData, billData, debtorsByBillId)
}*/

fun oweGetMapMerge(oweMap: HashMap<String, BigDecimal>, getMap: HashMap<String, BigDecimal>)
        : Pair<HashMap<String, BigDecimal>, HashMap<String, BigDecimal>> {
    val newOweMap = HashMap<String, BigDecimal>()
    val newGetMap = HashMap<String, BigDecimal>()

    oweMap.forEach { entry ->
        if (getMap.containsKey(entry.key)) {
            val value = getMap[entry.key]
            val res = entry.value.subtract(value, MATH_CONTEXT)
            if (res > BigDecimal.ZERO) {
                newOweMap[entry.key] = res
            } else if (res < BigDecimal.ZERO) {
                newGetMap[entry.key] = res.abs()
            }
        } else {
            newOweMap[entry.key] = entry.value
        }
    }
    getMap.forEach { entry ->
        if (!newGetMap.containsKey(entry.key) && !newOweMap.containsKey(entry.key)) {
            newGetMap[entry.key] = entry.value
        }
    }
    return Pair(newOweMap, newGetMap)
}

/*fun memberOwesEachMember(memberData: MemberData,
                         billData: List<BillData>,
                         debtorsByBillId: HashMap<String, List<DebtorData>>
): HashMap<String, BigDecimal> {

    val balanceMap = HashMap<String, BigDecimal>()
    billData.filter { bill -> bill.creditorEmail != memberData.email }.forEach { bill ->
        val debtors = debtorsByBillId[bill.id] ?: LinkedList()
        if (debtors.isNotEmpty() && debtors.contains(DebtorData(bill.id, memberData.email))) {

            val amount = BigDecimal(bill.amount)
            val debtorsSize = BigDecimal(debtors.size)
            val res = amount.divide(debtorsSize, MATH_CONTEXT)

            if (balanceMap.containsKey(bill.creditorEmail)) {
                balanceMap[bill.creditorEmail] = balanceMap[bill.creditorEmail]!!.add(res)
            } else {
                balanceMap[bill.creditorEmail] = res
            }
        }
    }

    return balanceMap
}*/

fun memberOwesGroup(member: Member, group: GroupMembersBillsDebtors): HashMap<String, BigDecimal> {
    val balanceMap = HashMap<String, BigDecimal>()

    if (!group.members.contains(GroupMember(group.group.name, member.email))) {
        return balanceMap
    }

    group.bills.filter { billDebtors ->
        billDebtors.bill.creditorEmail != member.email && billDebtors.bill.valid
    }.forEach { billDebtors ->
        val debtors = billDebtors.debtors
        val debtor = findDebtorByEmail(member.email, debtors)

        // if (debtors.isNotEmpty() && debtors.contains(Debtor(billDebtors.bill.id, member.email))) {
        if (debtor != null) {

            // val amount = BigDecimal(billDebtors.bill.amount)
            // val debtorsSize = BigDecimal(debtors.size)
            // val res = amount.divide(debtorsSize, MATH_CONTEXT)

            if (balanceMap.containsKey(billDebtors.bill.creditorEmail)) {
                // balanceMap[billDebtors.bill.creditorEmail] = balanceMap[billDebtors.bill.creditorEmail]!!.add(res)
                balanceMap[billDebtors.bill.creditorEmail] = balanceMap[billDebtors.bill.creditorEmail]!!.add(debtor.amount)
            } else {
                // balanceMap[billDebtors.bill.creditorEmail] = res
                balanceMap[billDebtors.bill.creditorEmail] = debtor.amount
            }
        }
    }
    return balanceMap
}

/*fun memberOwesGroup(member: Member, group: Group): HashMap<String, BigDecimal> {

    val balanceMap = HashMap<String, BigDecimal>()

    group.bills.items.filter { bill -> bill.creditorEmail != member.email && bill.valid }.forEach { bill ->
        val debtors = bill.debtors
        if (debtors.items.isNotEmpty() && debtors.contains(member)) {

            val amount = BigDecimal(bill.amount)
            val debtorsSize = BigDecimal(debtors.size)
            val res = amount.divide(debtorsSize, MATH_CONTEXT)

            if (balanceMap.containsKey(bill.creditorEmail)) {
                balanceMap[bill.creditorEmail] = balanceMap[bill.creditorEmail]!!.add(res)
            } else {
                balanceMap[bill.creditorEmail] = res
            }
        }
    }

    return balanceMap
}*/

/*fun memberOwesEachMemberSmart(memberData: MemberData,
                              billData: List<BillData>,
                              debtorsByBillId: HashMap<String, List<DebtorData>>
): HashMap<String, BigDecimal> {
    return memberOwesEachMember(memberData, billData, debtorsByBillId)
}*/


/*fun memberOwesTotal(memberData: MemberData,
                    billData: List<BillData>,
                    debtorsByBillId: HashMap<String, List<DebtorData>>): BigDecimal {
    var total = BigDecimal.ZERO
    val owes = memberOwesEachMember(memberData, billData, debtorsByBillId)

    owes.forEach {owe ->
        total = total.add(owe.value)
    }

    return total
}*/

fun memberOwesGroupTotal(member: Member, group: GroupMembersBillsDebtors): BigDecimal {
    var total = BigDecimal.ZERO
    val owes = memberOwesGroup(member, group)

    owes.forEach {owe ->
        total = total.add(owe.value)
    }

    return total
}

/*fun memberOwesGroupTotal(member: Member, group: Group): BigDecimal {
    var total = BigDecimal.ZERO
    val owes = memberOwesGroup(member, group)

    owes.forEach {owe ->
        total = total.add(owe.value)
    }

    return total
}*/

fun memberGetsFromGroupTotal(member: Member, group: GroupMembersBillsDebtors): BigDecimal {
    var total = BigDecimal.ZERO
    val gets = memberGetsFromGroup(member, group)

    gets.forEach { get ->
        total = total.add(get.value)
    }

    return total
}

/*fun memberGetsTotalFromGroup(member: Member, group: Group): BigDecimal {
    var total = BigDecimal.ZERO
    val gets = memberGetsFromGroup(member, group)

    gets.forEach { get ->
        total = total.add(get.value)
    }

    return total
}*/

/*
fun validBills(billData: List<BillData>): List<BillData> {
    return billData.filter { bill -> bill.valid }
}*/

private fun findDebtorByEmail(email: String, debtors: List<Debtor>): Debtor? {
    for (debtor in debtors) {
        if (debtor.memberEmail == email) {
            return debtor
        }
    }
    return null
}