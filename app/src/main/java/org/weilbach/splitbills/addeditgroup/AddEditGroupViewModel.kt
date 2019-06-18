package org.weilbach.splitbills.addeditgroup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.MemberItemNavigator
import org.weilbach.splitbills.R
import org.weilbach.splitbills.addmember.AddMemberActivity
import org.weilbach.splitbills.data2.Group
import org.weilbach.splitbills.data2.Member
import org.weilbach.splitbills.data2.source.GroupMemberRepository
import org.weilbach.splitbills.data2.source.GroupRepository
import org.weilbach.splitbills.data2.source.MemberRepository
import org.weilbach.splitbills.util.AppExecutors
import org.weilbach.splitbills.util.getUser

class AddEditGroupViewModel(
        private val groupsRepository: GroupRepository,
        private val membersRepository: MemberRepository,
        private val groupsMembersRepository: GroupMemberRepository,
        private val appExecutors: AppExecutors,
        private val appContext: Context
) : ViewModel(), MemberItemNavigator {

    val memberContainer = HashMap<String, Member>()

    val name = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _groupUpdated = MutableLiveData<Event<Unit>>()
    val groupUpdatedEvent: LiveData<Event<Unit>>
        get() = _groupUpdated

    private val _addMember = MutableLiveData<Event<Unit>>()
    val addMember: LiveData<Event<Unit>>
        get() = _addMember

    private val _memberUpdated = MutableLiveData<Event<Member>>()
    val memberDataUpdatedEvent: LiveData<Event<Member>>
        get() = _memberUpdated

    private val _memberItems = MutableLiveData<ArrayList<Member>>().apply {
        val user = getUser(appContext)
        value = arrayListOf(user)
    }
    val memberItems: LiveData<ArrayList<Member>>
        get() = _memberItems

    private var groupName: String? = null

    private var isNewGroup = false

    private var isDataLoaded = false

    fun addMember() {
        _addMember.value = Event(Unit)
    }

    /*fun start() {
        _dataLoading.value?.let { isLoading ->
            // Already loading, ignore.
            if (isLoading) return
        }
        this.groupName = groupName
        if (groupName == null) {
            // No need to populate, it's a new group
            isNewGroup = true
            return
        }
        if (isDataLoaded) {
            // No need to populate, already have data.
            return
        }
        isNewGroup = false
        _dataLoading.value = true

        groupRepository.getGroup(groupName, this)
    }*/

    private fun memberAdded(name: String, email: String) {
        val newMember = Member(name, email)
        if (memberContainer.contains(newMember.email)) {
            _snackbarText.value = Event(R.string.member_already_added)
            return
        }
        memberContainer[newMember.email] = newMember
        addMemberToList(newMember)
        _snackbarText.value = Event(R.string.member_successful_added)
    }

    fun handleActivityOnResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AddEditGroupActivity.REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val name = data?.getStringExtra(AddMemberActivity.RESULT_MEMBER_NAME)
                    val email = data?.getStringExtra(AddMemberActivity.RESULT_MEMBER_EMAIL)
                    if (name != null && email != null) {
                        memberAdded(name, email)
                    }
                }
            }
        }
    }

    private fun addMemberToList(member: Member) {
        _memberItems.value?.add(member)
        _memberItems.value = _memberItems.value
    }

    fun saveGroup() {
        val currentName = name.value
        if (currentName == null) {
            _snackbarText.value = Event(R.string.no_group_name_message)
            return
        }
        if (memberContainer.isEmpty()) {
            _snackbarText.value = Event(R.string.add_at_least_one_member)
            return
        }
        val group = Group(currentName)
        groupsMembersRepository.createNewGroup(group, memberContainer.values.toList())
        _groupUpdated.value = Event(Unit)
    }

    /*private fun createGroup(group: Group) {
        *//*appExecutors.diskIO.execute {
            groupRepository.saveGroupSync(group)

            memberContainer.forEach { member ->
                membersRepository.saveMemberSync(MemberData(member.name, member.email))
                appExecutors.mainThread.execute {
                    _memberUpdated.value = Event(member)
                }
            }

            memberContainer.forEach { member ->
                groupsMembersRepository.saveGroupMemberSync(GroupMemberData(group.name, member.email))
            }

            appExecutors.mainThread.execute {
                _groupUpdated.value = Event(Unit)
            }
        }*//*
    }*/

    /*private fun createMembers(memberContainer: HashMap<String, Member>) {
        *//*memberContainer.forEach { member ->
            memberRepository.saveMember(member)
            _memberUpdated.value = Event(member)
        }*//*
    }

    private fun addMembersToGroup(groupName: String, memberContainer: HashMap<String, Member>) {
        *//*memberContainer.forEach { member ->
            groupMemberRepository.saveGroupMember(GroupMemberData(groupName, member.email))
        }*//*
    }*/

    fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> {
                val pos = item.order
                _memberItems.value?.get(pos)?.let {
                    memberContainer.remove(it.email)
                }
                _memberItems.value?.removeAt(pos)
                _memberItems.value = _memberItems.value
            }
        }
        return false
    }

    override fun onClick(view: View) {
    }

    companion object {
        private const val TAG = "AddEditGroupViewModel"
    }
}