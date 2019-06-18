/*
package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.GroupData

interface GroupsDataSource {

    interface GetGroupsCallback {
        fun onGroupsLoaded(group: List<GroupData>)
        fun onDataNotAvailable()
    }

    interface GetGroupCallback {
        fun onGroupLoaded(group: GroupData)
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

    fun getGroupsSync(): List<GroupData>

    fun getGroup(groupName: String, callback: GetGroupCallback)

    fun saveGroup(group: GroupData, callback: SaveGroupCallback)

    fun saveGroupSync(group: GroupData)

    fun deleteGroup(groupName: String, callback: DeleteGroupCallback)

    fun deleteGroupSync(groupName: String)

    fun deleteAllGroups(callback: DeleteGroupsCallback)

    fun refreshGroups()
}*/
