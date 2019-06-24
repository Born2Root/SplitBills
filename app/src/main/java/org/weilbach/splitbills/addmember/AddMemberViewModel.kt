package org.weilbach.splitbills.addmember

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.util.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Member

class AddMemberViewModel(
        private val appContext: Context
) : ViewModel() {

    val name = MutableLiveData<String>()

    private val _nameError = MutableLiveData<String>()
    val nameError: LiveData<String>
        get() = _nameError

    val email = MutableLiveData<String>()

    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String>
        get() = _emailError

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _saveMemberEvent = MutableLiveData<Event<Member>>()
    val saveMemberDataEvent: LiveData<Event<Member>>
        get() = _saveMemberEvent

    fun saveMember() {
        val currentName = name.value
        if (currentName == null) {
            _nameError.value = appContext.getString(R.string.enter_name)
            return
        }
        val currentEmail = email.value
        if (currentEmail == null) {
            _emailError.value = appContext.getString(R.string.enter_email)
            return
        }
        createMember(Member(currentName, currentEmail))
    }

    private fun createMember(memberData: Member) {
        _saveMemberEvent.value = Event(memberData)
    }
}