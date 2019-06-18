package org.weilbach.splitbills.group

import android.content.Context
import androidx.lifecycle.*
import org.weilbach.splitbills.*
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data2.Group
import org.weilbach.splitbills.util.AppExecutors
import org.weilbach.splitbills.util.getCurrency
import org.weilbach.splitbills.util.getUser
import java.math.BigDecimal

class GroupItemViewModel(
        val group: Group,
        private val groupViewModel: GroupViewModel,
        private val appExecutors: AppExecutors,
        private val lifecycleOwner: LifecycleOwner,
        private val appContext: Context

) : ViewModel() {
    val user = getUser(appContext)
    val currency = getCurrency(appContext)
    val groupName = group.name

    private val groupMembersBillsDebtors = groupViewModel.groupRepository.getGroupWithMembersAndBillsWithDebtorsByName(groupName)

    /*val userOwesTotalAmount = MutableLiveData<String>().apply {
        value = "" // appContext.getString(R.string.calculating)
    }*/

    val userOwesTotalAmount: LiveData<BigDecimal> = Transformations.map(groupMembersBillsDebtors) { group ->
        var res = BigDecimal.ZERO
        group?.let {
            memberOwesGroup(user, it).forEach { entry ->
                res = res.add(entry.value)
            }
        }
        res
    }

    /*val userGetsTotalAmount = MutableLiveData<String>().apply {
        value = "" // appContext.getString(R.string.calculating)
    }*/

    val userGetsTotalAmount: LiveData<BigDecimal> = Transformations.map(groupMembersBillsDebtors) { group ->
        var res = BigDecimal.ZERO
        group?.let {
            memberGetsFromGroup(user, it).forEach { entry ->
                res = res.add(entry.value)
            }
        }
        res
    }

    /*val userOwesOrGetsTotal = MutableLiveData<String>().apply {
        value = "" // appContext.getString(R.string.calculating)
    }*/

    val userOwesOrGetsTotal = MediatorLiveData<String>().apply {
        addSource(userOwesTotalAmount) { owe ->
            userGetsTotalAmount.value?.let { get ->
                val res = get.subtract(owe)
                when (res.compareTo(BigDecimal.ZERO)) {
                    0 -> {
                        _userOwesOrGetsTotalColor.value = R.color.colorGet
                        value = appContext.getString(R.string.you_are_settled_up)
                    }
                    1 -> {
                        _userOwesOrGetsTotalColor.value = R.color.colorGet
                        value = appContext.getString(R.string.you_get, prettyPrintNum(res), currency.symbol)
                    }
                    -1 -> {
                        _userOwesOrGetsTotalColor.value = R.color.colorOwe
                        value = appContext.getString(R.string.you_owe, prettyPrintNum(res.abs()), currency.symbol)
                    }
                }
            }

        }

        addSource(userGetsTotalAmount) { get ->
            userOwesTotalAmount.value?.let { owe ->
                val res = get.subtract(owe)
                when (res.compareTo(BigDecimal.ZERO)) {
                    0 -> {
                        _userOwesOrGetsTotalColor.value = R.color.colorGet
                        value = appContext.getString(R.string.you_are_settled_up)
                    }
                    1 -> {
                        _userOwesOrGetsTotalColor.value = R.color.colorGet
                        value = appContext.getString(R.string.you_get, prettyPrintNum(res), currency.symbol)
                    }
                    -1 -> {
                        _userOwesOrGetsTotalColor.value = R.color.colorOwe
                        value = appContext.getString(R.string.you_owe, prettyPrintNum(res.abs()), currency.symbol)
                    }
                }
            }
        }
    }

    private val _userOwesOrGetsTotalColor = MutableLiveData<Int>().apply {
        value = R.color.colorGet
    }
    val userOwesOrGetsTotalColor: LiveData<Int>
        get() = _userOwesOrGetsTotalColor

    init {
        /*groupViewModel.billUpdatedEvent.observe(lifecycleOwner, Observer<Event<Unit>> {
            setup()
        })*/
        // setup()
    }

    private fun setup() {
/*        appExecutors.diskIO.execute {
            groupViewModel.billRepository.refreshBills()
            groupViewModel.groupRepository.refreshGroups()
            groupViewModel.amountsRepository.refreshAmounts()
            groupViewModel.debtorRepository.refreshDebtors()
            groupViewModel.groupMemberRepository.refreshGroupsMembers()

            val debtorsByBillId = HashMap<String, List<DebtorData>>()
            val amountByBillId = HashMap<String, AmountData>()

            val bills = validBills(groupViewModel.billRepository.getBillsByGroupNameSync(groupName))

            bills.forEach { bill ->
                debtorsByBillId[bill.id] = groupViewModel.debtorRepository.getDebtorsByBillIdSync(bill.id)
                val amount = groupViewModel.amountsRepository.getValidAmountByBillIdSync(bill.id)
                if (amount != null) {
                    amountByBillId[bill.id] = amount
                }
            }
            val u = getUser(appContext)
            val user = MemberData(u.name, u.email)
            val totalOwe = memberOwesTotal(user, bills, debtorsByBillId, amountByBillId)
            val totalGet = memberGetsTotal(user, bills, debtorsByBillId, amountByBillId)

            appExecutors.mainThread.execute {
                userOwesTotalAmount.value = "$totalOwe"
                userGetsTotalAmount.value = "$totalGet"
                val currency = getCurrency(appContext)
                val res = totalGet.subtract(totalOwe)
                when (res.compareTo(BigDecimal.ZERO)) {
                    0 -> {
                        _userOwesOrGetsTotalColor.value = R.color.colorGet
                        userOwesOrGetsTotal.value = appContext.getString(R.string.you_are_settled_up)
                    }
                    1 -> {
                        _userOwesOrGetsTotalColor.value = R.color.colorGet
                        userOwesOrGetsTotal.value = appContext.getString(R.string.you_get, prettyPrintNum(res), currency.symbol)
                    }
                    -1 -> {
                        _userOwesOrGetsTotalColor.value = R.color.colorOwe
                        userOwesOrGetsTotal.value = appContext.getString(R.string.you_owe, prettyPrintNum(res.abs()), currency.symbol)
                    }
                }
            }
        }*/
    }
}