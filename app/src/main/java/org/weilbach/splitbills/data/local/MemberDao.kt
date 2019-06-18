package org.weilbach.splitbills.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import org.weilbach.splitbills.data.Member

@Dao
interface MemberDao : BaseDao<Member> {

    @Query("SELECT * FROM members")
    fun getMembers(): LiveData<List<Member>>

    @Query("SELECT * FROM members WHERE email = :memberEmail")
    fun getMemberByEmail(memberEmail: String): LiveData<Member>

    @Query("SELECT * FROM members WHERE email = :memberEmail")
    fun getMemberByEmailSync(memberEmail: String): Member?

    @Query("DELETE FROM members WHERE email = :memberEmail")
    fun deleteMemberByEmail(memberEmail: String): Int

    @Query("DELETE FROM members")
    fun deleteMembers()
}