package org.weilbach.splitbills.group

/**
 * Unit tests for the implementation of [GroupsViewModel]
 */
class GroupsViewModelTests {

    // Executes each task synchronously using Architecture Components.
    /*@get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @Mock
    private lateinit var groupRepository: GroupsRepository
    @Mock
    private lateinit var context: Application
    @Captor
    private lateinit var loadGroupsCallbackCaptor: ArgumentCaptor<GetGroupsCallback>
    private lateinit var groupsViewModel: GroupViewModel
    private lateinit var group: List<GroupData>

    @Before
    fun setupTasksViewModel() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        setupContext()

        // Get a reference to the class under test
        groupsViewModel = GroupViewModel(groupRepository)

        // We initialise the group to 3, with one active and two completed
        val group1 = GroupData("Title1")
        val group2 = GroupData("Title2")
        val group3 = GroupData("Title3")

        group = Lists.newArrayList(group1, group2, group3)

    }

    private fun setupContext() {
        `when`<Context>(context.applicationContext).thenReturn(context)
        `when`(context.getString(R.string.successfully_saved_group_message))
                .thenReturn("EDIT_RESULT_OK")
        `when`(context.getString(R.string.successfully_added_group_message))
                .thenReturn("ADD_EDIT_RESULT_OK")
        `when`(context.getString(R.string.successfully_deleted_group_message))
                .thenReturn("DELETE_RESULT_OK")

        `when`(context.resources).thenReturn(mock(Resources::class.java))
    }

    @Test
    fun loadAllGroupsFromRepository_dataLoaded() {
        // Given an initialized TasksViewModel with initialized group
        // When loading of Tasks is requested
        groupsViewModel.loadGroups(true)

        // Callback is captured and invoked with stubbed group
        verify<GroupsRepository>(groupRepository).getGroups(capture(loadGroupsCallbackCaptor))

        // Then progress indicator is shown
        assertTrue(LiveDataTestUtil.getValue(groupsViewModel.dataLoading))
        loadGroupsCallbackCaptor.value.onGroupsLoaded(group)

        // Then progress indicator is hidden
        assertFalse(LiveDataTestUtil.getValue(groupsViewModel.dataLoading))

        // And data loaded
        assertFalse(LiveDataTestUtil.getValue(groupsViewModel.items).isEmpty())
        assertTrue(LiveDataTestUtil.getValue(groupsViewModel.items).size == 3)
    }

    *//*@Test
    @Throws(InterruptedException::class)
    fun clickOnFab_ShowsAddGroupUi() {
        // When adding a new task
        groupsViewModel.addNewTask()

        // Then the event is triggered
        val value = LiveDataTestUtil.getValue(groupsViewModel.newTaskEvent)
        assertNotNull(value.getContentIfNotHandled())
    }*//*

    *//*@Test
    @Throws(InterruptedException::class)
    fun handleActivityResult_editOK() {
        // When TaskDetailActivity sends a EDIT_RESULT_OK
        groupsViewModel.handleActivityResult(
                AddEditTaskActivity.REQUEST_CODE, EDIT_RESULT_OK
        )

        // Then the event is triggered
        val value = LiveDataTestUtil.getValue(groupsViewModel.snackbarMessage)
        assertEquals(
                value.getContentIfNotHandled(),
                R.string.successfully_saved_task_message
        )
    }*//*

    *//*@Test
    @Throws(InterruptedException::class)
    fun handleActivityResult_addEditOK() {
        // When TaskDetailActivity sends an EDIT_RESULT_OK
        groupsViewModel.handleActivityResult(
                AddEditTaskActivity.REQUEST_CODE, ADD_EDIT_RESULT_OK
        )

        // Then the snackbar shows the correct message
        val value = LiveDataTestUtil.getValue(groupsViewModel.snackbarMessage)
        assertEquals(
                value.getContentIfNotHandled(),
                R.string.successfully_added_task_message
        )
    }*//*

    *//*@Test
    @Throws(InterruptedException::class)
    fun handleActivityResult_deleteOk() {
        // When TaskDetailActivity sends a DELETE_RESULT_OK
        groupsViewModel.handleActivityResult(
                AddEditTaskActivity.REQUEST_CODE, DELETE_RESULT_OK
        )

        // Then the snackbar shows the correct message
        val value = LiveDataTestUtil.getValue(groupsViewModel.snackbarMessage)
        assertEquals(
                value.getContentIfNotHandled(),
                R.string.successfully_deleted_task_message
        )
    }*//*

    *//*@Test
    @Throws(InterruptedException::class)
    fun getTasksAddViewVisible() {
        // When the filter type is ALL_TASKS
        groupsViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" action is visible
        assertTrue(LiveDataTestUtil.getValue(groupsViewModel.tasksAddViewVisible))
    }*/
}