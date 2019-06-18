/*
package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.MemberData

interface MembersDataSource {

    interface GetMembersCallback {
        fun onMembersLoaded(memberData: List<MemberData>)
        fun onDataNotAvailable()
    }

    interface GetMemberCallback {
        fun onMemberLoaded(memberData: MemberData)
        fun onDataNotAvailable()
    }

    fun getMembers(callback: GetMembersCallback)

    fun getMember(memberEmail: String, callback: GetMemberCallback)

    fun getMemberSync(memberEmail: String): MemberData?

    fun saveMember(memberData: MemberData)

    fun saveMemberSync(memberData: MemberData)

    fun deleteMember(memberEmail: String)

    fun deleteAllMembers()

    fun refreshMembers()
}*/
