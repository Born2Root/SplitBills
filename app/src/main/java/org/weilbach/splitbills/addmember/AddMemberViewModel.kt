package org.weilbach.splitbills.addmember

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data2.Member

class AddMemberViewModel : ViewModel() {

    val name = MutableLiveData<String>()

    val email = MutableLiveData<String>()

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _saveMemberEvent = MutableLiveData<Event<Member>>()
    val saveMemberDataEvent: LiveData<Event<Member>>
        get() = _saveMemberEvent

    fun saveMember() {
        val currentName = name.value
        if (currentName == null) {
            _snackbarText.value = Event(R.string.no_name_given)
            return
        }
        val currentEmail = email.value
        if (currentEmail == null) {
            _snackbarText.value = Event(R.string.no_email_given)
            return
        }
        createMember(Member(currentName, currentEmail))
    }

    private fun createMember(memberData: Member) {
        _saveMemberEvent.value = Event(memberData)
    }
}