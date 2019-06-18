/*
package org.weilbach.splitbills.data.source.local

import androidx.room.Room
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.fail
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.weilbach.splitbills.data.MemberData
import org.weilbach.splitbills.data.local.MembersLocalDataSource
import org.weilbach.splitbills.data.local.SplitBillsDatabase
import org.weilbach.splitbills.data.source.MembersDataSource
import org.weilbach.splitbills.utils.SingleExecutors

@RunWith(AndroidJUnit4::class)
@LargeTest
class MembersLocalDataSourceTest {

    private lateinit var localDataSource: MembersLocalDataSource
    private lateinit var database: SplitBillsDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context,
                SplitBillsDatabase::class.java)
                .build()

        MembersLocalDataSource.clearInstance()
        localDataSource = MembersLocalDataSource.getInstance(SingleExecutors(),
                database.memberDao())
    }

    @After
    fun cleanUp() {
        database.close()
        MembersLocalDataSource.clearInstance()
    }

    @Test
    fun testPreConditions() {
        assertNotNull(localDataSource)
    }

    @Test
    fun saveMember_retrievesMember() {
        val newMember = MemberData(NAME, EMAIL)

        with(localDataSource) {
            saveMember(newMember)

            getMember(newMember.email, object : MembersDataSource.GetMemberCallback {
                override fun onMemberLoaded(memberData: MemberData) {
                    assertThat(memberData, `is`(newMember))
                }

                override fun onDataNotAvailable() {
                    fail("Callback error")
                }
            })
        }
    }

    @Test
    fun deleteAllMembers_emptyListOfRetrievedMembers() {
        val callback = mock(MembersDataSource.GetMembersCallback::class.java)
        val newMember = MemberData(NAME, EMAIL)

        with(localDataSource) {
            saveMember(newMember)
            deleteAllMembers()
            getMembers(callback)
        }
        verify<MembersDataSource.GetMembersCallback>(callback).onDataNotAvailable()
        verify<MembersDataSource.GetMembersCallback>(callback, never())
                .onMembersLoaded(org.weilbach.splitbills.util.any<List<MemberData>>())
    }

    @Test
    fun getMembers_retrieveSavedMembers() {
        val newMember1 = MemberData(NAME, EMAIL)
        val newMember2 = MemberData(NAME2, EMAIL2)

        with(localDataSource) {
            saveMember(newMember1)
            saveMember(newMember2)

            getMembers(object : MembersDataSource.GetMembersCallback {
                override fun onMembersLoaded(memberData: List<MemberData>) {
                    assertNotNull(memberData)
                    assertTrue(memberData.size >= 2)

                    var newMember1EmailFound = false
                    var newMember2EmailFound = false
                    for (member in memberData) {
                        if (member.email == newMember1.email) {
                            newMember1EmailFound = true
                        }
                        if (member.email == newMember2.email) {
                            newMember2EmailFound = true
                        }
                    }
                    assertTrue(newMember1EmailFound)
                    assertTrue(newMember2EmailFound)
                }

                override fun onDataNotAvailable() {
                    fail()
                }
            })
        }
    }

    companion object {
        private const val NAME = "name"
        private const val EMAIL = "mail@mail.com"
        private const val NAME2 = "name2"
        private const val EMAIL2 = "mail2@mail.com"
    }
}*/
