package org.weilbach.splitbills.balances

import android.content.Context
import android.text.Spanned
import android.view.View
import androidx.lifecycle.*
import org.weilbach.splitbills.*
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data2.Group
import org.weilbach.splitbills.data2.Member
import org.weilbach.splitbills.data2.source.GroupRepository
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

    /*val memberTotal = MutableLiveData<Spanned>().apply{
        value = fromHtml("")
    }*/

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

    /*val memberOwesMembers = MutableLiveData<Spanned>().apply {
        value = fromHtml("")
    }*/

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

    /*val memberGetsMembers = MutableLiveData<Spanned>().apply {
        value = fromHtml("")
    }*/

    val memberGetsMembers: LiveData<Spanned> = MediatorLiveData<Spanned>().apply {

        addSource(groupWithMembersAndBillsDebtors) { group ->
            members.value?.let { members ->
                val currency = getCurrency(appContext)
                val pair = oweGetMapMerge(
                        memberOwesGroup(member, group),
                        memberGetsFromGroup(member, group))
                //val owe = pair.first
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
                //val owe = pair.first
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

    init {
        setup()
    }

    private fun findMemberIndex(memberEmail: String, members: List<Member>) :Int {
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

    private fun setup() {
        // memberTotal.value = member.name

/*        appExecutors.diskIO.execute {
            val debtorsByBillId = HashMap<String, List<DebtorData>>()
            val amountByBillId = HashMap<String, AmountData>()

            val bills = validBills(balancesViewModel.billRepository.getBillsByGroupNameSync(group.name))

            bills.forEach { bill ->
                debtorsByBillId[bill.id] = balancesViewModel.debtorRepository.getDebtorsByBillIdSync(bill.id)
                val amount = balancesViewModel.amountsRepository.getValidAmountByBillIdSync(bill.id)
                if (amount != null) {
                    amountByBillId[bill.id] = amount
                }
            }

            val memberData = MemberData(member.name, member.email)
            val pair = oweGetMapMerge(
                    memberOwesEachMemberSmart(memberData, bills, debtorsByBillId, amountByBillId),
                    memberGetsFromEachMemberSmart(memberData, bills, debtorsByBillId, amountByBillId))
            val owe = pair.first
            val get = pair.second

            val totalOwe = memberOwesTotal(memberData, bills, debtorsByBillId, amountByBillId)
            val totalGet = memberGetsTotal(memberData, bills, debtorsByBillId, amountByBillId)

            val oweWithMember = HashMap<MemberData, BigDecimal>()
            owe.forEach {
                balancesViewModel.memberRepository.getMemberSync(it.key)?.let { member ->
                    oweWithMember[member] = it.value
                }
            }

            val getWithMember = HashMap<MemberData, BigDecimal>()
            get.forEach {
                balancesViewModel.memberRepository.getMemberSync(it.key)?.let { member ->
                    getWithMember[member] = it.value
                }
            }

            appExecutors.mainThread.execute {
                val currency = getCurrency(appContext)
                val res = totalGet.subtract(totalOwe)
                when (res.compareTo(BigDecimal.ZERO)) {
                    0 -> {
                        _memberTotalColor.value = R.color.colorGet
                        memberTotal.value = fromHtml(appContext.getString(R.string.member_is_settled_up, member.name))
                    }
                    1 -> {
                        _memberTotalColor.value = R.color.colorGet
                        memberTotal.value = fromHtml(appContext.getString(R.string.member_gets, member.name, prettyPrintNum(res), currency.symbol))
                    }
                    -1 -> {
                        _memberTotalColor.value = R.color.colorOwe
                        memberTotal.value = fromHtml(appContext.getString(R.string.member_owes, member.name, prettyPrintNum(res.abs()), currency.symbol))
                    }
                }

                var i = 0
                var s1 = ""
                oweWithMember.filter { it.value != BigDecimal.ZERO }.forEach {
                    if (i != 0) s1 += "<br>"
                    s1 += appContext.getString(R.string.member_owes_member, member.name, it.key.name, prettyPrintNum(it.value), currency.symbol)
                    _memberOwesMembersVisible.value = View.VISIBLE
                    i++
                }
                memberOwesMembers.value = fromHtml(s1)

                var j = 0
                var s2 = ""
                getWithMember.filter { it.value != BigDecimal.ZERO }.forEach {
                    if (j != 0) s2 += "<br>"
                    s2 += appContext.getString(R.string.member_gets_member, member.name, prettyPrintNum(it.value), currency.symbol, it.key.name)
                    _memberGetsMembersVisible.value = View.VISIBLE
                    j++
                }
                memberGetsMembers.value = fromHtml(s2)
            }
        }*/
    }
}