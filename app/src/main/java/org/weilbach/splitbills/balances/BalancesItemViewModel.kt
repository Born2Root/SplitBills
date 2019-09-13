package org.weilbach.splitbills.balances

import android.content.Context
import android.text.Spanned
import android.view.View
import androidx.lifecycle.*
import org.weilbach.splitbills.*
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.util.AppExecutors
import org.weilbach.splitbills.util.fromHtml
import org.weilbach.splitbills.util.getCurrency
import java.math.BigDecimal

class BalancesItemViewModel(
        private val group: Group,
        private val member: Member,
        private val balancesViewModel: BalancesViewModel,
        private val appExecutors: AppExecutors,
        private val appContext: Context
) : ViewModel() {

    private val groupWithMembersAndBillsDebtors = balancesViewModel.groupRepository.getGroupWithMembersAndBillsWithDebtorsByName(group.name)

    private val members = balancesViewModel.groupMemberRepository.getGroupMembersMembersByGroupName(group.name)

    private val _memberTotalColor = MutableLiveData<Int>().apply {
        value = R.color.colorGet
    }
    val memberTotalColor: LiveData<Int>
        get() = _memberTotalColor

    val memberTotal: LiveData<Spanned> = Transformations.map(groupWithMembersAndBillsDebtors) { group ->
        val totalOwe = memberOwesGroupTotal(member, group)
        val totalGet = memberGetsFromGroupTotal(member, group)
        val currency = getCurrency(appContext)

        val res = totalGet.subtract(totalOwe)
        when (res.compareTo(BigDecimal.ZERO)) {
            0 -> {
                _memberTotalColor.value = R.color.colorGet
                fromHtml(appContext.getString(R.string.member_is_settled_up, member.name))
            }
            1 -> {
                _memberTotalColor.value = R.color.colorGet
                fromHtml(appContext.getString(R.string.member_gets, member.name, prettyPrintNum(res), currency.symbol))
            }
            -1 -> {
                _memberTotalColor.value = R.color.colorOwe
                fromHtml(appContext.getString(R.string.member_owes, member.name, prettyPrintNum(res.abs()), currency.symbol))
            }
            else -> {
                fromHtml("")
            }
        }
    }

    val memberOwesMembers: LiveData<Spanned> = MediatorLiveData<Spanned>().apply {

        addSource(groupWithMembersAndBillsDebtors) { group ->
            members.value?.let { members ->
                val currency = getCurrency(appContext)
                val pair = oweGetMapMerge(
                        memberOwesGroup(member, group),
                        memberGetsFromGroup(member, group))
                val owe = pair.first
                // val get = pair.second

                var i = 0
                var s1 = ""
                owe.filter { it.value != BigDecimal.ZERO }.forEach {
                    val memberIndex = findMemberIndex(it.key, members)
                    if (memberIndex > -1) {
                        if (i != 0) s1 += "<br>"
                        s1 += appContext.getString(
                                R.string.member_owes_member,
                                member.name,
                                members[memberIndex].name,
                                prettyPrintNum(it.value),
                                currency.symbol)
                        _memberOwesMembersVisible.value = View.VISIBLE
                    }
                    i++
                }
                value = fromHtml(s1)
            }
        }

        addSource(members) { members ->
            groupWithMembersAndBillsDebtors.value?.let { group ->
                val currency = getCurrency(appContext)
                val pair = oweGetMapMerge(
                        memberOwesGroup(member, group),
                        memberGetsFromGroup(member, group))
                val owe = pair.first
                // val get = pair.second

                var i = 0
                var s1 = ""
                owe.filter { it.value != BigDecimal.ZERO }.forEach {
                    val memberIndex = findMemberIndex(it.key, members)
                    if (memberIndex > -1) {
                        if (i != 0) s1 += "<br>"
                        s1 += appContext.getString(
                                R.string.member_owes_member,
                                member.name,
                                members[memberIndex].name,
                                prettyPrintNum(it.value),
                                currency.symbol)
                        _memberOwesMembersVisible.value = View.VISIBLE
                    }
                    i++
                }
                value = fromHtml(s1)
            }
        }
    }

    val memberGetsMembers: LiveData<Spanned> = MediatorLiveData<Spanned>().apply {

        addSource(groupWithMembersAndBillsDebtors) { group ->
            members.value?.let { members ->
                val currency = getCurrency(appContext)
                val pair = oweGetMapMerge(
                        memberOwesGroup(member, group),
                        memberGetsFromGroup(member, group))

                val get = pair.second

                var i = 0
                var s1 = ""
                get.filter { it.value != BigDecimal.ZERO }.forEach {
                    val memberIndex = findMemberIndex(it.key, members)
                    if (memberIndex > -1) {
                        if (i != 0) s1 += "<br>"
                        s1 += appContext.getString(
                                R.string.member_gets_member,
                                member.name,
                                prettyPrintNum(it.value),
                                currency.symbol,
                                members[memberIndex].name)
                        _memberGetsMembersVisible.value = View.VISIBLE
                    }
                    i++
                }
                value = fromHtml(s1)
            }
        }

        addSource(members) { members ->
            groupWithMembersAndBillsDebtors.value?.let { group ->
                val currency = getCurrency(appContext)
                val pair = oweGetMapMerge(
                        memberOwesGroup(member, group),
                        memberGetsFromGroup(member, group))

                val get = pair.second

                var i = 0
                var s1 = ""
                get.filter { it.value != BigDecimal.ZERO }.forEach {
                    val memberIndex = findMemberIndex(it.key, members)
                    if (memberIndex > -1) {
                        if (i != 0) s1 += "<br>"
                        s1 += appContext.getString(
                                R.string.member_gets_member,
                                member.name,
                                prettyPrintNum(it.value),
                                currency.symbol,
                                members[memberIndex].name)
                        _memberGetsMembersVisible.value = View.VISIBLE
                    }
                    i++
                }
                value = fromHtml(s1)
            }
        }
    }

    private val _memberGetsMembersVisible = MutableLiveData<Int>().apply {
        value = View.GONE
    }
    val memberGetsMembersVisible: LiveData<Int>
        get() = _memberGetsMembersVisible

    private val _memberOwesMembersVisible = MutableLiveData<Int>().apply {
        value = View.GONE
    }
    val memberOwesMembersVisible: LiveData<Int>
        get() = _memberOwesMembersVisible

    private fun findMemberIndex(memberEmail: String, members: List<Member>): Int {
        var res = -1
        var i = 0
        members.forEach { member ->
            if (memberEmail == member.email) {
                res = i
            }
            i++
        }
        return res
    }
}