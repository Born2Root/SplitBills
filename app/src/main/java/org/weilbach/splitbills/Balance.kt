package org.weilbach.splitbills

import org.weilbach.splitbills.data.Debtor
import org.weilbach.splitbills.data.GroupMember
import org.weilbach.splitbills.data.GroupMembersBillsDebtors
import org.weilbach.splitbills.data.Member
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.collections.HashMap

private const val SCALE = 2
private val ROUNDING_MODE = RoundingMode.UP
private const val PRECISION = 34
val MATH_CONTEXT = MathContext(PRECISION, ROUNDING_MODE)

fun prettyPrintNum(num: BigDecimal): String {
    return num.setScale(SCALE, ROUNDING_MODE).toString()
}

fun memberGetsFromGroup(member: Member, group: GroupMembersBillsDebtors): HashMap<String, BigDecimal> {
    val balanceMap = HashMap<String, BigDecimal>()

    if (!group.members.contains(GroupMember(group.group.name, member.email))) {
        return balanceMap
    }

    group.bills.filter { billDebtors ->
        billDebtors.bill.creditorEmail == member.email && billDebtors.bill.valid
    }.forEach { billDebtors ->
        val debtors = billDebtors.debtors
            debtors.filter { debtor -> debtor.memberEmail != member.email }.forEach { debtor ->
                if (balanceMap.containsKey(debtor.memberEmail)) {
                    balanceMap[debtor.memberEmail] = balanceMap[debtor.memberEmail]!!.add(debtor.amount)
                } else {
                    balanceMap[debtor.memberEmail] = debtor.amount
                }
            }
    }
    return balanceMap
}

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

        if (debtor != null) {

            if (balanceMap.containsKey(billDebtors.bill.creditorEmail)) {
                balanceMap[billDebtors.bill.creditorEmail] = balanceMap[billDebtors.bill.creditorEmail]!!.add(debtor.amount)
            } else {
                balanceMap[billDebtors.bill.creditorEmail] = debtor.amount
            }
        }
    }
    return balanceMap
}

fun memberOwesGroupTotal(member: Member, group: GroupMembersBillsDebtors): BigDecimal {
    var total = BigDecimal.ZERO
    val owes = memberOwesGroup(member, group)

    owes.forEach {owe ->
        total = total.add(owe.value)
    }

    return total
}

fun memberGetsFromGroupTotal(member: Member, group: GroupMembersBillsDebtors): BigDecimal {
    var total = BigDecimal.ZERO
    val gets = memberGetsFromGroup(member, group)

    gets.forEach { get ->
        total = total.add(get.value)
    }

    return total
}

private fun findDebtorByEmail(email: String, debtors: List<Debtor>): Debtor? {
    for (debtor in debtors) {
        if (debtor.memberEmail == email) {
            return debtor
        }
    }
    return null
}