package org.weilbach.splitbills.data.source

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.weilbach.splitbills.data.GroupMember
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
        val newGroupMember = GroupMember(GROUP_NAME, MEMBER_EMAIL)

        groupsMembersRepository.saveGroupMember(newGroupMember)

        verify<GroupsMembersDataSource>(groupsMembersLocalDataSource).saveGroupMember(eq(newGroupMember))
        assertThat(groupsMembersRepository.cachedGroupsMembers.size, `is`(1))
    }

    companion object {
        private const val GROUP_NAME = "name"
        private const val MEMBER_EMAIL = "mail@mail.com"
    }
}