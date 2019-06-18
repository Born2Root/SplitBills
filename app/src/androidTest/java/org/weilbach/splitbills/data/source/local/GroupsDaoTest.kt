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
 *//*


package org.weilbach.splitbills.data.source.local

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.weilbach.splitbills.data.GroupData
import org.weilbach.splitbills.data.local.SplitBillsDatabase

@RunWith(AndroidJUnit4::class)
class GroupsDaoTest {

    private lateinit var database: SplitBillsDatabase

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getContext(),
                SplitBillsDatabase::class.java).build()
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun insertGroupAndGetById() {
        // When inserting a group
        database.groupsDao().insertGroup(DEFAULT_GROUP)

        // When getting the group by id from the database
        val loaded = database.groupsDao().getGroupByName(DEFAULT_GROUP.name)

        // The loaded data contains the expected values
        assertGroup(loaded, DEFAULT_NAME)
    }

    @Test
    fun insertGroupReplacesOnConflict() {
        // Given that a group is inserted
        database.groupsDao().insertGroup(DEFAULT_GROUP)

        // When a group with the same id is inserted
        val newGroup = GroupData(DEFAULT_NAME)
        database.groupsDao().insertGroup(newGroup)

        // When getting the group by name from the database
        val loaded = database.groupsDao().getGroupByName(DEFAULT_GROUP.name)

        // The loaded data contains the expected values
        assertGroup(loaded, DEFAULT_NAME)
    }

    @Test
    fun insertGroupAndGetGroups() {
        // When inserting a group
        database.groupsDao().insertGroup(DEFAULT_GROUP)

        // When getting the groups from the database
        val groups = database.groupsDao().getGroups()

        // There is only 1 group in the database
        assertThat(groups.size, `is`(1))
        // The loaded data contains the expected values
        assertGroup(groups[0], DEFAULT_NAME)
    }

    @Test
    fun updateGroupAndGetByName() {
        // When inserting a group
        database.groupsDao().insertGroup(DEFAULT_GROUP)

        // When the group is updated
        val updatedGroup = GroupData(DEFAULT_NAME)
        database.groupsDao().updateGroup(updatedGroup)

        // When getting the group by name from the database
        val loaded = database.groupsDao().getGroupByName(DEFAULT_NAME)

        // The loaded data contains the expected values
        assertGroup(loaded, DEFAULT_NAME)
    }

    @Test
    fun deleteGroupByNameAndGettingGroups() {
        // Given a group inserted
        database.groupsDao().insertGroup(DEFAULT_GROUP)

        // When deleting a group by name
        database.groupsDao().deleteGroupByName(DEFAULT_GROUP.name)

        // When getting the groups
        val groups = database.groupsDao().getGroups()

        // The list is empty
        assertThat(groups.size, `is`(0))
    }

    @Test
    fun deleteGroupsAndGettingGroups() {
        // Given a group inserted
        database.groupsDao().insertGroup(DEFAULT_GROUP)

        // When deleting all groups
        database.groupsDao().deleteGroups()

        // When getting the groups
        val groups = database.groupsDao().getGroups()

        // The list is empty
        assertThat(groups.size, `is`(0))
    }


    private fun assertGroup(groupData: GroupData?, name: String) {
        assertThat<GroupData>(groupData as GroupData, notNullValue())
        assertThat(groupData.name, `is`(name))
    }

    companion object {
        private const val DEFAULT_NAME = "id"
        private val DEFAULT_GROUP = GroupData(DEFAULT_NAME)
    }
}*/
