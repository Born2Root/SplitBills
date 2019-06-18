package org.weilbach.splitbills.balances

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.data2.Group
import org.weilbach.splitbills.data2.Member
import org.weilbach.splitbills.data2.source.*

class BalancesViewModel(
        val billRepository: BillRepository,
        val memberRepository: MemberRepository,
        val groupMemberRepository: GroupMemberRepository,
        val debtorRepository: DebtorRepository,
        val groupRepository: GroupRepository
) : ViewModel() {

    // private var groupName: String? = null

    private val group = MutableLiveData<Group>()

/*    private val _items = MutableLiveData<List<Member>>().apply { value = emptyList() }
    val items: LiveData<List<Member>>
        get() = _items*/

    val items: LiveData<List<Member>> = Transformations.switchMap(group) {
        _dataLoading.value = false
        groupMemberRepository.getGroupMembersMembersByGroupName(it.name)
    }

    private val _dataLoading = MutableLiveData<Boolean>().apply { value = true }
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    fun start(groupName: String) {
        group.value = Group(groupName)
        // this.groupName = groupName
        //loadMembers(groupName, true)
    }

    fun loadMembers(forceUpdate: Boolean) {
        _dataLoading.value = false
        /*groupName?.let {
            loadMembers(it, forceUpdate)
            return
        }
        Log.w(TAG, "no group name set, can not load bills")*/
    }

    fun loadMembers(groupName: String, forceUpdate: Boolean) {
        // this.groupName = groupName
        //loadMembers(forceUpdate, true, groupName)
    }

    private fun loadMembers(forceUpdate: Boolean, showLoadingUi: Boolean, groupName: String) {
/*        if (showLoadingUi) {
            _dataLoading.value = true
        }
        if (forceUpdate) {
            memberRepository.refreshMembers()
            groupMemberRepository.refreshGroupsMembers()
        }

        groupMemberRepository.getGroupMembersByGroupName(groupName, object : GroupsMembersDataSource.GetGroupsMembersCallback {
            override fun onGroupsMembersLoaded(groupsMemberData: List<GroupMemberData>) {
                val members = ArrayList<Member>()

                groupsMemberData.forEach {
                    memberRepository.getMember(it.memberEmail, object : MembersDataSource.GetMemberCallback {
                        override fun onMemberLoaded(memberData: MemberData) {
                            members.add(Member(memberData.name, memberData.email))
                            _items.value = members
                        }

                        override fun onDataNotAvailable() {
                            Log.w(TAG, "MemberData not found")
                        }
                    })

                    if (showLoadingUi) {
                        _dataLoading.value = false
                    }
                }
            }

            override fun onDataNotAvailable() {
                if (showLoadingUi) {
                    _dataLoading.value = false
                }
            }
        })*/
    }

    companion object {
        private const val TAG = "BalancesViewModel"
    }
}