package org.weilbach.splitbills.data.source

import com.google.common.collect.Lists
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.util.any
import org.weilbach.splitbills.util.capture
import org.weilbach.splitbills.util.eq


/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
class GroupsRepositoryTest {

    private val GROUP_TITLE = "title"
    private val GROUP_TITLE2 = "title2"
    private val GROUP_TITLE3 = "title3"
    private val GROUP = Lists.newArrayList(Group("Title1"), Group("Title2"))
    private lateinit var groupsRepository: GroupsRepository

    @Mock
    private lateinit var groupsLocalDataSource: GroupsDataSource
    @Mock
    private lateinit var getGroupCallback: GroupsDataSource.GetGroupCallback
    @Mock
    private lateinit var loadGroupsCallback: GroupsDataSource.GetGroupsCallback
    @Mock
    private lateinit var saveGroupCallback: GroupsDataSource.SaveGroupCallback
    @Mock
    private lateinit var deleteGroupCallback: GroupsDataSource.DeleteGroupCallback
    @Mock
    private lateinit var deleteGroupsCallback: GroupsDataSource.DeleteGroupsCallback

    @Captor
    private lateinit var getGroupsCallbackCaptor:
            ArgumentCaptor<GroupsDataSource.GetGroupsCallback>
    @Captor
    private lateinit var getGroupCallbackCaptor: ArgumentCaptor<GroupsDataSource.GetGroupCallback>

    @Before
    fun setupGroupsRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        groupsRepository = GroupsRepository.getInstance(groupsLocalDataSource)
    }

    @After
    fun destroyRepositoryInstance() {
        GroupsRepository.destroyInstance()
    }

    @Test
    fun getGroups_repositoryCachesAfterFirstApiCall() {
        // Given a setup Captor to capture callbacks
        // When two calls are issued to the groups repository
        twoGroupsLoadCallsToRepository(loadGroupsCallback)

        // Then tasks were only requested once from Service API
        verify<GroupsDataSource>(groupsLocalDataSource).getGroups(any())
    }

    @Test
    fun getGroups_requestsAllGroupsFromLocalDataSource() {
        // When groups are requested from the tasks repository
        groupsRepository.getGroups(loadGroupsCallback)

        // Then groups are loaded from the local data source
        verify<GroupsDataSource>(groupsLocalDataSource).getGroups(
                any<GroupsDataSource.GetGroupsCallback>())
    }

    @Test
    fun saveGroup_savesGroupToServiceAPI() {
        // Given a stub group with title and description
        val newGroup = Group(GROUP_TITLE)

        // When a group is saved to the tasks repository
        groupsRepository.saveGroup(newGroup, saveGroupCallback)

        // Then the service API and persistent repository are called and the cache is updated
        verify<GroupsDataSource>(groupsLocalDataSource).saveGroup(eq(newGroup), any())
        assertThat(groupsRepository.cachedGroups.size, `is`(1))
    }

    @Test
    fun getGroup_requestsSingleGroupFromLocalDataSource() {
        // When a group is requested from the group repository
        groupsRepository.getGroup(GROUP_TITLE, getGroupCallback)

        // Then the task is loaded from the database
        verify(groupsLocalDataSource)
                .getGroup(eq(GROUP_TITLE), any<GroupsDataSource.GetGroupCallback>())
    }

    @Test
    fun deleteAllGroups_deleteGroupsToServiceAPIUpdatesCache() {
        with(groupsRepository) {
            val newGroup = Group(GROUP_TITLE)
            saveGroup(newGroup, saveGroupCallback)
            val newGroup2 = Group(GROUP_TITLE2)
            saveGroup(newGroup2, saveGroupCallback)
            val newGroup3 = Group(GROUP_TITLE3)
            saveGroup(newGroup3, saveGroupCallback)

            // When all groups are deleted to the tasks repository
            deleteAllGroups(deleteGroupsCallback)

            // Verify the data sources were called
            verify(this@GroupsRepositoryTest.groupsLocalDataSource)
                    .deleteAllGroups(deleteGroupsCallback)

            assertThat(cachedGroups.size, `is`(0))
        }
    }

    @Test
    fun deleteGroup_deleteGroupToServiceAPIRemovedFromCache() {
        with(groupsRepository) {
            // Given a task in the repository
            val newGroup = Group(GROUP_TITLE)
            saveGroup(newGroup, saveGroupCallback)
            assertThat(cachedGroups.containsKey(newGroup.name), `is`(true))

            // When deleted
            deleteGroup(newGroup.name, deleteGroupCallback)

            // Verify the data sources were called
            verify(this@GroupsRepositoryTest.groupsLocalDataSource)
                    .deleteGroup(newGroup.name, deleteGroupCallback)

            // Verify it's removed from repository
            assertThat(cachedGroups.containsKey(newGroup.name), `is`(false))
        }
    }

    @Test
    fun getGroupsWithDirtyCache_GroupsAreRetrievedFromLocal() {
        with(groupsRepository) {
            // When calling getGroups in the repository with dirty cache
            refreshGroups()
            getGroups(loadGroupsCallback)
        }

        // And the remote data source has data available
        setGroupsAvailable(groupsLocalDataSource, GROUP)

        // Verify the groups from the remote data source are returned, not the local
        verify(loadGroupsCallback).onGroupsLoaded(GROUP)
    }

    @Test
    fun getGroupsWithDataSourceUnavailable_firesOnDataUnavailable() {
        // When calling getTasks in the repository
        groupsRepository.getGroups(loadGroupsCallback)

        // And the local data source has no data available
        setGroupsNotAvailable(groupsLocalDataSource)

        // Verify no data is returned
        verify(loadGroupsCallback).onDataNotAvailable()
    }

    @Test
    fun getGroupWithDataSourceUnavailable_firesOnDataUnavailable() {
        // Given a task id
        val groupId = "123"

        // When calling getGroup in the repository
        groupsRepository.getGroup(groupId, getGroupCallback)

        // And the local data source has no data available
        setGroupNotAvailable(groupsLocalDataSource, groupId)

        // Verify no data is returned
        verify(getGroupCallback).onDataNotAvailable()
    }

    /*@Test
    fun getGroups_refreshesLocalDataSource() {
        with(groupsRepository) {
            // Mark cache as dirty to force a reload of data from remote data source.
            refreshGroups()

            // When calling getTasks in the repository
            getGroups(loadGroupsCallback)
        }

        // Make the remote data source return data
        setGroupsAvailable(groupsLocalDataSource, GROUP)

        // Verify that the data fetched from the remote data source was saved in local.
        verify(groupsLocalDataSource, times(GROUP.size))
                .saveGroup(any<Group>(), any<GroupsDataSource.SaveGroupCallback>())
    }*/

    /**
     * Convenience method that issues two calls to the groups repository
     */
    private fun twoGroupsLoadCallsToRepository(callback: GroupsDataSource.GetGroupsCallback) {
        // When tasks are requested from repository
        groupsRepository.getGroups(callback) // First call to API

        // Use the Mockito Captor to capture the callback
        verify(groupsLocalDataSource).getGroups(capture(getGroupsCallbackCaptor))

        // Trigger callback so tasks are cached
        getGroupsCallbackCaptor.value.onGroupsLoaded(GROUP)

        groupsRepository.getGroups(callback) // Second call to API
    }

    private fun setGroupsNotAvailable(dataSource: GroupsDataSource) {
        verify(dataSource).getGroups(capture(getGroupsCallbackCaptor))
        getGroupsCallbackCaptor.value.onDataNotAvailable()
    }

    private fun setGroupsAvailable(dataSource: GroupsDataSource, groups: List<Group>) {
        verify(dataSource).getGroups(capture(getGroupsCallbackCaptor))
        getGroupsCallbackCaptor.value.onGroupsLoaded(groups)
    }

    private fun setGroupNotAvailable(dataSource: GroupsDataSource, groupName: String) {
        verify(dataSource).getGroup(eq(groupName), capture(getGroupCallbackCaptor))
        getGroupCallbackCaptor.value.onDataNotAvailable()
    }

    private fun setGroupAvailable(dataSource: GroupsDataSource, group: Group) {
        verify(dataSource).getGroup(eq(group.name), capture(getGroupCallbackCaptor))
        getGroupCallbackCaptor.value.onGroupLoaded(group)
    }
}