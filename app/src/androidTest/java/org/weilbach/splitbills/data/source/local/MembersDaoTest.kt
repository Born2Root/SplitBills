package org.weilbach.splitbills.data.source.local

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.data.local.SplitBillsDatabase

@RunWith(AndroidJUnit4::class)
class MembersDaoTest {

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
    fun insertMemberAndGetByEmail() {
        database.memberDao().insertMember(DEFAULT_MEMBER)

        val loaded = database.memberDao().getMemberByEmail(DEFAULT_MEMBER.email)

        assertMember(loaded, DEFAULT_NAME, DEFAULT_EMAIL)
    }

    @Test
    fun insertMemberReplacesOnConflict() {
        database.memberDao().insertMember(DEFAULT_MEMBER)

        val newMember = Member(DEFAULT_NAME, DEFAULT_EMAIL)
        database.memberDao().insertMember(newMember)

        val loaded = database.memberDao().getMemberByEmail(DEFAULT_MEMBER.email)

        assertMember(loaded, DEFAULT_NAME, DEFAULT_EMAIL)
    }

    @Test
    fun insertMemberAndGetMembers() {
        database.memberDao().insertMember(DEFAULT_MEMBER)

        val members = database.memberDao().getMembers()

        assertThat(members.size, `is`(1))
        assertMember(members[0], DEFAULT_NAME, DEFAULT_EMAIL)
    }

    @Test
    fun updateMemberAndGetByEmail() {
        database.memberDao().insertMember(DEFAULT_MEMBER)

        val updatedMember = Member(DEFAULT_NAME, DEFAULT_EMAIL)
        database.memberDao().updateMember(updatedMember)

        val loaded = database.memberDao().getMemberByEmail(DEFAULT_EMAIL)

        assertMember(loaded, DEFAULT_NAME, DEFAULT_EMAIL)
    }

    @Test
    fun deleteMemberByNameAndGettingMembers() {
        database.memberDao().insertMember(DEFAULT_MEMBER)

        database.memberDao().deleteMemberByEmail(DEFAULT_MEMBER.email)

        val members = database.memberDao().getMembers()

        // The list is empty
        assertThat(members.size, `is`(0))
    }

    @Test
    fun deleteMembersAndGettingMembers() {
        database.memberDao().insertMember(DEFAULT_MEMBER)

        database.memberDao().deleteMembers()

        val members = database.memberDao().getMembers()

        assertThat(members.size, `is`(0))
    }

    private fun assertMember(member: Member?, name: String, email: String) {
        assertThat<Member>(member as Member, notNullValue())
        assertThat(member.name, `is`(name))
        assertThat(member.email, `is`(email))
    }

    companion object {
        private val DEFAULT_NAME = "name"
        private val DEFAULT_EMAIL = "mail@mail.com"
        private val DEFAULT_MEMBER = Member(DEFAULT_NAME, DEFAULT_EMAIL)
    }
}