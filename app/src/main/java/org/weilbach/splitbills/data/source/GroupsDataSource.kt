package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.Group

interface GroupsDataSource {

    interface GetGroupsCallback {
        fun onGroupsLoaded(groups: List<Group>)
        fun onDataNotAvailable()
    }

    interface GetGroupCallback {
        fun onGroupLoaded(group: Group)
        fun onDataNotAvailable()
    }

    interface SaveGroupCallback {
        fun onGroupSaved()
        fun onDataNotAvailable()
    }

    interface DeleteGroupCallback {
        fun onGroupDeleted()
        fun onDataNotAvailable()
    }

    interface DeleteGroupsCallback {
        fun onGroupsDeleted()
        fun onDataNotAvailable()
    }

    fun getGroups(callback: GetGroupsCallback)

    fun getGroup(groupName: String, callback: GetGroupCallback)

    fun saveGroup(group: Group, callback: SaveGroupCallback)

    fun deleteGroup(groupName: String, callback: DeleteGroupCallback)

    fun deleteAllGroups(callback: DeleteGroupsCallback)

    fun refreshGroups()
}