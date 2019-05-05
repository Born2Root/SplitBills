package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.util.EspressoIdlingResource

class MembersRepository private constructor(
        private val membersLocalDataSource: MembersDataSource
) : MembersDataSource {

    var cachedMembers: LinkedHashMap<String, Member> = LinkedHashMap()

    var cacheIsDirty = false

    override fun getMembers(callback: MembersDataSource.GetMembersCallback) {
        if (cachedMembers.isNotEmpty() && !cacheIsDirty) {
            callback.onMembersLoaded(ArrayList(cachedMembers.values))
            return
        }

        EspressoIdlingResource.increment()

        membersLocalDataSource.getMembers(object : MembersDataSource.GetMembersCallback {
            override fun onMembersLoaded(members: List<Member>) {
                refreshCache(members)
                EspressoIdlingResource.decrement()
                callback.onMembersLoaded(ArrayList(cachedMembers.values))
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    override fun saveMember(member: Member) {
        cacheAndPerform(member) {
            membersLocalDataSource.saveMember(it)
        }
    }

    override fun getMember(memberEmail: String, callback: MembersDataSource.GetMemberCallback) {
        val memberInCache = getMemberWithEmail(memberEmail)

        if (memberInCache != null) {
            callback.onMemberLoaded(memberInCache)
        }

        EspressoIdlingResource.increment()

        membersLocalDataSource.getMember(memberEmail, object : MembersDataSource.GetMemberCallback {
            override fun onMemberLoaded(member: Member) {
                cacheAndPerform(member) {
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

    private fun refreshCache(members: List<Member>) {
        cachedMembers.clear()
        members.forEach {
            cacheAndPerform(it) { }
        }
        cacheIsDirty = false
    }

    private inline fun cacheAndPerform(member: Member, perform: (Member) -> Unit) {
        val cachedMember = Member(member.name, member.email)
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
}