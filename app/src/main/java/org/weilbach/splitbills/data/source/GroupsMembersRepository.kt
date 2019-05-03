package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.GroupMember
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.util.EspressoIdlingResource

class GroupsMembersRepository(
        val groupsMembersRemoteDataSource: GroupsMembersDataSource,
        val groupsMembersLocalDataSource: GroupsMembersDataSource
) : GroupsMembersDataSource {

    var cachedGroupsMembers: LinkedHashMap<String, GroupMember> = LinkedHashMap()

    var cacheIsDirty = false

    override fun saveGroupMember(groupMember: GroupMember) {
        cacheAndPerform(groupMember) {
            groupsMembersRemoteDataSource.saveGroupMember(it)
            groupsMembersLocalDataSource.saveGroupMember(it)
        }
    }

    override fun refreshGroupsMembers() {
        cacheIsDirty = true
    }

    private inline fun cacheAndPerform(groupMember: GroupMember, perform: (GroupMember) -> Unit) {
        val cachedGroupMember = GroupMember(groupMember.groupName, groupMember.memberId)
        cachedGroupsMembers[cachedGroupMember.memberId] = cachedGroupMember
        perform(cachedGroupMember)
    }

    companion object {
        private var INSTANCE: GroupsMembersRepository? = null

        @JvmStatic
        fun getInstance(groupsMembersRemoteDataSource: GroupsMembersDataSource,
                        groupsMembersLocalDataSource: GroupsMembersDataSource) {
            INSTANCE ?: synchronized(GroupsMembersRepository::class.java) {
                INSTANCE ?: GroupsMembersRepository(
                        groupsMembersRemoteDataSource,
                        groupsMembersLocalDataSource)
                        .also { INSTANCE = it }
            }
        }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}