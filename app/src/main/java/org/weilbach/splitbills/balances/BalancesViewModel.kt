package org.weilbach.splitbills.balances

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.R
import org.weilbach.splitbills.util.Event
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.data.source.*
import org.weilbach.splitbills.prettyPrintNum
import org.weilbach.splitbills.util.getCurrency
import java.math.BigDecimal

class BalancesViewModel(
        val billRepository: BillRepository,
        val memberRepository: MemberRepository,
        val groupMemberRepository: GroupMemberRepository,
        val debtorRepository: DebtorRepository,
        val groupRepository: GroupRepository,
        val appContext: Context
) : ViewModel() {

    private val group = MutableLiveData<Group>()

    val items: LiveData<List<Member>> = Transformations.switchMap(group) {
        _dataLoading.value = false
        groupMemberRepository.getGroupMembersMembersByGroupName(it.name)
    }

    private val _dataLoading = MutableLiveData<Boolean>().apply { value = false }
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _groupSpent = MutableLiveData<String>()
    val groupSpent: LiveData<String>
        get() = _groupSpent

    fun start(groupName: String) {
        group.value = Group(groupName)

        // FIXME: Must not be ordered
        val bills = billRepository.getBillsByGroupNameOrdered(groupName)

        bills.observeForever { bills ->
            var res = BigDecimal.ZERO
            bills.filter { bill -> bill.valid }.forEach { bill ->
                res = res.add(bill.amount)
            }

            _groupSpent.value = appContext.getString(R.string.group_spent, groupName, prettyPrintNum(res), getCurrency(appContext).symbol)
        }
    }

    fun loadMembers(forceUpdate: Boolean) {
        _dataLoading.value = false
    }

    companion object {
        private const val TAG = "BalancesViewModel"
    }
}