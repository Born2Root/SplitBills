package org.weilbach.splitbills.data.source.local

import androidx.room.Room
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.GroupMember
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.data.local.GroupsMembersLocalDataSource
import org.weilbach.splitbills.data.local.SplitBillsDatabase
import org.weilbach.splitbills.data.source.GroupsMembersDataSource
import org.weilbach.splitbills.utils.SingleExecutors

@RunWith(AndroidJUnit4::class)
@LargeTest
class GroupsMembersLocalDataSourceTest {

    private lateinit var localDataSource: GroupsMembersDataSource
    private lateinit var database: SplitBillsDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context,
                SplitBillsDatabase::class.java)
                .build()

        GroupsMembersLocalDataSource.clearInstance()
        localDataSource = GroupsMembersLocalDataSource.getInstance(SingleExecutors(),
                database.groupsMembersDao())
    }

    @After
    fun cleanUp() {
        database.close()
        GroupsMembersLocalDataSource.clearInstance()
    }

    @Test
    fun testPreConditions() {
        assertNotNull(localDataSource)
    }

    @Test
    fun insertGroupsMembers_retrievesSavedGroupsMembers() {
        val newGroup1 = Group(GROUP_NAME1)
        val newGroup2 = Group(GROUP_NAME2)
        val newMember1 = Member(MEMBER_NAME1, MEMBER_EMAIL1)
        val newMember2 = Member(MEMBER_NAME2, MEMBER_EMAIL2)
        val newGroupMember1 = GroupMember(GROUP_NAME1, MEMBER_EMAIL1)
        val newGroupMember2 = GroupMember(GROUP_NAME2, MEMBER_EMAIL2)

        database.groupsDao().insertGroup(newGroup1)
        database.groupsDao().insertGroup(newGroup2)
        database.memberDao().insertMember(newMember1)
        database.memberDao().insertMember(newMember2)

        with(localDataSource) {
            saveGroupMember(newGroupMember1)
            saveGroupMember(newGroupMember2)
        }
        var newGroupMember1Found = false
        var newGroupMember2Found = false
        val inserted = database.groupsMembersDao().getGroupsMembers()
        assertTrue(inserted.size >= 2)
        for (groupMember in inserted) {
            if (groupMember.groupName == newGroupMember1.groupName
                    && groupMember.memberEmail == newGroupMember1.memberEmail) {
                newGroupMember1Found = true
            }
            if (groupMember.groupName == newGroupMember2.groupName
                    && groupMember.memberEmail == newGroupMember2.memberEmail) {
                newGroupMember2Found = true
            }
        }
        assertTrue(newGroupMember1Found)
        assertTrue(newGroupMember2Found)
    }

    companion object {
        private const val GROUP_NAME1 = "name"
        private const val GROUP_NAME2 = "name2"
        private const val MEMBER_NAME1 = "memname1"
        private const val MEMBER_NAME2 = "memname2"
        private const val MEMBER_EMAIL1 = "email"
        private const val MEMBER_EMAIL2 = "email2"
    }

}