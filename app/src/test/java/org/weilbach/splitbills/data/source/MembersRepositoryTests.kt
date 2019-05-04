package org.weilbach.splitbills.data.source

import com.google.common.collect.Lists
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.util.any
import org.weilbach.splitbills.util.capture
import org.weilbach.splitbills.util.eq

class MembersRepositoryTests {

    private lateinit var membersRepository: MembersRepository

    @Mock
    private lateinit var membersLocalDataSource: MembersDataSource
    @Mock
    private lateinit var getMemberCallback: MembersDataSource.GetMemberCallback
    @Mock
    private lateinit var getMembersCallback: MembersDataSource.GetMembersCallback

    @Captor
    private lateinit var getMembersCallbackCaptor: ArgumentCaptor<MembersDataSource.GetMembersCallback>
    @Captor
    private lateinit var getMemberCallbackCaptor: ArgumentCaptor<MembersDataSource.GetMemberCallback>

    @Before
    fun setupMembersRepository() {
        MockitoAnnotations.initMocks(this)
        membersRepository = MembersRepository.getInstance(membersLocalDataSource)
    }

    @After
    fun destroyRepositoryInstance() {
        MembersRepository.destroyInstance()
    }

    @Test
    fun getMembers_repositoryCachesAfterFirstApiCall() {
        twoMembersLoadCallsToRepository(getMembersCallback)
        verify<MembersDataSource>(membersLocalDataSource).getMembers(any())
    }

    @Test
    fun getMembers_requestsAllMembersFromLocalDataSource() {
        membersRepository.getMembers(getMembersCallback)

        verify<MembersDataSource>(membersLocalDataSource).getMembers(any())
    }

    @Test
    fun saveMember_savesMemberToServiceAPI() {
        val newMember = Member(MEMBER_NAME1, MEMBER_EMAIL1)

        membersRepository.saveMember(newMember)

        verify<MembersDataSource>(membersLocalDataSource).saveMember(eq(newMember))
        assertThat(membersRepository.cachedMembers.size, `is`(1))
    }

    @Test
    fun getMember_requestsSingleMemberFromLocalDataSource() {
        membersRepository.getMember(MEMBER_EMAIL1, getMemberCallback)
        verify(membersLocalDataSource).getMember(eq(MEMBER_EMAIL1), any())
    }

    @Test
    fun deleteAllMembers_deleteMembersToServiceAPIUpdatesCache() {
        with(membersRepository) {
            val newMember = Member(MEMBER_NAME1, MEMBER_EMAIL1)
            saveMember(newMember)
            val newMember2 = Member(MEMBER_NAME2, MEMBER_EMAIL2)
            saveMember(newMember2)
            val newMember3 = Member(MEMBER_NAME3, MEMBER_EMAIL3)
            saveMember(newMember3)

            deleteAllMembers()

            verify(this@MembersRepositoryTests.membersLocalDataSource)
                    .deleteAllMembers()

            assertThat(cachedMembers.size, `is`(0))
        }
    }

    @Test
    fun deleteMember_deleteMemberToServiceAPIRemovedFromCache() {
        with(membersRepository) {
            val newMember = Member(MEMBER_NAME1, MEMBER_NAME1)
            saveMember(newMember)
            assertThat(cachedMembers.containsKey(newMember.name), `is`(true))

            deleteMember(newMember.name)

            verify(this@MembersRepositoryTests.membersLocalDataSource)
                    .deleteMember(newMember.name)

            assertThat(cachedMembers.containsKey(newMember.name), `is`(false))
        }
    }

    @Test
    fun getGroupsWithDirtyCache_GroupsAreRetrievedFromLocal() {
        with(membersRepository) {
            refreshMembers()
            getMembers(getMembersCallback)
        }

        setMembersAvailable(membersLocalDataSource, MEMBERS)
        verify(getMembersCallback).onMembersLoaded(MEMBERS)
    }

    private fun twoMembersLoadCallsToRepository(callback: MembersDataSource.GetMembersCallback) {
        membersRepository.getMembers(callback)
        verify(membersLocalDataSource).getMembers(capture(getMembersCallbackCaptor))
        getMembersCallbackCaptor.value.onMembersLoaded(MEMBERS)
        membersRepository.getMembers(callback)
    }

    @Test
    fun getMembersWithDataSourceUnavailable_firesOnDataUnavailable() {
        membersRepository.getMembers(getMembersCallback)

        setMembersNotAvailable(membersLocalDataSource)

        verify(getMembersCallback).onDataNotAvailable()
    }

    @Test
    fun getMemberWithDataSourceUnavailable_firesOnDataUnavailable() {
        val memberEmail = "invalid@mail.com"

        membersRepository.getMember(memberEmail, getMemberCallback)

        setMemberNotAvailable(membersLocalDataSource, memberEmail)

        verify(getMemberCallback).onDataNotAvailable()
    }

    private fun setMembersNotAvailable(dataSource: MembersDataSource) {
        verify(dataSource).getMembers(capture(getMembersCallbackCaptor))
        getMembersCallbackCaptor.value.onDataNotAvailable()
    }

    private fun setMembersAvailable(dataSource: MembersDataSource, members: List<Member>) {
        verify(dataSource).getMembers(capture(getMembersCallbackCaptor))
        getMembersCallbackCaptor.value.onMembersLoaded(members)
    }

    private fun setMemberNotAvailable(dataSource: MembersDataSource, memberEmail: String) {
        verify(dataSource).getMember(eq(memberEmail), capture(getMemberCallbackCaptor))
        getMemberCallbackCaptor.value.onDataNotAvailable()
    }

    companion object {
        private const val MEMBER_NAME1 = "name"
        private const val MEMBER_EMAIL1 = "mail@mail.com"
        private const val MEMBER_NAME2 = "name2"
        private const val MEMBER_EMAIL2 = "mail2@mail.com"
        private const val MEMBER_NAME3 = "name3"
        private const val MEMBER_EMAIL3 = "mail3@mail.com"

        private val MEMBERS = Lists.newArrayList(
                Member(MEMBER_NAME1, MEMBER_EMAIL1),
                Member(MEMBER_NAME2, MEMBER_EMAIL2),
                Member(MEMBER_NAME3, MEMBER_EMAIL3)
        )
    }
}