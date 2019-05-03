package org.weilbach.splitbills.data.local

import androidx.room.*
import org.weilbach.splitbills.data.Member

@Dao
interface MemberDao {

    @Query("SELECT * FROM members")
    fun getMembers(): List<Member>

    @Query("SELECT * FROM members WHERE email = :memberEmail")
    fun getMemberByEmail(memberEmail: String): Member?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMember(member: Member)

    @Update
    fun updateMember(member: Member): Int

    @Query("DELETE FROM members WHERE email = :memberEmail")
    fun deleteMemberByEmail(memberEmail: String): Int

    @Query("DELETE FROM members")
    fun deleteMembers()
}