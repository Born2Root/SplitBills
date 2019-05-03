package org.weilbach.splitbills.data.source

import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.util.EspressoIdlingResource

class MembersRepository(
        val membersRemoteDataSource: MembersDataSource,
        val membersLocalDataSource: MembersDataSource
) : MembersDataSource {

    var cachedMembers: LinkedHashMap<String, Member> = LinkedHashMap()

    var cacheIsDirty = false

    override fun getMembers(callback: MembersDataSource.GetMembersCallback) {
        if (cachedMembers.isNotEmpty() && !cacheIsDirty) {
            callback.onMembersLoaded(ArrayList(cachedMembers.values))
            return
        }

        EspressoIdlingResource.increment()

        if (cacheIsDirty) {
            getMembersFromRemoteDataSource(callback)
        } else {
            membersLocalDataSource.getMembers(object : MembersDataSource.GetMembersCallback {
                override fun onMembersLoaded(members: List<Member>) {
                    refreshCache(members)
                    EspressoIdlingResource.decrement()
                    callback.onMembersLoaded(ArrayList(cachedMembers.values))
                }

                override fun onDataNotAvailable() {
                    getMembersFromRemoteDataSource(callback)
                }
            })
        }
    }

    override fun saveMember(member: Member) {
        cacheAndPerform(member) {
            membersRemoteDataSource.saveMember(it)
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
                membersRemoteDataSource.getMember(memberEmail, object : MembersDataSource.GetMemberCallback {
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
        })
    }

    override fun refreshMembers() {
        cacheIsDirty = true
    }

    override fun deleteAllMembers() {
        membersRemoteDataSource.deleteAllMembers()
        membersLocalDataSource.deleteAllMembers()
        cachedMembers.clear()
    }

    override fun deleteMember(memberEmail: String) {
        membersRemoteDataSource.deleteMember(memberEmail)
        membersLocalDataSource.deleteMember(memberEmail)
        cachedMembers.remove(memberEmail)
    }

    private fun getMembersFromRemoteDataSource(callback: MembersDataSource.GetMembersCallback) {
        membersRemoteDataSource.getMembers(object : MembersDataSource.GetMembersCallback {
            override fun onMembersLoaded(members: List<Member>) {
                refreshCache(members)
                refreshLocalDataSource(members)

                EspressoIdlingResource.decrement()
                callback.onMembersLoaded(ArrayList(cachedMembers.values))
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })
    }

    private fun refreshCache(members: List<Member>) {
        cachedMembers.clear()
        members.forEach {
            cacheAndPerform(it) { }
        }
        cacheIsDirty = false
    }

    private fun refreshLocalDataSource(members: List<Member>) {
        membersLocalDataSource.deleteAllMembers()
        for (member in members) {
            membersLocalDataSource.saveMember(member)
        }
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
        fun getInstance(membersRemoteDataSource: MembersDataSource,
                        membersLocalDataSource: MembersDataSource) {
            INSTANCE ?: synchronized(MembersRepository::class.java) {
                INSTANCE ?: MembersRepository(membersRemoteDataSource, membersLocalDataSource)
                        .also { INSTANCE = it }
            }
        }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}