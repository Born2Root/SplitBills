/*
package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.GroupMemberData
import org.weilbach.splitbills.util.EspressoIdlingResource
import java.util.*
import kotlin.collections.LinkedHashMap

class GroupsMembersRepository(
        private val groupsMembersLocalDataSource: GroupsMembersDataSource
) : GroupsMembersDataSource {

    var cachedGroupsMemberData: LinkedList<GroupMemberData> = LinkedList()
    var cachedGroupsMembersByGroupNameData: LinkedHashMap<String, List<GroupMemberData>> = LinkedHashMap()

    var cacheIsDirty = false
    var cacheByGroupNameIsDirty = false

    override fun deleteAllGroupsMembers() {
        groupsMembersLocalDataSource.deleteAllGroupsMembers()
        cachedGroupsMemberData.clear()
        cachedGroupsMembersByGroupNameData.clear()
    }

    override fun deleteGroupMembersByGroupNameSync(groupName: String) {
        groupsMembersLocalDataSource.deleteGroupMembersByGroupNameSync(groupName)
        cachedGroupsMemberData.clear()
        cachedGroupsMembersByGroupNameData.clear()
    }

    override fun saveGroupMember(groupMemberData: GroupMemberData) {
        cacheAndPerform(groupMemberData) {
            groupsMembersLocalDataSource.saveGroupMember(it)
        }
    }

    override fun saveGroupMemberSync(groupMemberData: GroupMemberData) {
        groupsMembersLocalDataSource.saveGroupMemberSync(groupMemberData)
        cacheAndPerform(groupMemberData) {}
    }

    override fun getGroupMembersByGroupName(groupName: String, callback: GroupsMembersDataSource.GetGroupsMembersCallback) {
        val groupMembersInCache = getGroupMembersWithGroupName(groupName)

        if (groupMembersInCache != null
                && groupMembersInCache.isNotEmpty()
                && !cacheByGroupNameIsDirty) {
            callback.onGroupsMembersLoaded(groupMembersInCache)
            return
        }

        EspressoIdlingResource.increment()

        groupsMembersLocalDataSource.getGroupMembersByGroupName(groupName, object : GroupsMembersDataSource.GetGroupsMembersCallback {
            override fun onGroupsMembersLoaded(groupsMemberData: List<GroupMemberData>) {
                cacheByGroupNameAndPerform(groupsMemberData) {
                    EspressoIdlingResource.decrement()
                    callback.onGroupsMembersLoaded(groupsMemberData)
                }
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getGroupMembersByGroupNameSync(groupName: String): List<GroupMemberData> {
        val groupMembersInCache = getGroupMembersWithGroupName(groupName)

        if (groupMembersInCache != null
                && groupMembersInCache.isNotEmpty()
                && !cacheByGroupNameIsDirty) {
            return groupMembersInCache
        }

        val groupsMembers = groupsMembersLocalDataSource.getGroupMembersByGroupNameSync(groupName)
        if (groupsMembers.isNotEmpty()) {
            cacheByGroupNameAndPerform(groupsMembers) {}
        }

        return  groupsMembers
    }

    override fun refreshGroupsMembers() {
        cacheIsDirty = true
        cacheByGroupNameIsDirty = true
    }

    private inline fun cacheAndPerform(groupMemberData: GroupMemberData, perform: (GroupMemberData) -> Unit) {
        val cachedGroupMember = GroupMemberData(groupMemberData.groupName, groupMemberData.memberEmail)
        cachedGroupsMemberData.add(cachedGroupMember)
        perform(cachedGroupMember)
    }

    private inline fun cacheByGroupNameAndPerform(groupMemberData: List<GroupMemberData>, perform: (List<GroupMemberData>) -> Unit) {
        val cachedGroupMembers = LinkedList<GroupMemberData>()
        groupMemberData.forEach { groupMember ->
            cachedGroupMembers.add(GroupMemberData(groupMember.groupName, groupMember.memberEmail))
        }
        perform(cachedGroupMembers)
    }

    private fun getGroupMembersWithGroupName(groupName: String)
            = cachedGroupsMembersByGroupNameData[groupName]

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
}*/
