/*
package org.weilbach.splitbills.data.local

import androidx.annotation.VisibleForTesting
import org.weilbach.splitbills.data.MemberData
import org.weilbach.splitbills.data.source.MembersDataSource
import org.weilbach.splitbills.util.AppExecutors

class MembersLocalDataSource private constructor(
        private val appExecutors: AppExecutors,
        private val membersDao: MemberDao
) : MembersDataSource {

    override fun getMembers(callback: MembersDataSource.GetMembersCallback) {
        appExecutors.diskIO.execute {
            val members = membersDao.getMembers()
            appExecutors.mainThread.execute {
                if (members.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onMembersLoaded(members)
                }
            }
        }
    }

    override fun getMember(memberEmail: String, callback: MembersDataSource.GetMemberCallback) {
        appExecutors.diskIO.execute {
            val member = membersDao.getMemberByEmail(memberEmail)
            appExecutors.mainThread.execute {
                if (member != null) {
                    callback.onMemberLoaded(member)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun getMemberSync(memberEmail: String): MemberData? {
        return membersDao.getMemberByEmail(memberEmail)
    }

    override fun saveMember(memberData: MemberData) {
        appExecutors.diskIO.execute { membersDao.insertMember(memberData) }
    }

    override fun saveMemberSync(memberData: MemberData) {
        membersDao.insertMember(memberData)
    }

    override fun deleteMember(memberEmail: String) {
        appExecutors.diskIO.execute { membersDao.deleteMemberByEmail(memberEmail) }
    }

    override fun deleteAllMembers() {
        appExecutors.diskIO.execute { membersDao.deleteMembers() }
    }

    override fun refreshMembers() {
        // Handled from {@link MembersRepository}
    }

    companion object {
        private var INSTANCE: MembersLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, membersDao: MemberDao): MembersLocalDataSource {
            if (INSTANCE == null) {
                synchronized(MembersLocalDataSource::javaClass) {
                    INSTANCE = MembersLocalDataSource(appExecutors, membersDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}*/
