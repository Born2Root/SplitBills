package org.weilbach.splitbills.data.remote

import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.source.GroupsDataSource

object GroupsRemoteDataSource : GroupsDataSource {
    override fun refreshGroups() {
    }

    override fun getGroups(callback: GroupsDataSource.GetGroupsCallback) {
        callback.onDataNotAvailable()
    }

    override fun getGroup(groupName: String, callback: GroupsDataSource.GetGroupCallback) {
        callback.onDataNotAvailable()
    }

    override fun saveGroup(group: Group, callback: GroupsDataSource.SaveGroupCallback) {
        callback.onDataNotAvailable()
    }

    override fun deleteGroup(groupName: String, callback: GroupsDataSource.DeleteGroupCallback) {
        callback.onDataNotAvailable()
    }

    override fun deleteAllGroups(callback: GroupsDataSource.DeleteGroupsCallback) {
        callback.onDataNotAvailable()
    }
}