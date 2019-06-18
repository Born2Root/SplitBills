package org.weilbach.splitbills.data2.source

import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data2.Member

interface MemberDataSource : BaseDataSource<Member> {

    fun deleteMember(memberEmail: String)

    fun getMemberByEmail(memberEmail: String): LiveData<Member>

    fun getMemberByEmailSync(memberEmail: String): Member?

    fun saveMemberSync(member: Member)
}