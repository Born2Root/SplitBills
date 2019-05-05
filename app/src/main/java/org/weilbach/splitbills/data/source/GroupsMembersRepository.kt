package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.GroupMember
import java.util.*

class GroupsMembersRepository(
        private val groupsMembersLocalDataSource: GroupsMembersDataSource
) : GroupsMembersDataSource {

    var cachedGroupsMembers: LinkedList<GroupMember> = LinkedList()

    var cacheIsDirty = false

    override fun deleteAllGroupsMembers() {
        groupsMembersLocalDataSource.deleteAllGroupsMembers()
        cachedGroupsMembers.clear()
    }

    override fun saveGroupMember(groupMember: GroupMember) {
        cacheAndPerform(groupMember) {
            groupsMembersLocalDataSource.saveGroupMember(it)
        }
    }

    override fun refreshGroupsMembers() {
        cacheIsDirty = true
    }

    private inline fun cacheAndPerform(groupMember: GroupMember, perform: (GroupMember) -> Unit) {
        val cachedGroupMember = GroupMember(groupMember.groupName, groupMember.memberEmail)
        cachedGroupsMembers.add(cachedGroupMember)
        perform(cachedGroupMember)
    }

    companion object {
        private var INSTANCE: GroupsMembersRepository? = null

        @JvmStatic
        fun getInstance(groupsMembersLocalDataSource: GroupsMembersDataSource) =
                INSTANCE ?: synchronized(GroupsMembersRepository::class.java) {
                    INSTANCE ?: GroupsMembersRepository(groupsMembersLocalDataSource)
                            .also { INSTANCE = it }
                }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}