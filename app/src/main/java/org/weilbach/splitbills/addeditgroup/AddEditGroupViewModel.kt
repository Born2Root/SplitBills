package org.weilbach.splitbills.addeditgroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.source.GroupsDataSource
import org.weilbach.splitbills.data.source.GroupsRepository

class AddEditGroupViewModel(
        private val groupsRepository: GroupsRepository
) : ViewModel(), GroupsDataSource.GetGroupCallback {

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

    private val _addMemberEvent = MutableLiveData<Event<Unit>>()
    val addMemberEvent: LiveData<Event<Unit>>
        get() = _addMemberEvent

    private var groupName: String? = null

    private var isNewGroup = false

    private var isDataLoaded = false

    fun addMember() {
        _addMemberEvent.value = Event(Unit)
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

    internal fun saveTask() {
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
}