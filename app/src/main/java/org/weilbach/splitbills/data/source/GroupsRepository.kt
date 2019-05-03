package org.weilbach.splitbills.data.source

import android.util.Log
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.util.EspressoIdlingResource

class GroupsRepository(
        val groupsLocalDataSource: GroupsDataSource
) : GroupsDataSource {

    /**
     * This variable has public visibility so it can be accessed from tests.
     */
    var cachedGroups: LinkedHashMap<String, Group> = LinkedHashMap()

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    var cacheIsDirty = false

    override fun getGroups(callback: GroupsDataSource.GetGroupsCallback) {
        // Respond immediately with cache if available and not dirty
        if (cachedGroups.isNotEmpty() && !cacheIsDirty) {
            callback.onGroupsLoaded(ArrayList(cachedGroups.values))
            return
        }

        EspressoIdlingResource.increment() // Set app as busy.

        // Query the local storage if available.
        groupsLocalDataSource.getGroups(object : GroupsDataSource.GetGroupsCallback {
            override fun onGroupsLoaded(groups: List<Group>) {
                refreshCache(groups)
                EspressoIdlingResource.decrement() // Set app as idle.
                callback.onGroupsLoaded(ArrayList(cachedGroups.values))
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement() // Set app as idle.
                callback.onDataNotAvailable()
            }
        })
    }

    override fun saveGroup(group: Group, callback: GroupsDataSource.SaveGroupCallback) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(group) {
            groupsLocalDataSource.saveGroup(it, callback)
        }
    }

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     *
     *
     * Note: [GetTaskCallback.onDataNotAvailable] is fired if both data sources fail to
     * get the data.
     */
    override fun getGroup(groupName: String, callback: GroupsDataSource.GetGroupCallback) {
        val groupInCache = getGroupWithId(groupName)

        // Respond immediately with cache if available
        if (groupInCache != null) {
            callback.onGroupLoaded(groupInCache)
            return
        }

        EspressoIdlingResource.increment() // Set app as busy.

        // Load from server/persisted if needed.

        // Is the task in the local data source?
        groupsLocalDataSource.getGroup(groupName, object : GroupsDataSource.GetGroupCallback {
            override fun onGroupLoaded(group: Group) {
                // Do in memory cache update to keep the app UI up to date
                cacheAndPerform(group) {
                    EspressoIdlingResource.decrement() // Set app as idle.
                    callback.onGroupLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement() // Set app as idle.
                callback.onDataNotAvailable()
            }
        })
    }

    override fun refreshGroups() {
        cacheIsDirty = true
    }

    override fun deleteAllGroups(callback: GroupsDataSource.DeleteGroupsCallback) {
        groupsLocalDataSource.deleteAllGroups(callback)
        cachedGroups.clear()
    }

    override fun deleteGroup(groupName: String, callback: GroupsDataSource.DeleteGroupCallback) {
        groupsLocalDataSource.deleteGroup(groupName, callback)
        cachedGroups.remove(groupName)
    }

    private fun refreshCache(groups: List<Group>) {
        cachedGroups.clear()
        groups.forEach {
            cacheAndPerform(it) {}
        }
        cacheIsDirty = false
    }

    private inline fun cacheAndPerform(group: Group, perform: (Group) -> Unit) {
        val cachedGroup = Group(group.name)
        cachedGroups[cachedGroup.name] = cachedGroup
        perform(cachedGroup)
    }

    private fun getGroupWithId(id: String) = cachedGroups[id]

    companion object {

        private var INSTANCE: GroupsRepository? = null

        @JvmStatic fun getInstance(groupsLocalDataSource: GroupsDataSource) =
                INSTANCE ?: synchronized(GroupsRepository::class.java) {
                    INSTANCE ?: GroupsRepository(groupsLocalDataSource)
                            .also { INSTANCE = it }
                }

        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }
}