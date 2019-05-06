package org.weilbach.splitbills.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.source.GroupsDataSource
import org.weilbach.splitbills.data.source.GroupsRepository

class GroupViewModel(private val groupsRepository: GroupsRepository) : ViewModel() {

    private val _items = MutableLiveData<List<Group>>().apply { value = emptyList() }
    val items: LiveData<List<Group>>
        get() = _items

    private val _openGroupEvent = MutableLiveData<Event<String>>()
    val openGroupEvent: LiveData<Event<String>>
        get() = _openGroupEvent

    private val _newGroupEvent = MutableLiveData<Event<Unit>>()
    val newGroupEvent: LiveData<Event<Unit>>
        get() = _newGroupEvent

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _noGroupsLabel = MutableLiveData<Int>()
    val noGroupsLabel: LiveData<Int>
        get() = _noGroupsLabel

    private val _noGroupsIconRes = MutableLiveData<Int>()
    val noGroupsIconRes: LiveData<Int>
        get() = _noGroupsIconRes

    private val _groupsAddViewVisible = MutableLiveData<Boolean>()
    val groupsAddViewVisible: LiveData<Boolean>
        get() = _groupsAddViewVisible

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    // Not used at the moment
    private val isDataLoadingError = MutableLiveData<Boolean>()

    init {
        _dataLoading.value = false
        _noGroupsLabel.value = R.string.no_groups_all
        _groupsAddViewVisible.value = true
        _noGroupsIconRes.value = R.drawable.ic_assignment_turned_in_24dp
    }

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    fun start() {
        loadGroups(false)
    }

    fun loadGroups(forceUpdate: Boolean) {
        loadGroups(forceUpdate, true)
    }

    fun addNewGroup() {
        _newGroupEvent.value = Event(Unit)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {
    }

    private fun loadGroups(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            _dataLoading.value = true
        }
        if (forceUpdate) {

            groupsRepository.refreshGroups()
        }

        groupsRepository.getGroups(object : GroupsDataSource.GetGroupsCallback {
            override fun onGroupsLoaded(groups: List<Group>) {
                val groupsToShow = ArrayList<Group>()

                for (group in groups) {
                    groupsToShow.add(group)
                }
                if (showLoadingUI) {
                    _dataLoading.value = false
                }
                isDataLoadingError.value = false

                val itemsValue = ArrayList(groupsToShow)
                _items.value = itemsValue
            }

            override fun onDataNotAvailable() {
                if (showLoadingUI) {
                    _dataLoading.value = false
                }
                isDataLoadingError.value = true
            }
        })
    }


    internal fun openGroup(groupName: String) {
        _openGroupEvent.value = Event(groupName)
    }
}