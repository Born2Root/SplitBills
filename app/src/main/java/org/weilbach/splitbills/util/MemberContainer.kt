package org.weilbach.splitbills.util

import org.weilbach.splitbills.data.Member

class MemberContainer {

    val members: HashMap<String, Member> = HashMap()

    var size = members.size
        get() = members.size
        private set

    fun contains(member: Member): Boolean {
        if (members.contains(member.email)) {
            return true
        }
        return false
    }

    fun add(member: Member): Boolean {
        if (contains(member)) {
            return false
        }
        members[member.email] = member
        return true
    }

    fun get(email: String): Member? {
        return members[email]
    }

    fun forEach(operation: (Member) -> Unit) {
        members.forEach {
            operation(it.value)
        }
    }
}