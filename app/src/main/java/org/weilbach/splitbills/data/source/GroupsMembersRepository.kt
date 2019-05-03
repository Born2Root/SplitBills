package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.GroupMember
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.util.EspressoIdlingResource
import java.util.*
import kotlin.collections.LinkedHashMap

class GroupsMembersRepository(
        val groupsMembersLocalDataSource: GroupsMembersDataSource
) : GroupsMembersDataSource {

    var cachedGroupsMembers: LinkedList<GroupMember> = LinkedList()

    var cacheIsDirty = false

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