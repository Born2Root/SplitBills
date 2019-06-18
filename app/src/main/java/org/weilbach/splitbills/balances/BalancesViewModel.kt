package org.weilbach.splitbills.balances

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.data.source.*

class BalancesViewModel(
        val billRepository: BillRepository,
        val memberRepository: MemberRepository,
        val groupMemberRepository: GroupMemberRepository,
        val debtorRepository: DebtorRepository,
        val groupRepository: GroupRepository
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

    fun start(groupName: String) {
        group.value = Group(groupName)
    }

    fun loadMembers(forceUpdate: Boolean) {
        _dataLoading.value = false
    }

    companion object {
        private const val TAG = "BalancesViewModel"
    }
}