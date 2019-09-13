package org.weilbach.splitbills.data.local

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.data.source.MemberDataSource
import org.weilbach.splitbills.util.AppExecutors

class MemberLocalDataSource private constructor(
        private val appExecutors: AppExecutors,
        private val memberDao: MemberDao
) : MemberDataSource {

    override fun getMemberByEmailSync(memberEmail: String): Member? {
        return memberDao.getMemberByEmailSync(memberEmail)
    }

    override fun saveMemberSync(member: Member) {
        memberDao.insert(member)
    }

    override fun getMemberByEmail(memberEmail: String): LiveData<Member> {
        return memberDao.getMemberByEmail(memberEmail)
    }

    override fun deleteMember(memberEmail: String) {
        appExecutors.diskIO.execute { memberDao.deleteMemberByEmail(memberEmail) }
    }

    override fun getAll(): LiveData<List<Member>> {
        return memberDao.getMembers()
    }

    override fun save(item: Member) {
        appExecutors.diskIO.execute { memberDao.insert(item) }
    }

    override fun deleteAll() {
        appExecutors.diskIO.execute { memberDao.deleteMembers() }
    }

    override fun refresh() {}

    companion object {
        private var INSTANCE: MemberLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, membersDao: MemberDao): MemberLocalDataSource {
            if (INSTANCE == null) {
                synchronized(MemberLocalDataSource::javaClass) {
                    INSTANCE = MemberLocalDataSource(appExecutors, membersDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}