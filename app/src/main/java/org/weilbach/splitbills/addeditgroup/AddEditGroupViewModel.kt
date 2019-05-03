package org.weilbach.splitbills.addeditgroup

import android.app.Activity
import android.content.Intent
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.addmember.AddMemberActivity
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.data.source.GroupsDataSource
import org.weilbach.splitbills.data.source.GroupsMembersRepository
import org.weilbach.splitbills.data.source.GroupsRepository
import org.weilbach.splitbills.data.source.MembersRepository
import org.weilbach.splitbills.util.MemberContainer

class AddEditGroupViewModel(
        private val groupsRepository: GroupsRepository,
        private val membersRepository: MembersRepository,
        private val groupsMembersRepository: GroupsMembersRepository
) : ViewModel(), GroupsDataSource.GetGroupCallback {

    val memberContainer = MemberContainer()

    val name = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() =_dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _groupUpdated = MutableLiveData<Event<Unit>>()
    val groupUpdatedEvent: LiveData<Event<Unit>>
        get() = _groupUpdated

    private val _addMember = MutableLiveData<Event<Unit>>()
    val addMemberEvent: LiveData<Event<Unit>>
        get() = _addMember

    private val _memberUpdated = MutableLiveData<Event<Member>>()
    val memberUpdatedEvent: LiveData<Event<Member>>
        get() = _memberUpdated

    private val _memberItems = MutableLiveData<ArrayList<Member>>().apply {
        value = ArrayList()
    }
    val memberItems: LiveData<ArrayList<Member>>
        get() = _memberItems

    private var groupName: String? = null

    private var isNewGroup = false

    private var isDataLoaded = false

    fun addMember() {
        _addMember.value = Event(Unit)
    }

    override fun onGroupLoaded(group: Group) {
        name.value = group.name
        _dataLoading.value = false
        isDataLoaded = true
    }

    override fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    fun start(groupName: String?) {
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

        groupsRepository.getGroup(groupName, this)
    }

    private fun memberAdded(name: String, email: String) {
        val newMember = Member(name, email)
        if (memberContainer.contains(newMember)) {
            _snackbarText.value = Event(R.string.member_already_added)
            return
        }
        memberContainer.add(newMember)
        addMemberToList(newMember)
        _snackbarText.value = Event(R.string.member_successful_added)
    }

    fun handleActivityOnResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AddEditGroupActivity.REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val name = data?.getStringExtra(AddMemberActivity.RESULT_MEMBER_NAME)
                    val email = data?.getStringExtra(AddMemberActivity.RESULT_MEMBER_EMAIL)
                    memberAdded(name!!, email!!)
                }
            }
        }
    }

    private fun addMemberToList(member: Member) {
        _memberItems.value?.add(member)
    }

    internal fun saveGroup() {
        val currentName = name.value
        if (currentName == null) {
            _snackbarText.value = Event(R.string.no_group_name_message)
            return
        }
        /*if (memberContainer.size == 0) {
            _snackbarText.value = Event(R.string.add_at_least_one_member)
            return
        }*/
        val group = Group(currentName)
        // group.setMemberContainer(memberContainer)
        createGroup(group)
        /*val currentName = name.value

        if (currentName == null) {
            _snackbarText.value =  Event(R.string.empty_group_message)
            return
        }
        if (Group(currentName).isEmpty) {
            _snackbarText.value =  Event(R.string.empty_group_message)
            return
        }

        val currentTaskId = taskId
        if (isNewTask || currentTaskId == null) {
            createTask(Task(currentName, currentDescription))
        } else {
            val task = Task(currentName, currentDescription, currentTaskId)
                    .apply { isCompleted = taskCompleted }
            updateTask(task)
        }*/
    }

    private fun createGroup(group: Group) {
        groupsRepository.saveGroup(group, object : GroupsDataSource.SaveGroupCallback {
            override fun onGroupSaved() {
            }
            override fun onDataNotAvailable() {
            }
        })
        // createMembers(memberContainer)
        // addMembersToGroup(memberContainer)
        _groupUpdated.value = Event(Unit)
    }

    private fun createMembers(memberContainer: MemberContainer) {
        memberContainer.forEach { member ->
            membersRepository.saveMember(member)
            _memberUpdated.value = Event(member)
        }
    }

    private fun addMembersToGroup(memberContainer: MemberContainer) {
    }
}