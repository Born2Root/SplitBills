/*
package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.MemberData
import org.weilbach.splitbills.util.EspressoIdlingResource

class MembersRepository private constructor(
        private val membersLocalDataSource: MembersDataSource
) : MembersDataSource {

    var cachedMembers: LinkedHashMap<String, MemberData> = LinkedHashMap()

    var cacheIsDirty = false

    override fun getMembers(callback: MembersDataSource.GetMembersCallback) {
        if (cachedMembers.isNotEmpty() && !cacheIsDirty) {
            callback.onMembersLoaded(ArrayList(cachedMembers.values))
            return
        }

        EspressoIdlingResource.increment()

        membersLocalDataSource.getMembers(object : MembersDataSource.GetMembersCallback {
            override fun onMembersLoaded(memberData: List<MemberData>) {
                refreshCache(memberData)
                EspressoIdlingResource.decrement()
                callback.onMembersLoaded(ArrayList(cachedMembers.values))
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun saveMember(memberData: MemberData) {
        cacheAndPerform(memberData) {
            membersLocalDataSource.saveMember(it)
        }
    }

    override fun saveMemberSync(memberData: MemberData) {
        membersLocalDataSource.saveMemberSync(memberData)
        cacheAndPerform(memberData) {}
    }

    override fun getMember(memberEmail: String, callback: MembersDataSource.GetMemberCallback) {
        val memberInCache = getMemberWithEmail(memberEmail)

        if (memberInCache != null) {
            callback.onMemberLoaded(memberInCache)
            return
        }

        EspressoIdlingResource.increment()

        membersLocalDataSource.getMember(memberEmail, object : MembersDataSource.GetMemberCallback {
            override fun onMemberLoaded(memberData: MemberData) {
                cacheAndPerform(memberData) {
                    EspressoIdlingResource.decrement()
                    callback.onMemberLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getMemberSync(memberEmail: String): MemberData? {
        val memberInCache = getMemberWithEmail(memberEmail)

        if (memberInCache != null) {
            return memberInCache
        }

        val member = membersLocalDataSource.getMemberSync(memberEmail)
        member?.let {
            cacheAndPerform(it) {}
        }
        return member
    }

    override fun refreshMembers() {
        cacheIsDirty = true
    }

    override fun deleteAllMembers() {
        membersLocalDataSource.deleteAllMembers()
        cachedMembers.clear()
    }

    override fun deleteMember(memberEmail: String) {
        membersLocalDataSource.deleteMember(memberEmail)
        cachedMembers.remove(memberEmail)
    }

    private fun refreshCache(memberData: List<MemberData>) {
        cachedMembers.clear()
        memberData.forEach {
            cacheAndPerform(it) { }
        }
        cacheIsDirty = false
    }

    private inline fun cacheAndPerform(memberData: MemberData, perform: (MemberData) -> Unit) {
        val cachedMember = MemberData(memberData.name, memberData.email)
        cachedMembers[cachedMember.email] = cachedMember
        perform(cachedMember)
    }

    private fun getMemberWithEmail(email: String) = cachedMembers[email]

    companion object {
        private var INSTANCE: MembersRepository? = null

        @JvmStatic
        fun getInstance(membersLocalDataSource: MembersDataSource) =
                INSTANCE ?: synchronized(MembersRepository::class.java) {
                    INSTANCE ?: MembersRepository(membersLocalDataSource)
                            .also { INSTANCE = it }
                }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}*/
