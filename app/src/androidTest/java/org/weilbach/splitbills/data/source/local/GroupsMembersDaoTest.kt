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
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.GroupMember
import org.weilbach.splitbills.data.Member
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

    private fun assertGroupMember(groupMember: GroupMember?, groupName: String, memberEmail: String) {
        assertThat<GroupMember>(groupMember as GroupMember, notNullValue())
        assertThat(groupMember.groupName, `is`(groupName))
        assertThat(groupMember.memberEmail, `is`(memberEmail))
    }

    companion object {
        private const val DEFAULT_GROUP_NAME = "group"
        private const val DEFAULT_MEMBER_NAME = "name"
        private const val DEFAULT_MEMBER_EMAIL = "mail@mail.com"
        private val DEFAULT_MEMBER = Member(DEFAULT_MEMBER_NAME, DEFAULT_MEMBER_EMAIL)
        private val DEFAULT_GROUP = Group(DEFAULT_GROUP_NAME)
        private val DEFAULT_GROUP_MEMBER = GroupMember(DEFAULT_GROUP_NAME, DEFAULT_MEMBER_EMAIL)
    }
}