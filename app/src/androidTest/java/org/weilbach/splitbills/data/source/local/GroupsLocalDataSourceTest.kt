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
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.weilbach.splitbills.data.GroupData
import org.weilbach.splitbills.data.local.GroupsLocalDataSource
import org.weilbach.splitbills.data.local.SplitBillsDatabase
import org.weilbach.splitbills.data.source.GroupsDataSource
import org.weilbach.splitbills.util.any
import org.weilbach.splitbills.utils.SingleExecutors


*/
/**
 * Integration test for the [GroupsDataSource].
 *//*

@RunWith(AndroidJUnit4::class)
@LargeTest
class GroupsLocalDataSourceTest {
    private val TITLE = "title"
    private val TITLE2 = "title2"
    private val TITLE3 = "title3"
    private lateinit var localDataSource: GroupsLocalDataSource
    private lateinit var database: SplitBillsDatabase

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                SplitBillsDatabase::class.java)
                .build()

        // Make sure that we're not keeping a reference to the wrong instance.
        GroupsLocalDataSource.clearInstance()
        localDataSource = GroupsLocalDataSource.getInstance(SingleExecutors(), database.groupsDao())
    }

    @After
    fun cleanUp() {
        database.close()
        GroupsLocalDataSource.clearInstance()
    }

    @Test
    fun testPreConditions() {
        assertNotNull(localDataSource)
    }

    @Test
    fun saveGroup_retrievesGroup() {
        // Given a new group
        val newGroup = GroupData(TITLE)

        with(localDataSource) {
            // When saved into the persistent repository
            saveGroup(newGroup, object : GroupsDataSource.SaveGroupCallback {
                override fun onGroupSaved() {
                }

                override fun onDataNotAvailable() {
                }
            })

            // Then the task can be retrieved from the persistent repository
            getGroup(newGroup.name, object : GroupsDataSource.GetGroupCallback {
                override fun onGroupLoaded(groupData: GroupData) {
                    assertThat(groupData, `is`(newGroup))
                }

                override fun onDataNotAvailable() {
                    fail("Callback error")
                }
            })
        }
    }

    @Test
    fun deleteAllGroups_emptyListOfRetrievedGroups() {
        val callback = mock(GroupsDataSource.GetGroupsCallback::class.java)

        // Given a new group in the persistent repository and a mocked callback
        val newGroup = GroupData(TITLE)

        with(localDataSource) {
            saveGroup(newGroup, object : GroupsDataSource.SaveGroupCallback {
                override fun onGroupSaved() {
                }

                override fun onDataNotAvailable() {
                }
            })

            // When all groups are deleted
            deleteAllGroups(object : GroupsDataSource.DeleteGroupsCallback {
                override fun onGroupsDeleted() {
                }

                override fun onDataNotAvailable() {
                }
            })

            // Then the retrieved groups is an empty list
            getGroups(callback)
        }
        verify<GroupsDataSource.GetGroupsCallback>(callback).onDataNotAvailable()
        verify<GroupsDataSource.GetGroupsCallback>(callback, never())
                .onGroupsLoaded(any<List<GroupData>>())
    }

    @Test
    fun getGroups_retrieveSavedGroups() {
        // Given 2 new groups in the persistent repository
        val newGroup1 = GroupData(TITLE)
        val newGroup2 = GroupData(TITLE2)

        with(localDataSource) {
            saveGroup(newGroup1, object : GroupsDataSource.SaveGroupCallback {
                override fun onGroupSaved() {
                }

                override fun onDataNotAvailable() {
                }
            })
            saveGroup(newGroup2, object : GroupsDataSource.SaveGroupCallback {
                override fun onGroupSaved() {
                }

                override fun onDataNotAvailable() {
                }
            })
            // Then the groups can be retrieved from the persistent repository
            getGroups(object : GroupsDataSource.GetGroupsCallback {
                override fun onGroupsLoaded(groupData: List<GroupData>) {
                    assertNotNull(groupData)
                    assertTrue(groupData.size >= 2)

                    var newGroup1IdFound = false
                    var newGroup2IdFound = false
                    for ((name) in groupData) {
                        if (name == newGroup1.name) {
                            newGroup1IdFound = true
                        }
                        if (name == newGroup2.name) {
                            newGroup2IdFound = true
                        }
                    }
                    assertTrue(newGroup1IdFound)
                    assertTrue(newGroup2IdFound)
                }

                override fun onDataNotAvailable() {
                    fail()
                }
            })
        }
    }
}
*/
