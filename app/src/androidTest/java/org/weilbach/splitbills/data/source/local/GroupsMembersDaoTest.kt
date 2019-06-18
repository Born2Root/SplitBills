/*
package org.weilbach.splitbills.data.source.local

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.weilbach.splitbills.data.GroupData
import org.weilbach.splitbills.data.GroupMemberData
import org.weilbach.splitbills.data.MemberData
import org.weilbach.splitbills.data.local.SplitBillsDatabase

@RunWith(AndroidJUnit4::class)
class GroupsMembersDaoTest {

    private lateinit var database: SplitBillsDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context,
                SplitBillsDatabase::class.java).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertGroupMemberAndGetGroupsMembers() {
        database.groupsDao().insertGroup(DEFAULT_GROUP)
        database.memberDao().insertMember(DEFAULT_MEMBER)
        database.groupsMembersDao().insertGroupMember(DEFAULT_GROUP_MEMBER)

        val loaded = database.groupsMembersDao().getGroupsMembers()

        assertThat(loaded.size, `is`(1))
        assertGroupMember(loaded[0], DEFAULT_GROUP_NAME, DEFAULT_MEMBER_EMAIL)
    }

    @Test
    fun deleteGroupsMembersAndGettingGroupsMembers() {
        database.groupsMembersDao().insertGroupMember(DEFAULT_GROUP_MEMBER)

        database.groupsMembersDao().deleteAllGroupsMembers()

        val groups = database.groupsMembersDao().getGroupsMembers()

        assertThat(groups.size, `is`(0))
    }

    private fun assertGroupMember(groupMemberData: GroupMemberData?, groupName: String, memberEmail: String) {
        assertThat<GroupMemberData>(groupMemberData as GroupMemberData, notNullValue())
        assertThat(groupMemberData.groupName, `is`(groupName))
        assertThat(groupMemberData.memberEmail, `is`(memberEmail))
    }

    companion object {
        private const val DEFAULT_GROUP_NAME = "group"
        private const val DEFAULT_MEMBER_NAME = "name"
        private const val DEFAULT_MEMBER_EMAIL = "mail@mail.com"
        private val DEFAULT_MEMBER = MemberData(DEFAULT_MEMBER_NAME, DEFAULT_MEMBER_EMAIL)
        private val DEFAULT_GROUP = GroupData(DEFAULT_GROUP_NAME)
        private val DEFAULT_GROUP_MEMBER = GroupMemberData(DEFAULT_GROUP_NAME, DEFAULT_MEMBER_EMAIL)
    }
}*/
