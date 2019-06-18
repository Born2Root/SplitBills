package org.weilbach.splitbills.data.source

import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data.Member

class MemberRepository private constructor(
        private val memberLocalDataSource: MemberDataSource
) : MemberDataSource {

    override fun getMemberByEmailSync(memberEmail: String): Member? {
        return memberLocalDataSource.getMemberByEmailSync(memberEmail)
    }

    override fun saveMemberSync(member: Member) {
        memberLocalDataSource.saveMemberSync(member)
    }

    override fun getMemberByEmail(memberEmail: String): LiveData<Member> {
        return memberLocalDataSource.getMemberByEmail(memberEmail)
    }

    override fun deleteMember(memberEmail: String) {
        memberLocalDataSource.deleteMember(memberEmail)
    }

    override fun getAll(): LiveData<List<Member>> {
        return memberLocalDataSource.getAll()
    }

    override fun save(item: Member) {
        memberLocalDataSource.save(item)
    }

    override fun deleteAll() {
        memberLocalDataSource.deleteAll()
    }

    override fun refresh() { }

    companion object {
        private var INSTANCE: MemberRepository? = null

        @JvmStatic
        fun getInstance(membersLocalDataSource: MemberDataSource) =
                INSTANCE ?: synchronized(MemberRepository::class.java) {
                    INSTANCE ?: MemberRepository(membersLocalDataSource)
                            .also { INSTANCE = it }
                }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}