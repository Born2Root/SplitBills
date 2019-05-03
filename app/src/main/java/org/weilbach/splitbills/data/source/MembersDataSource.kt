package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.Member

interface MembersDataSource {

    interface GetMembersCallback {
        fun onMembersLoaded(members: List<Member>)
        fun onDataNotAvailable()
    }

    interface GetMemberCallback {
        fun onMemberLoaded(member: Member)
        fun onDataNotAvailable()
    }

    fun getMembers(callback: GetMembersCallback)

    fun getMember(memberEmail: String, callback: GetMemberCallback)

    fun saveMember(member: Member)

    fun deleteMember(memberEmail: String)

    fun deleteAllMembers()

    fun refreshMembers()
}