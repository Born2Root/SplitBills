package org.weilbach.splitbills.group

import android.content.Context
import androidx.lifecycle.*
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.GroupMembersBillsDebtors
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.memberGetsFromGroup
import org.weilbach.splitbills.memberOwesGroup
import org.weilbach.splitbills.prettyPrintNum
import org.weilbach.splitbills.util.AppExecutors
import org.weilbach.splitbills.util.getCurrencyLive
import org.weilbach.splitbills.util.getUserLive
import java.math.BigDecimal
import java.util.*

class GroupItemViewModel(
        val group: Group,
        private val groupViewModel: GroupViewModel,
        private val appExecutors: AppExecutors,
        private val lifecycleOwner: LifecycleOwner,
        private val appContext: Context

) : ViewModel() {
    val user = getUserLive(appContext)
    val currency = getCurrencyLive(appContext)
    val groupName = group.name

    private val groupMembersBillsDebtors = groupViewModel.groupRepository.getGroupWithMembersAndBillsWithDebtorsByName(groupName)

    private val userOwesTotalAmount: LiveData<BigDecimal> = MediatorLiveData<BigDecimal>().apply {
        addSource(groupMembersBillsDebtors) { groupMembersBillsDebtors ->
            user.value?.let { user ->
                if (groupMembersBillsDebtors != null) {
                    value = calcUserOwesTotalAmount(user, groupMembersBillsDebtors)
                }
            }
        }
        addSource(user) { user ->
            groupMembersBillsDebtors.value?.let { groupMembersBillsDebtors ->
                value = calcUserOwesTotalAmount(user, groupMembersBillsDebtors)
            }
        }
    }

    private fun calcUserOwesTotalAmount(user: Member, groupMembersBillsDebtors: GroupMembersBillsDebtors): BigDecimal {
        var res = BigDecimal.ZERO
        memberOwesGroup(user, groupMembersBillsDebtors).forEach { entry ->
            res = res.add(entry.value)
        }
        return res
    }

    private val userGetsTotalAmount: LiveData<BigDecimal> = MediatorLiveData<BigDecimal>().apply {
        addSource(groupMembersBillsDebtors) { groupMembersBillsDebtors ->
            user.value?.let { user ->
                if (groupMembersBillsDebtors != null) {
                    value = calcUserGetsTotalAmount(user, groupMembersBillsDebtors)
                }
            }
        }
        addSource(user) { user ->
            groupMembersBillsDebtors.value?.let { groupMembersBillsDebtors ->
                value = calcUserGetsTotalAmount(user, groupMembersBillsDebtors)
            }
        }
    }

    private fun calcUserGetsTotalAmount(user: Member, groupMembersBillsDebtors: GroupMembersBillsDebtors): BigDecimal {
        var res = BigDecimal.ZERO
        memberGetsFromGroup(user, groupMembersBillsDebtors).forEach { entry ->
            res = res.add(entry.value)
        }
        return res
    }

    val userOwesOrGetsTotal = MediatorLiveData<String>().apply {
        addSource(userOwesTotalAmount) { owe ->
            userGetsTotalAmount.value?.let { get ->
                currency.value?.let { currency ->
                    user.value?.let { user ->
                        value = calcUserOwesOrGetsTotalAmount(user, get, owe, Currency.getInstance(currency))
                    }
                }
            }
        }

        addSource(userGetsTotalAmount) { get ->
            userOwesTotalAmount.value?.let { owe ->
                currency.value?.let { currency ->
                    user.value?.let { user ->
                        value = calcUserOwesOrGetsTotalAmount(user, get, owe, Currency.getInstance(currency))
                    }
                }
            }
        }

        addSource(currency) { currency ->
            userOwesTotalAmount.value?.let { owe ->
                userGetsTotalAmount.value?.let { get ->
                    user.value?.let { user ->
                        value = calcUserOwesOrGetsTotalAmount(user, get, owe, Currency.getInstance(currency))
                    }
                }
            }
        }

        addSource(user) { user ->
            userOwesTotalAmount.value?.let { owe ->
                userGetsTotalAmount.value?.let { get ->
                    currency.value?.let { currency ->
                        value = calcUserOwesOrGetsTotalAmount(user, get, owe, Currency.getInstance(currency))
                    }
                }
            }
        }
    }

    private fun calcUserOwesOrGetsTotalAmount(user: Member, get: BigDecimal, owe: BigDecimal, currency: Currency): String {
        val res = get.subtract(owe)
        return when (res.compareTo(BigDecimal.ZERO)) {
            1 -> {
                _userOwesOrGetsTotalColor.value = R.color.colorGet
                appContext.getString(R.string.you_get, prettyPrintNum(res), currency.symbol)
            }
            -1 -> {
                _userOwesOrGetsTotalColor.value = R.color.colorOwe
                appContext.getString(R.string.you_owe, prettyPrintNum(res.abs()), currency.symbol)
            }
            else -> {
                _userOwesOrGetsTotalColor.value = R.color.colorGet
                appContext.getString(R.string.you_are_settled_up)
            }
        }
    }

    private val _userOwesOrGetsTotalColor = MutableLiveData<Int>().apply {
        value = R.color.colorGet
    }

    val userOwesOrGetsTotalColor: LiveData<Int>
        get() = _userOwesOrGetsTotalColor
}