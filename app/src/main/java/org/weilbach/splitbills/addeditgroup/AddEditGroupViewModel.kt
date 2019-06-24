package org.weilbach.splitbills.addeditgroup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.util.Event
import org.weilbach.splitbills.MemberItemNavigator
import org.weilbach.splitbills.R
import org.weilbach.splitbills.addmember.AddMemberActivity
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.data.source.GroupMemberRepository
import org.weilbach.splitbills.data.source.GroupRepository
import org.weilbach.splitbills.data.source.MemberRepository
import org.weilbach.splitbills.util.AppExecutors
import org.weilbach.splitbills.util.getUser

class AddEditGroupViewModel(
        private val groupsRepository: GroupRepository,
        private val membersRepository: MemberRepository,
        private val groupsMembersRepository: GroupMemberRepository,
        private val appExecutors: AppExecutors,
        private val appContext: Context
) : ViewModel(), MemberItemNavigator {

    private val memberContainer = HashMap<String, Member>()

    val name = MutableLiveData<String>()

    private val _nameError = MutableLiveData<String>()
    val nameError: LiveData<String>
        get() = _nameError

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

    private val _memberItems = MutableLiveData<ArrayList<Member>>().apply {
        val user = getUser(appContext)
        memberContainer[user.email] = user
        value = arrayListOf(user)
    }
    val memberItems: LiveData<ArrayList<Member>>
        get() = _memberItems

    fun addMember() {
        _addMember.value = Event(Unit)
    }

    private fun memberAdded(name: String, email: String) {
        val newMember = Member(name, email)
        if (memberContainer.contains(newMember.email)) {
            _snackbarText.value = Event(R.string.member_already_added)
            return
        }
        memberContainer[newMember.email] = newMember
        addMemberToList(newMember)
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

    private fun saveGroup() {
        val currentName = name.value
        if (currentName == null) {
            _nameError.value = appContext.getString(R.string.no_group_name_message)
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

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_save_group -> {
                saveGroup()
                false
            }
            else -> return false
        }
    }

    override fun onClick(view: View) {
    }

    companion object {
        private const val TAG = "AddEditGroupViewModel"
    }
}