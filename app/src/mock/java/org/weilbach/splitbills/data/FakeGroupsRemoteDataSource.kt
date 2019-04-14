/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.weilbach.splitbills.data

import androidx.annotation.VisibleForTesting

import com.google.common.collect.Lists
import org.weilbach.splitbills.data.source.GroupsDataSource
import java.util.LinkedHashMap

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
object FakeGroupsRemoteDataSource : GroupsDataSource {

    private var GROUPS_SERVICE_DATA: LinkedHashMap<String, Group> = LinkedHashMap()

    override fun getGroups(callback: GroupsDataSource.GetGroupsCallback) {
        callback.onGroupsLoaded(Lists.newArrayList(GROUPS_SERVICE_DATA.values))
    }

    override fun getGroup(groupName: String, callback: GroupsDataSource.GetGroupCallback) {
        val task = GROUPS_SERVICE_DATA[groupName]
        task?.let { callback.onGroupLoaded(it) }
    }

    override fun saveGroup(group: Group, callback: GroupsDataSource.SaveGroupCallback) {
        GROUPS_SERVICE_DATA.put(group.name, group)
        callback.onGroupSaved()
    }

    override fun refreshGroups() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteGroup(groupName: String, callback: GroupsDataSource.DeleteGroupCallback) {
        GROUPS_SERVICE_DATA.remove(groupName)
        callback.onGroupDeleted()
    }

    override fun deleteAllGroups(callback: GroupsDataSource.DeleteGroupsCallback) {
        GROUPS_SERVICE_DATA.clear()
        callback.onGroupsDeleted()
    }

    @VisibleForTesting
    fun addTasks(vararg groups: Group) {
        for (group in groups) {
            GROUPS_SERVICE_DATA.put(group.name, group)
        }
    }
}
