package org.weilbach.splitbills.data.source

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.weilbach.splitbills.data.GroupMemberData
import org.weilbach.splitbills.util.eq

class GroupsMembersRepositoryTests {

    private lateinit var groupsMembersRepository: GroupsMembersRepository

    @Mock
    private lateinit var groupsMembersLocalDataSource: GroupsMembersDataSource

    @Before
    fun setupGroupsMembersRepository() {
        MockitoAnnotations.initMocks(this)
        groupsMembersRepository = GroupsMembersRepository.getInstance(groupsMembersLocalDataSource)
    }

    @After
    fun destroyRepositoryInstance() {
        GroupsMembersRepository.destroyInstance()
    }

    @Test
    fun saveGroupMember_savesGroupMemberToServiceApi() {
        val newGroupMember = GroupMemberData(GROUP_NAME, MEMBER_EMAIL)

        groupsMembersRepository.saveGroupMember(newGroupMember)

        verify<GroupsMembersDataSource>(groupsMembersLocalDataSource).saveGroupMember(eq(newGroupMember))
        assertThat(groupsMembersRepository.cachedGroupsMemberData.size, `is`(1))
    }

    @Test
    fun deleteAllGroupsMembers_deleteGroupsMembersToServiceAPIUpdatesCache() {
        with(groupsMembersRepository) {
            val newGroupMember = GroupMemberData(GROUP_NAME, MEMBER_EMAIL)
            saveGroupMember(newGroupMember)
            val newGroupMember2 = GroupMemberData(GROUP_NAME2, MEMBER_EMAIL2)
            saveGroupMember(newGroupMember2)
            val newGroupMember3 = GroupMemberData(GROUP_NAME3, MEMBER_EMAIL3)
            saveGroupMember(newGroupMember3)

            deleteAllGroupsMembers()

            verify(this@GroupsMembersRepositoryTests.groupsMembersLocalDataSource)
                    .deleteAllGroupsMembers()

            assertThat(cachedGroupsMemberData.size, `is`(0))
        }
    }

    companion object {
        private const val GROUP_NAME = "name"
        private const val MEMBER_EMAIL = "mail@mail.com"
        private const val GROUP_NAME2 = "name2"
        private const val MEMBER_EMAIL2 = "mail2@mail.com"
        private const val GROUP_NAME3 = "name3"
        private const val MEMBER_EMAIL3 = "mail3@mail.com"
    }
}