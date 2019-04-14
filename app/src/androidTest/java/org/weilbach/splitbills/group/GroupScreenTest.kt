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
package org.weilbach.splitbills.group

import android.view.View
import android.widget.ListView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.google.common.base.Preconditions.checkArgument
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.matches
import org.weilbach.splitbills.Injection
import org.weilbach.splitbills.R
import org.weilbach.splitbills.ViewModelFactory
import org.weilbach.splitbills.data.source.GroupsDataSource
import org.weilbach.splitbills.util.EspressoIdlingResource

/**
 * Tests for the groups screen, the main screen which contains a list of all groups.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class GroupScreenTest {

    val NAME1 = "Group name 1"

    @get:Rule
    var groupsActivityTestRule =
            ActivityTestRule<GroupActivity>(GroupActivity::class.java)

    @Before
    fun resetState() {
        ViewModelFactory.destroyInstance()
        Injection.provideGroupsRepository(ApplicationProvider.getApplicationContext())
                .deleteAllGroups(object : GroupsDataSource.DeleteGroupsCallback {
                    override fun onGroupsDeleted() {
                    }
                    override fun onDataNotAvailable() {
                    }
                })
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun clickAddGroupButton_opensAddGroupUi() {
        // Click on the add group button
        onView(withId(R.id.act_group_button_add_group)).perform(click())

        // Check if the add group screen is displayed
        onView(withId(R.id.frag_add_edit_group_button_save)).check(matches(isDisplayed()))
    }

    @Test
    fun addGroupToGroupList() {
        createGroup(NAME1)

        // Verify task is displayed on screen
        onView(withItemText(NAME1)).check(matches(isDisplayed()))
    }

    private fun createGroup(groupName: String) {
        // Click on the add group button
        onView(withId(R.id.act_group_button_add_group)).perform(click())

        // Add group groupName
        onView(withId(R.id.frag_add_edit_group_edit_text_group_name)).perform(
                typeText(groupName),
                closeSoftKeyboard()
        )
        // Save the group
        onView(withId(R.id.frag_add_edit_group_button_save)).perform(click())
    }

    private fun withItemText(itemText: String): Matcher<View> {
        checkArgument(itemText.isNotEmpty(), "itemText cannot be null or empty")
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(item: View) = allOf(
                    isDescendantOfA(isAssignableFrom(ListView::class.java)),
                    withText(itemText)
            ).matches(item)

            override fun describeTo(description: Description) {
                description.appendText("is isDescendantOfA LV with text $itemText")
            }
        }
    }

    /*@Test
    fun editTask() {
        // First add a task
        createGroup(TITLE1, DESCRIPTION)

        // Click on the task on the list
        onView(withText(TITLE1)).perform(click())

        // Click on the edit task button
        onView(withId(R.id.fab_edit_task)).perform(click())

        val editTaskTitle = TITLE2
        val editTaskDescription = "New Description"

        // Edit task title and description
        onView(withId(R.id.add_task_title))
                .perform(replaceText(editTaskTitle), closeSoftKeyboard()) // Type new task title
        onView(withId(R.id.add_task_description)).perform(
                replaceText(editTaskDescription),
                closeSoftKeyboard()
        ) // Type new task description and close the keyboard

        // Save the task
        onView(withId(R.id.fab_edit_task_done)).perform(click())

        // Verify task is displayed on screen in the task list.
        onView(withItemText(editTaskTitle)).check(matches(isDisplayed()))

        // Verify previous task is not displayed
        onView(withItemText(TITLE1)).check(doesNotExist())
    }*//*

    @Test
    fun markTaskAsComplete() {
        viewAllTasks()

        // Add active task
        createGroup(TITLE1, DESCRIPTION)

        // Mark the task as complete
        clickCheckBoxForTask(TITLE1)

        // Verify task is shown as complete
        viewAllTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
        viewActiveTasks()
        onView(withItemText(TITLE1)).check(matches(not(isDisplayed())))
        viewCompletedTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
    }

    @Test
    fun markTaskAsActive() {
        viewAllTasks()

        // Add completed task
        createGroup(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)

        // Mark the task as active
        clickCheckBoxForTask(TITLE1)

        // Verify task is shown as active
        viewAllTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
        viewActiveTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
        viewCompletedTasks()
        onView(withItemText(TITLE1)).check(matches(not(isDisplayed())))
    }

    @Test
    fun showAllTasks() {
        // Add 2 active tasks
        createGroup(TITLE1, DESCRIPTION)
        createGroup(TITLE2, DESCRIPTION)

        //Verify that all our tasks are shown
        viewAllTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
        onView(withItemText(TITLE2)).check(matches(isDisplayed()))
    }

    @Test
    fun showActiveTasks() {
        // Add 2 active tasks
        createGroup(TITLE1, DESCRIPTION)
        createGroup(TITLE2, DESCRIPTION)

        //Verify that all our tasks are shown
        viewActiveTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
        onView(withItemText(TITLE2)).check(matches(isDisplayed()))
    }

    @Test
    fun showCompletedTasks() {
        // Add 2 completed tasks
        createGroup(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)
        createGroup(TITLE2, DESCRIPTION)
        clickCheckBoxForTask(TITLE2)

        // Verify that all our tasks are shown
        viewCompletedTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
        onView(withItemText(TITLE2)).check(matches(isDisplayed()))
    }

    @Test
    fun clearCompletedTasks() {
        viewAllTasks()

        // Add 2 complete tasks
        createGroup(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)
        createGroup(TITLE2, DESCRIPTION)
        clickCheckBoxForTask(TITLE2)

        // Click clear completed in menu
        openActionBarOverflowOrOptionsMenu(getTargetContext())
        onView(withText(string.menu_clear)).perform(click())

        //Verify that completed tasks are not shown
        onView(withItemText(TITLE1)).check(matches(not(isDisplayed())))
        onView(withItemText(TITLE2)).check(matches(not(isDisplayed())))
    }

    @Test
    fun createOneTask_deleteTask() {
        viewAllTasks()

        // Add active task
        createGroup(TITLE1, DESCRIPTION)

        // Open it in details view
        onView(withText(TITLE1)).perform(click())

        // Click delete task in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        viewAllTasks()
        onView(withText(TITLE1)).check(matches(not(isDisplayed())))
    }

    @Test
    fun createTwoTasks_deleteOneTask() {
        // Add 2 active tasks
        createGroup(TITLE1, DESCRIPTION)
        createGroup(TITLE2, DESCRIPTION)

        // Open the second task in details view
        onView(withText(TITLE2)).perform(click())

        // Click delete task in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify only one task was deleted
        viewAllTasks()
        onView(withText(TITLE1)).check(matches(isDisplayed()))
        onView(withText(TITLE2)).check(doesNotExist())
    }

    @Test
    fun markTaskAsCompleteOnDetailScreen_taskIsCompleteInList() {
        viewAllTasks()

        // Add 1 active task
        createGroup(TITLE1, DESCRIPTION)

        // Click on the task on the list
        onView(withText(TITLE1)).perform(click())

        // Click on the checkbox in task details screen
        onView(withId(R.id.task_detail_complete)).perform(click())

        // Click on the navigation up button to go back to the list
        onView(withContentDescription(toolbarNavigationContentDescription)).perform(click())

        // Check that the task is marked as completed
        onView(allOf(withId(R.id.complete), hasSibling(withText(TITLE1))))
                .check(matches(isChecked()))
    }

    @Test
    fun markTaskAsActiveOnDetailScreen_taskIsActiveInList() {
        viewAllTasks()

        // Add 1 completed task
        createGroup(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)

        // Click on the task on the list
        onView(withText(TITLE1)).perform(click())

        // Click on the checkbox in task details screen
        onView(withId(R.id.task_detail_complete)).perform(click())

        // Click on the navigation up button to go back to the list
        onView(withContentDescription(toolbarNavigationContentDescription)).perform(click())

        // Check that the task is marked as active
        onView(allOf(withId(R.id.complete), hasSibling(withText(TITLE1))))
                .check(matches(not(isChecked())))
    }

    @Test
    fun markTaskAsAcompleteAndActiveOnDetailScreen_taskIsActiveInList() {
        viewAllTasks()

        // Add 1 active task
        createGroup(TITLE1, DESCRIPTION)

        // Click on the task on the list
        onView(withText(TITLE1)).perform(click())

        // Click on the checkbox in task details screen
        onView(withId(R.id.task_detail_complete)).perform(click())

        // Click again to restore it to original state
        onView(withId(R.id.task_detail_complete)).perform(click())

        // Click on the navigation up button to go back to the list
        onView(withContentDescription(toolbarNavigationContentDescription)).perform(click())

        // Check that the task is marked as active
        onView(allOf(withId(R.id.complete), hasSibling(withText(TITLE1))))
                .check(matches(not(isChecked())))
    }

    @Test
    fun markTaskAsActiveAndCompleteOnDetailScreen_taskIsCompleteInList() {
        viewAllTasks()

        // Add 1 completed task
        createGroup(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)

        // Click on the task on the list
        onView(withText(TITLE1)).perform(click())

        // Click on the checkbox in task details screen
        onView(withId(R.id.task_detail_complete)).perform(click())

        // Click again to restore it to original state
        onView(withId(R.id.task_detail_complete)).perform(click())

        // Click on the navigation up button to go back to the list
        onView(withContentDescription(toolbarNavigationContentDescription)).perform(click())

        // Check that the task is marked as active
        onView(allOf(withId(R.id.complete), hasSibling(withText(TITLE1))))
                .check(matches(isChecked()))
    }

    @Test
    fun orientationChange_FilterActivePersists() {

        // Add a completed task
        createGroup(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)

        // when switching to active tasks
        viewActiveTasks()

        // then no tasks should appear
        onView(withText(TITLE1)).check(matches(not(isDisplayed())))

        // when rotating the screen
        groupsActivityTestRule.activity.rotateOrientation()

        // then nothing changes
        onView(withText(TITLE1)).check(doesNotExist())
    }

    @Test
    fun orientationChange_FilterCompletedPersists() {

        // Add a completed task
        createGroup(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)

        // when switching to completed tasks
        viewCompletedTasks()

        // the completed task should be displayed
        onView(withText(TITLE1)).check(matches(isDisplayed()))

        // when rotating the screen
        groupsActivityTestRule.activity.rotateOrientation()

        // then nothing changes
        onView(withText(TITLE1)).check(matches(isDisplayed()))
        onView(withText(string.label_completed)).check(matches(isDisplayed()))
    }

    // Blinking cursor after rotation breaks this in API 19
    @Test
    @SdkSuppress(minSdkVersion = 21)
    fun orientationChange_DuringEdit_ChangePersists() {
        // Add a completed task
        createGroup(TITLE1, DESCRIPTION)

        // Open the task in details view
        onView(withText(TITLE1)).perform(click())

        // Click on the edit task button
        onView(withId(R.id.fab_edit_task)).perform(click())

        // Change task title (but don't save)
        onView(withId(R.id.add_task_title))
                .perform(replaceText(TITLE2), closeSoftKeyboard()) // Type new task title

        // Rotate the screen
        currentActivity.rotateOrientation()

        // Verify task title is restored
        onView(withId(R.id.add_task_title)).check(matches(withText(TITLE2)))
    }

    // Blinking cursor after rotation breaks this in API 19
    @Test
    @SdkSuppress(minSdkVersion = 21)
    fun orientationChange_DuringEdit_NoDuplicate() {
        // Add a completed task
        createGroup(TITLE1, DESCRIPTION)

        // Open the task in details view
        onView(withText(TITLE1)).perform(click())

        // Click on the edit task button
        onView(withId(R.id.fab_edit_task)).perform(click())

        // Rotate the screen
        currentActivity.rotateOrientation()

        // Edit task title and description
        onView(withId(R.id.add_task_title))
                .perform(replaceText(TITLE2), closeSoftKeyboard()) // Type new task title
        onView(withId(R.id.add_task_description)).perform(
                replaceText(DESCRIPTION),
                closeSoftKeyboard()
        ) // Type new task description and close the keyboard

        // Save the task
        onView(withId(R.id.fab_edit_task_done)).perform(click())

        // Verify task is displayed on screen in the task list.
        onView(withItemText(TITLE2)).check(matches(isDisplayed()))

        // Verify previous task is not displayed
        onView(withItemText(TITLE1)).check(doesNotExist())
    }

    @Test
    fun noTasks_AllTasksFilter_AddTaskViewVisible() {
        // Given an empty list of tasks, make sure "All tasks" filter is on
        viewAllTasks()

        // Add task View should be displayed
        onView(withId(R.id.noTasksAdd)).check(matches(isDisplayed()))
    }

    @Test
    fun noTasks_CompletedTasksFilter_AddTaskViewNotVisible() {
        // Given an empty list of tasks, make sure "All tasks" filter is on
        viewCompletedTasks()

        // Add task View should be displayed
        onView(withId(R.id.noTasksAdd)).check(matches(not(isDisplayed())))
    }

    */
    /*

    private fun getText(stringId: Int) =
            groupsActivityTestRule.activity.resources.getString(stringId)*/
}